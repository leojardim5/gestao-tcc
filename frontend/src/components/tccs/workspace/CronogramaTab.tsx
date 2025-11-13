"use client";

import { useEffect, useMemo, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  DndContext,
  DragEndEvent,
  DragStartEvent,
  useDroppable,
  useDraggable,
} from "@dnd-kit/core";
import { CSS } from "@dnd-kit/utilities";
import { Button } from "@/components/ui/Button";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/Modal";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Badge } from "@/components/ui/Badge";
import { Spinner } from "@/components/ui/Spinner";
import {
  PapelUsuario,
  CronogramaEtapa,
  CronogramaEtapaCreateRequest,
  StatusCronogramaEtapa,
} from "@/interfaces";
import {
  createCronogramaEtapa,
  listCronogramaEtapas,
  updateCronogramaEtapaStatus,
  deleteCronogramaEtapa,
} from "@/services/cronograma";
import { formatDate, formatDateTime } from "@/utils/date";
import { handleApiError } from "@/services/api";
import { useToast } from "@/hooks/useToast";
import { ObservationDialog } from "./ObservationDialog";
import { cn } from "@/lib/utils";
import { GripVertical, Info, Plus, Trash2 } from "lucide-react";
import { useTccNotificationsStore } from "@/store/tccNotifications";
import { differenceInCalendarDays, isAfter, parseISO } from "date-fns";

const etapaSchema = z.object({
  nome: z.string().min(3, "Informe um nome com pelo menos 3 caracteres."),
  dataFim: z.string().min(1, "Informe a data de término."),
  observacao: z
    .string()
    .max(2000, "Observação deve ter até 2000 caracteres.")
    .optional(),
});

type EtapaFormValues = z.infer<typeof etapaSchema>;
type KanbanColumnId = "todo" | "doing" | "done";

interface CronogramaTabProps {
  tccId: string;
  userRole: PapelUsuario | null;
}

interface PendingAction {
  etapa: CronogramaEtapa;
  origin: KanbanColumnId;
  destination: KanbanColumnId;
  targetStatus: StatusCronogramaEtapa;
  requiresObservation: boolean;
  dialog: ObservationDialogConfig;
  successMessage: string;
}

interface ObservationDialogConfig {
  title: string;
  description?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  requiresObservation?: boolean;
}

const COLUMN_CONFIG: Array<{
  id: KanbanColumnId;
  title: string;
  subtitle: string;
  emptyState: string;
}> = [
  {
    id: "todo",
    title: "A Fazer",
    subtitle: "Etapas aguardando execução",
    emptyState: "Sem etapas pendentes no momento.",
  },
  {
    id: "doing",
    title: "Fazendo",
    subtitle: "Etapas em execução pelo aluno",
    emptyState: "Nenhuma etapa em execução no momento.",
  },
  {
    id: "done",
    title: "Concluído",
    subtitle: "Etapas finalizadas pelo aluno",
    emptyState: "Ainda não há etapas concluídas.",
  },
];

const statusToColumn: Record<StatusCronogramaEtapa, KanbanColumnId> = {
  [StatusCronogramaEtapa.PENDENTE]: "todo",
  [StatusCronogramaEtapa.ATRASADA]: "todo",
  [StatusCronogramaEtapa.EM_ANDAMENTO]: "doing",
  [StatusCronogramaEtapa.CONCLUIDO]: "done",
};

const statusVariant: Record<
  StatusCronogramaEtapa,
  "default" | "secondary" | "destructive" | "warning" | "success"
> = {
  [StatusCronogramaEtapa.PENDENTE]: "warning",
  [StatusCronogramaEtapa.EM_ANDAMENTO]: "secondary",
  [StatusCronogramaEtapa.CONCLUIDO]: "success",
  [StatusCronogramaEtapa.ATRASADA]: "destructive",
};

const statusLabel: Record<StatusCronogramaEtapa, string> = {
  [StatusCronogramaEtapa.PENDENTE]: "Pendente",
  [StatusCronogramaEtapa.EM_ANDAMENTO]: "Em andamento",
  [StatusCronogramaEtapa.CONCLUIDO]: "Concluída",
  [StatusCronogramaEtapa.ATRASADA]: "Atrasada",
};

const getColumnFromStatus = (status: StatusCronogramaEtapa): KanbanColumnId =>
  statusToColumn[status] ?? "todo";

const KanbanColumn = ({
  columnId,
  title,
  subtitle,
  itemCount,
  children,
  isDropDisabled = false,
  extraAction,
}: {
  columnId: KanbanColumnId;
  title: string;
  subtitle: string;
  itemCount: number;
  children: React.ReactNode;
  isDropDisabled?: boolean;
  extraAction?: React.ReactNode;
}) => {
  const { setNodeRef, isOver } = useDroppable({
    id: columnId,
    disabled: isDropDisabled,
  });

  return (
    <div
      ref={setNodeRef}
      className={cn(
        "flex h-full min-h-[280px] max-h-[calc(100vh-280px)] flex-col rounded-lg border border-slate-300 bg-slate-200 p-3 transition-colors",
        isOver && "border-primary bg-primary/10",
      )}
    >
      <header className="mb-3 flex items-start justify-between gap-2">
        <div>
          <h3 className="text-xs font-semibold uppercase tracking-wide">
            {title}
          </h3>
          <p className="text-[11px] text-muted-foreground">{subtitle}</p>
        </div>
        <div className="flex items-center gap-2">
          {extraAction}
          <span className="inline-flex h-5 min-w-[1.5rem] items-center justify-center rounded-full bg-foreground/10 px-1.5 text-[11px] font-semibold text-foreground">
            {itemCount}
          </span>
        </div>
      </header>
      <div className="flex-1 space-y-2 overflow-y-auto pr-2">{children}</div>
    </div>
  );
};

const KanbanCard = ({
  etapa,
  columnId,
  canDrag,
  onStart,
  onDeliver,
  onRequestChanges,
  onDelete,
  isProcessing,
}: {
  etapa: CronogramaEtapa;
  columnId: KanbanColumnId;
  canDrag: boolean;
  onStart?: () => void;
  onDeliver?: () => void;
  onRequestChanges?: () => void;
  onDelete?: () => void;
  isProcessing: boolean;
}) => {
  const { attributes, listeners, setNodeRef, transform, isDragging } =
    useDraggable({
      id: etapa.id,
      data: { etapa, columnId },
      disabled: !canDrag,
    });

  const style = {
    transform: transform ? CSS.Translate.toString(transform) : undefined,
    opacity: isDragging ? 0.5 : 1,
  };

  const endDate = etapa.dataFim ? parseISO(etapa.dataFim) : null;
  const today = new Date();
  const isCompleted = etapa.status === StatusCronogramaEtapa.CONCLUIDO;
  const isOverdue = endDate ? !isCompleted && isAfter(today, endDate) : false;
  const daysToDeadline = endDate ? differenceInCalendarDays(endDate, today) : null;
  const isUpcomingDeadline =
    !isCompleted && daysToDeadline !== null && daysToDeadline >= 0 && daysToDeadline <= 7;

  return (
    <div
      ref={setNodeRef}
      style={style}
      className={cn(
        "group relative flex flex-col gap-2 rounded-lg border border-slate-300 bg-slate-200 p-3 shadow-sm transition-transform",
        isProcessing && "opacity-75",
      )}
    >
      {isProcessing && (
        <div className="absolute inset-0 z-10 flex items-center justify-center rounded-lg bg-slate-200/90 backdrop-blur-sm">
          <Spinner className="h-5 w-5 text-primary" />
        </div>
      )}
      <div className="flex items-start gap-3">
        <button
          type="button"
          className={cn(
            "mt-1 inline-flex h-6 w-6 cursor-grab items-center justify-center rounded border border-dashed border-muted-foreground/40 text-muted-foreground transition-colors",
            canDrag
              ? "hover:border-primary hover:text-primary"
              : "cursor-not-allowed opacity-40",
          )}
          {...listeners}
          {...attributes}
          disabled={!canDrag}
          aria-label="Arrastar etapa"
        >
          <GripVertical className="h-3.5 w-3.5" />
        </button>
        <div className="flex-1 space-y-1.5">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <h4 className="text-sm font-semibold leading-tight line-clamp-1">{etapa.nome}</h4>
            <Badge variant={getBadgeVariant(columnId, etapa.status)}>
              {getBadgeLabel(columnId, etapa.status)}
            </Badge>
          </div>
          <div className="flex flex-wrap items-center gap-2">
            {isOverdue && (
              <Badge variant="destructive" className="text-[11px]">
                Atraso
              </Badge>
            )}
            {isUpcomingDeadline && (
              <Badge variant="warning" className="text-[11px]">
                Próximo prazo
              </Badge>
            )}
          </div>
          <div className="space-y-0.5 text-xs text-muted-foreground">
            <p>
              <span className="font-semibold text-foreground">Prazo:</span>{" "}
              {formatDate(etapa.dataFim)}
            </p>
            {etapa.concluidoEm && (
              <p>
                <span className="font-semibold text-foreground">Concluída:</span>{" "}
                {formatDateTime(etapa.concluidoEm)}
              </p>
            )}
            <p>
              <span className="font-semibold text-foreground">Criada em:</span>{" "}
              {formatDateTime(etapa.criadoEm)}
            </p>
          </div>
          {etapa.observacao && (
            <div className="flex items-start gap-2 rounded-md border border-muted-foreground/20 bg-muted/40 p-2 text-[11px] text-muted-foreground">
              <Info className="mt-0.5 h-3 w-3 text-muted-foreground/70" />
              <p className="line-clamp-3">{etapa.observacao}</p>
            </div>
          )}
          <div className="flex flex-wrap items-center gap-1.5">
            {onStart && (
              <Button
                size="sm"
                variant="secondary"
                onClick={onStart}
                disabled={isProcessing}
              >
                Iniciar etapa
              </Button>
            )}
            {onDeliver && (
              <Button
                size="sm"
                variant="default"
                onClick={onDeliver}
                disabled={isProcessing}
              >
                Concluir etapa
              </Button>
            )}
            {onRequestChanges && (
              <Button
                size="sm"
                variant="secondary"
                onClick={onRequestChanges}
                disabled={isProcessing}
              >
                Solicitar ajustes
              </Button>
            )}
            {onDelete && (
              <Button
                size="sm"
                variant="ghost"
                onClick={onDelete}
                disabled={isProcessing}
                className="text-destructive hover:text-destructive hover:bg-destructive/10"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

const getBadgeLabel = (columnId: KanbanColumnId, status: StatusCronogramaEtapa) => {
  if (columnId === "doing") {
    return "Fazendo";
  }
  if (columnId === "done") {
    return "Concluída";
  }
  return statusLabel[status];
};

const getBadgeVariant = (columnId: KanbanColumnId, status: StatusCronogramaEtapa) => {
  if (columnId === "doing") {
    return "secondary";
  }
  if (columnId === "done") {
    return "success";
  }
  return statusVariant[status];
};

export const CronogramaTab = ({ tccId, userRole }: CronogramaTabProps) => {
  const canDropInColumn = (columnId: KanbanColumnId) => {
    if (!userRole) return false;
    if (userRole === PapelUsuario.ALUNO) {
      return columnId === "doing" || columnId === "done";
    }
    if (userRole === PapelUsuario.ORIENTADOR || userRole === PapelUsuario.COORDENADOR) {
      return true;
    }
    return false;
  };
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const setNotificationCount = useTccNotificationsStore((state) => state.setCountForTcc);

  const canManage = userRole === PapelUsuario.ORIENTADOR || userRole === PapelUsuario.COORDENADOR;

  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [processingId, setProcessingId] = useState<string | null>(null);
  const [activeCardId, setActiveCardId] = useState<string | null>(null);

  const [pendingAction, setPendingAction] = useState<PendingAction | null>(null);
  const [observationDialogOpen, setObservationDialogOpen] = useState(false);
  const [observationDialogConfig, setObservationDialogConfig] =
    useState<ObservationDialogConfig>({
      title: "Adicionar observação",
      description: "Descreva observações importantes (opcional).",
      confirmLabel: "Confirmar",
      cancelLabel: "Cancelar",
      requiresObservation: false,
    });
  const [isDialogSubmitting, setIsDialogSubmitting] = useState(false);

  const resetActionState = () => {
    setPendingAction(null);
    setObservationDialogOpen(false);
    setIsDialogSubmitting(false);
    setProcessingId(null);
  };

  const { data: etapas = [], isLoading, isFetching } = useQuery<CronogramaEtapa[]>(
    {
      queryKey: ["tccs", tccId, "cronograma"],
      queryFn: () => listCronogramaEtapas(tccId),
    },
  );

  const form = useForm<EtapaFormValues>({
    resolver: zodResolver(etapaSchema),
    defaultValues: {
      nome: "",
      dataFim: "",
      observacao: "",
    },
  });

  const handleMutationError = (error: unknown) => {
    const { message } = handleApiError(error);
    showToast(message || "Ocorreu um erro", "error");
  };

  const invalidateCronograma = () => {
    queryClient.invalidateQueries({ queryKey: ["tccs", tccId, "cronograma"] });
  };

  const { mutate: createEtapa, isPending: isCreating } = useMutation({
    mutationFn: (payload: CronogramaEtapaCreateRequest) =>
      createCronogramaEtapa(tccId, payload),
    onSuccess: () => {
      showToast("Etapa criada com sucesso!", "success");
      setIsCreateDialogOpen(false);
      form.reset();
      invalidateCronograma();
    },
    onError: handleMutationError,
  });

  interface UpdateStatusVariables {
    etapaId: string;
    status: StatusCronogramaEtapa;
    observacao?: string;
    successMessage: string;
  }

  const { mutate: updateStatusMutate } = useMutation({
    mutationFn: ({ etapaId, status, observacao }: UpdateStatusVariables) =>
      updateCronogramaEtapaStatus(tccId, etapaId, { status, observacao }),
    onSuccess: (_, variables) => {
      showToast(variables.successMessage, "success");
      invalidateCronograma();
    },
    onError: handleMutationError,
    onSettled: resetActionState,
  });

  const { mutate: deleteEtapaMutate } = useMutation({
    mutationFn: (etapaId: string) => deleteCronogramaEtapa(tccId, etapaId),
    onSuccess: () => {
      showToast("Etapa deletada com sucesso.", "success");
      invalidateCronograma();
    },
    onError: handleMutationError,
  });

  const groupedEtapas = useMemo(() => {
    const buckets: Record<KanbanColumnId, CronogramaEtapa[]> = {
      todo: [],
      doing: [],
      done: [],
    };

    etapas.forEach((etapa) => {
      const column = getColumnFromStatus(etapa.status);
      buckets[column].push(etapa);
    });

    (Object.keys(buckets) as KanbanColumnId[]).forEach((column) => {
      buckets[column].sort(
        (a, b) =>
          new Date(a.criadoEm).getTime() - new Date(b.criadoEm).getTime(),
      );
    });

    return buckets;
  }, [etapas]);

  useEffect(() => {
    if (userRole === PapelUsuario.ALUNO) {
      setNotificationCount(tccId, groupedEtapas.todo.length);
    } else {
      setNotificationCount(tccId, 0);
    }
  }, [groupedEtapas.todo.length, setNotificationCount, tccId, userRole]);

  const onSubmit = (values: EtapaFormValues) => {
    const payload: CronogramaEtapaCreateRequest = {
      nome: values.nome.trim(),
      dataInicio: new Date().toISOString().split("T")[0], // Data atual como data de início
      dataFim: values.dataFim,
      observacao: values.observacao?.trim() || undefined,
    };
    createEtapa(payload);
  };

  const canDragEtapa = (etapa: CronogramaEtapa, columnId: KanbanColumnId) => {
    if (processingId && processingId === etapa.id) return false;
    if (!userRole) return false;

    if (userRole === PapelUsuario.ALUNO) {
      return columnId === "todo" || columnId === "doing";
    }

    if (userRole === PapelUsuario.ORIENTADOR || userRole === PapelUsuario.COORDENADOR) {
      return true;
    }

    return false;
  };

  const resolvePendingAction = (
    origin: KanbanColumnId,
    destination: KanbanColumnId,
    etapa: CronogramaEtapa,
  ): PendingAction | null => {
    if (!userRole || origin === destination) {
      return null;
    }

    const actionFor = (
      targetStatus: StatusCronogramaEtapa,
      dialog: ObservationDialogConfig,
      successMessage: string,
      requiresObservation = false,
    ): PendingAction => ({
      etapa,
      origin,
      destination,
      targetStatus,
      dialog: { ...dialog, requiresObservation },
      successMessage,
      requiresObservation,
    });

    if (userRole === PapelUsuario.ALUNO) {
      if (origin === "todo" && destination === "doing") {
        return actionFor(
          StatusCronogramaEtapa.EM_ANDAMENTO,
          {
            title: "Mover para Fazendo",
            description:
              "Confirme o início desta etapa. Caso deseje, adicione observações para contextualizar o orientador.",
            confirmLabel: "Iniciar etapa",
            cancelLabel: "Cancelar",
          },
          "Etapa marcada como em andamento.",
        );
      }

      if (origin === "doing" && destination === "done") {
        return actionFor(
          StatusCronogramaEtapa.CONCLUIDO,
          {
            title: "Concluir etapa",
            description:
              "Deseja marcar esta etapa como concluída? O orientador será notificado. Adicione observações se achar necessário.",
            confirmLabel: "Concluir etapa",
            cancelLabel: "Cancelar",
          },
          "Etapa concluída e orientador notificado.",
        );
      }

      return null;
    }

    const isOrientadorLike =
      userRole === PapelUsuario.ORIENTADOR || userRole === PapelUsuario.COORDENADOR;

    if (isOrientadorLike) {
      if (destination === "todo") {
        return actionFor(
          StatusCronogramaEtapa.PENDENTE,
          {
            title: "Enviar para A Fazer",
            description:
              "Você está retornando esta etapa para A Fazer. Informe orientações ao aluno, se necessário.",
            confirmLabel: "Mover para A Fazer",
            cancelLabel: "Cancelar",
          },
          "Etapa movida para A Fazer.",
          origin === "done",
        );
      }

      if (destination === "doing") {
        const requiresObservation = origin === "done";
        return actionFor(
          StatusCronogramaEtapa.EM_ANDAMENTO,
          {
            title: "Mover para Fazendo",
            description: requiresObservation
              ? "Descreva os ajustes solicitados para que o aluno retome esta etapa."
              : "Confirme a movimentação desta etapa para Fazendo.",
            confirmLabel: requiresObservation ? "Solicitar ajustes" : "Mover para Fazendo",
            cancelLabel: "Cancelar",
            requiresObservation,
          },
          requiresObservation
            ? "Solicitação de ajustes enviada ao aluno."
            : "Etapa movida para Fazendo.",
          requiresObservation,
        );
      }

      if (destination === "done") {
        return actionFor(
          StatusCronogramaEtapa.CONCLUIDO,
          {
            title: "Marcar como concluída",
            description:
              "Confirme a conclusão desta etapa. Adicione observações caso deseje registrar orientações finais.",
            confirmLabel: "Marcar como concluída",
            cancelLabel: "Cancelar",
          },
          "Etapa marcada como concluída.",
        );
      }
    }

    return null;
  };

  const openObservationDialog = (action: PendingAction) => {
    setPendingAction(action);
    setObservationDialogConfig(action.dialog);
    setObservationDialogOpen(true);
  };

  const handleDragStart = (event: DragStartEvent) => {
    setActiveCardId(event.active.id as string);
  };

  const handleDragEnd = (event: DragEndEvent) => {
    setActiveCardId(null);
    const { active, over } = event;
    if (!over) return;

    const origin = active.data.current?.columnId as KanbanColumnId | undefined;
    const etapa = active.data.current?.etapa as CronogramaEtapa | undefined;
    const destination = over.id as KanbanColumnId;

    if (!origin || !destination || !etapa) return;

    const action = resolvePendingAction(origin, destination, etapa);
    if (!action) {
      showToast("Você não tem permissão para mover esta etapa.", "warning");
      return;
    }

    openObservationDialog(action);
  };

  const executePendingAction = (observation: string) => {
    if (!pendingAction) return;
    setIsDialogSubmitting(true);
    setProcessingId(pendingAction.etapa.id);

    if (pendingAction.requiresObservation && !observation) {
      showToast("Informe uma observação para prosseguir.", "warning");
      setIsDialogSubmitting(false);
      setProcessingId(null);
      return;
    }

    updateStatusMutate({
      etapaId: pendingAction.etapa.id,
      status: pendingAction.targetStatus,
      observacao: observation || undefined,
      successMessage: pendingAction.successMessage,
    });
  };

  const handleStartClick = (etapa: CronogramaEtapa) => {
    const action = resolvePendingAction("todo", "doing", etapa);
    if (!action) {
      showToast("Você não pode iniciar esta etapa.", "warning");
      return;
    }
    openObservationDialog(action);
  };

  const handleFinishClick = (etapa: CronogramaEtapa) => {
    const action = resolvePendingAction("doing", "done", etapa);
    if (!action) {
      showToast("Você não pode concluir esta etapa.", "warning");
      return;
    }
    openObservationDialog(action);
  };

  const handleSolicitarAjustesClick = (etapa: CronogramaEtapa) => {
    const origin = getColumnFromStatus(etapa.status);
    const action = resolvePendingAction(origin, "doing", etapa);
    if (!action) {
      showToast("Você não pode solicitar ajustes para esta etapa.", "warning");
      return;
    }
    openObservationDialog(action);
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 className="text-lg font-semibold">Trilha de Progresso</h2>
          <p className="text-xs text-muted-foreground">
            Organize o fluxo do TCC movendo cada etapa por A Fazer, Fazendo e Concluído. Alunos avançam as entregas; orientadores podem reordenar quando necessário.
          </p>
        </div>
      </div>

      {isLoading || isFetching ? (
        <div className="flex justify-center py-8">
          <Spinner className="h-6 w-6 text-muted-foreground" />
        </div>
      ) : (
        <DndContext onDragStart={handleDragStart} onDragEnd={handleDragEnd}>
          <div className="grid gap-3 md:grid-cols-3">
            {COLUMN_CONFIG.map((column) => {
              const items = groupedEtapas[column.id];
              const extraAction =
                column.id === "todo" && canManage ? (
                  <Button
                    type="button"
                    size="icon"
                    variant="ghost"
                    className="h-7 w-7"
                    onClick={() => setIsCreateDialogOpen(true)}
                    aria-label="Criar nova etapa"
                  >
                    <Plus className="h-4 w-4" />
                  </Button>
                ) : undefined;
              return (
                <KanbanColumn
                  key={column.id}
                  columnId={column.id}
                  title={column.title}
                  subtitle={column.subtitle}
                  itemCount={items.length}
                  isDropDisabled={!canDropInColumn(column.id)}
                  extraAction={extraAction}
                >
                  {items.length === 0 ? (
                    <div className="rounded-md border border-dashed border-muted-foreground/30 bg-muted/10 p-4 text-center text-[11px] text-muted-foreground">
                      {column.emptyState}
                    </div>
                  ) : (
                    items.map((etapa) => {
                      const columnId = getColumnFromStatus(etapa.status);
                      const canDrag = canDragEtapa(etapa, columnId);
                      const isProcessing = processingId === etapa.id;

                      const allowStart =
                        userRole === PapelUsuario.ALUNO &&
                        columnId === "todo" &&
                        (etapa.status === StatusCronogramaEtapa.PENDENTE ||
                          etapa.status === StatusCronogramaEtapa.ATRASADA);

                      const allowFinish =
                        userRole === PapelUsuario.ALUNO &&
                        columnId === "doing" &&
                        etapa.status === StatusCronogramaEtapa.EM_ANDAMENTO;

                      const allowRequestChanges =
                        (userRole === PapelUsuario.ORIENTADOR ||
                          userRole === PapelUsuario.COORDENADOR) &&
                        columnId === "done";

                      const allowDelete =
                        userRole === PapelUsuario.ORIENTADOR ||
                        userRole === PapelUsuario.COORDENADOR;

                      return (
                        <KanbanCard
                          key={etapa.id}
                          etapa={etapa}
                          columnId={columnId}
                          canDrag={canDrag}
                          onStart={
                            allowStart ? () => handleStartClick(etapa) : undefined
                          }
                          onDeliver={
                            allowFinish ? () => handleFinishClick(etapa) : undefined
                          }
                          onRequestChanges={
                            allowRequestChanges
                              ? () => handleSolicitarAjustesClick(etapa)
                              : undefined
                          }
                          onDelete={
                            allowDelete
                              ? () => {
                                  if (confirm(`Tem certeza que deseja deletar a etapa "${etapa.nome}"? Esta ação não pode ser desfeita.`)) {
                                    deleteEtapaMutate(etapa.id);
                                  }
                                }
                              : undefined
                          }
                          isProcessing={isProcessing || activeCardId === etapa.id}
                        />
                      );
                    })
                  )}
                </KanbanColumn>
              );
            })}
          </div>
        </DndContext>
      )}

      {canManage && (
        <Dialog open={isCreateDialogOpen} onOpenChange={setIsCreateDialogOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Criar nova etapa</DialogTitle>
              <DialogDescription>
                Defina as datas principais e um contexto para o aluno.
              </DialogDescription>
            </DialogHeader>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="nome">Título da etapa</Label>
                <Input
                  id="nome"
                  {...form.register("nome")}
                  placeholder="Ex.: Entrega do Capítulo 1"
                />
                {form.formState.errors.nome && (
                  <p className="text-sm font-medium text-destructive">
                    {form.formState.errors.nome.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="dataFim">Data de término</Label>
                <Input id="dataFim" type="date" {...form.register("dataFim")} />
                {form.formState.errors.dataFim && (
                  <p className="text-sm font-medium text-destructive">
                    {form.formState.errors.dataFim.message}
                  </p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="observacao">Observações (opcional)</Label>
                <textarea
                  id="observacao"
                  rows={4}
                  className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
                  placeholder="Adicione informações relevantes ou links úteis."
                  {...form.register("observacao")}
                />
                {form.formState.errors.observacao && (
                  <p className="text-sm font-medium text-destructive">
                    {form.formState.errors.observacao.message}
                  </p>
                )}
              </div>

              <DialogFooter>
                <Button
                  type="button"
                  variant="secondary"
                  onClick={() => setIsCreateDialogOpen(false)}
                >
                  Cancelar
                </Button>
                <Button type="submit" disabled={isCreating}>
                  {isCreating ? "Salvando..." : "Salvar etapa"}
                </Button>
              </DialogFooter>
            </form>
          </DialogContent>
        </Dialog>
      )}

      <ObservationDialog
        open={observationDialogOpen}
        title={observationDialogConfig.title}
        description={observationDialogConfig.description}
        confirmLabel={observationDialogConfig.confirmLabel}
        cancelLabel={observationDialogConfig.cancelLabel}
        required={observationDialogConfig.requiresObservation}
        requiredMessage="Informe uma observação para que o aluno saiba quais ajustes realizar."
        isSubmitting={isDialogSubmitting}
        onConfirm={executePendingAction}
        onCancel={() => {
          if (!isDialogSubmitting) {
            resetActionState();
          }
        }}
      />
    </div>
  );
};




