"use client";

import { z } from "zod";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useMemo, useState } from "react";
import { Tcc, Usuario, IaSuggestionItem } from "@/interfaces";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/Select";
import { useMutation, useQuery } from "@tanstack/react-query";
import { listUsuarios, listOrientadoresDisponiveis } from "@/services/usuarios";
import { sugerirOrientadoresIa } from "@/services/ia";
import { handleApiError } from "@/services/api";
import { PapelUsuario } from "@/interfaces";
import { Spinner } from "@/components/ui/Spinner";
import { Sparkles } from "lucide-react";
import { OrientadorSuggestionDialog } from "./OrientadorSuggestionDialog";
import { useToast } from "@/hooks/useToast";

const createTccSchema = (isAluno: boolean) => z.object({
  titulo: z.string().min(1, "Título é obrigatório"),
  tema: z.string().min(1, "Tema é obrigatório"),
  curso: z.string().min(1, "Curso é obrigatório"),
  alunoId: z.string().min(1, "Aluno é obrigatório"),
  orientadorId: z.string().min(1, "Orientador é obrigatório"), // Sempre obrigatório
  dataInicio: z.string().min(1, "Data de início é obrigatória"),
  mensagemOrientador: isAluno 
    ? z.string().min(10, "Mensagem deve ter pelo menos 10 caracteres")
    : z.string().optional(),
});

export type TccFormInputs = z.infer<ReturnType<typeof createTccSchema>>;

interface TccFormProps {
  onSubmit: (data: TccFormInputs) => void;
  defaultValues?: Partial<Tcc>;
  isSubmitting: boolean;
  onReset?: () => void;
}

import { useSessionStore } from "@/store/session";

export function TccForm({ onSubmit, defaultValues, isSubmitting, onReset }: TccFormProps) {
  const { user } = useSessionStore();
  const { showToast } = useToast();

  const isAluno = user?.papel === PapelUsuario.ALUNO;

  const { data: alunosData } = useQuery({
    queryKey: ["usuarios", { papel: PapelUsuario.ALUNO }],
    queryFn: () => listUsuarios({ papel: PapelUsuario.ALUNO }),
    enabled: !isAluno, // Only fetch if the user is not an Aluno
  });

  // BUSCAR ORIENTADORES REAIS DO BACKEND
  const { data: orientadoresData, isLoading: isLoadingOrientadores, error: orientadoresError } = useQuery({
    queryKey: ['orientadores-disponiveis'],
    queryFn: () => listOrientadoresDisponiveis(),
    retry: 1, // Tentar apenas 1 vez
  });

  const { register, handleSubmit, control, reset, formState: { errors }, getValues, setValue } = useForm<TccFormInputs>({
    resolver: zodResolver(createTccSchema(isAluno)),
    defaultValues: {
      ...defaultValues,
      alunoId: isAluno ? user.id : defaultValues?.aluno?.id,
      orientadorId: defaultValues?.orientador?.id,
      dataInicio: defaultValues?.dataInicio?.split('T')[0] || new Date().toISOString().split('T')[0] // Data atual como padrão
    },
  });

  // Reset form when onReset is called
  useEffect(() => {
    if (onReset) {
      reset({
        alunoId: isAluno ? user.id : undefined,
        orientadorId: undefined,
        dataInicio: new Date().toISOString().split('T')[0], // Data atual
        titulo: "",
        tema: "",
        curso: "",
        mensagemOrientador: ""
      });
    }
  }, [onReset, reset, isAluno, user?.id]);

  const orientadoresMap = useMemo(() => {
    const map: Record<string, Usuario> = {};
    orientadoresData?.content?.forEach((orientador) => {
      map[orientador.id] = orientador;
    });
    return map;
  }, [orientadoresData?.content]);

  const [suggestionsOpen, setSuggestionsOpen] = useState(false);
  const [iaSuggestions, setIaSuggestions] = useState<IaSuggestionItem[]>([]);
  const [iaMensagem, setIaMensagem] = useState<string | undefined>();
  const [iaModelo, setIaModelo] = useState<string | undefined>();

  const { mutate: sugerirOrientadores, isPending: isLoadingIa } = useMutation({
    mutationFn: sugerirOrientadoresIa,
    onSuccess: (data) => {
      setIaSuggestions(data.sugestoes ?? []);
      setIaMensagem(data.mensagemSistema ?? undefined);
      setIaModelo(data.modelo ?? undefined);
      setSuggestionsOpen(true);
    },
    onError: (error: unknown) => {
      const { message } = handleApiError(error);
      showToast(message || "Não foi possível obter sugestões da IA.", "error");
    },
  });

  const extrairPalavrasChave = (texto: string) => {
    return Array.from(
      new Set(
        texto
          .toLowerCase()
          .split(/[\s,.;:/]+/)
          .filter((token) => token.length > 3)
      )
    ).slice(0, 12);
  };

  const handleSolicitarSugestoes = () => {
    const values = getValues();
    const alunoIdSelecionado = isAluno ? user.id : values.alunoId;

    if (!alunoIdSelecionado) {
      showToast("Selecione um aluno antes de pedir sugestões.", "warning");
      return;
    }

    if (!values.titulo || !values.tema || !values.curso) {
      showToast("Preencha título, tema e curso para gerar sugestões.", "warning");
      return;
    }

    const alunoNome = isAluno
      ? user.nome
      : alunosData?.content?.find((aluno) => aluno.id === alunoIdSelecionado)?.nome ?? "Aluno";

    const palavrasChave = extrairPalavrasChave(
      [values.tema, values.curso, values.mensagemOrientador].filter(Boolean).join(" ")
    );

    sugerirOrientadores({
      alunoId: alunoIdSelecionado,
      alunoNome,
      curso: values.curso,
      titulo: values.titulo,
      tema: values.tema,
      resumo: values.mensagemOrientador || values.tema,
      mensagem: values.mensagemOrientador,
      palavrasChave,
    });
  };

  const handleSelecionarOrientador = (orientadorId: string) => {
    setValue("orientadorId", orientadorId, { shouldValidate: true, shouldDirty: true });
    setSuggestionsOpen(false);
    const orientador = orientadoresMap[orientadorId];
    showToast(
      orientador ? `Orientador ${orientador.nome} selecionado a partir da IA.` : "Orientador selecionado.",
      "success"
    );
  };

  return (
    <form onSubmit={handleSubmit((data) => {
      console.log("📝 FORMULÁRIO SUBMETIDO VIA handleSubmit!", data);
      onSubmit(data);
    })} className="grid gap-4">
      <div className="grid gap-2">
        <Label htmlFor="titulo">Título</Label>
        <Input id="titulo" {...register("titulo")} />
        {errors.titulo && <p className="text-sm font-medium text-destructive">{errors.titulo.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
            <Label htmlFor="tema">Tema</Label>
            <Input id="tema" {...register("tema")} />
            {errors.tema && <p className="text-sm font-medium text-destructive">{errors.tema.message}</p>}
        </div>
        <div className="grid gap-2">
            <Label htmlFor="curso">Curso</Label>
            <Input id="curso" {...register("curso")} />
            {errors.curso && <p className="text-sm font-medium text-destructive">{errors.curso.message}</p>}
        </div>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="dataInicio">Data de Solicitação</Label>
        <Input 
          id="dataInicio" 
          type="date" 
          {...register("dataInicio")} 
          readOnly
          className="bg-gray-50"
        />
        {errors.dataInicio && <p className="text-sm font-medium text-destructive">{errors.dataInicio.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
            <Label htmlFor="alunoId">Aluno</Label>
            {isAluno ? (
              <div>
                <Input defaultValue={user?.nome} disabled />
                <input type="hidden" {...register("alunoId")} value={user?.id} />
              </div>
            ) : (
              <Controller
                  name="alunoId"
                  control={control}
                  render={({ field }) => (
                      <Select onValueChange={field.onChange} defaultValue={field.value} disabled={!!defaultValues}>
                          <SelectTrigger><SelectValue placeholder="Selecione um aluno" /></SelectTrigger>
                          <SelectContent className="max-h-64 overflow-y-auto">
                              {alunosData?.content.map((aluno) => (
                                  <SelectItem key={aluno.id} value={aluno.id}>{aluno.nome}</SelectItem>
                              ))}
                          </SelectContent>
                      </Select>
                  )}
              />
            )}
            {errors.alunoId && <p className="text-sm font-medium text-destructive">{errors.alunoId.message}</p>}
        </div>
        <div className="grid gap-2">
            <div className="flex items-center justify-between gap-2">
              <Label htmlFor="orientadorId">Orientador</Label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="flex items-center gap-2 border-emerald-500/50 text-emerald-500 hover:border-emerald-500 hover:text-emerald-600"
                onClick={handleSolicitarSugestoes}
                disabled={isLoadingIa || (isAluno && (isLoadingOrientadores || !!orientadoresError))}
              >
                {isLoadingIa ? (
                  <>
                    <Spinner className="h-4 w-4" />
                    Consultando…
                  </>
                ) : (
                  <>
                    <Sparkles className="h-4 w-4" />
                    Sugerir orientadores
                  </>
                )}
              </Button>
            </div>
            <Controller
                name="orientadorId"
                control={control}
                render={({ field }) => (
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <SelectTrigger>
                            <SelectValue placeholder={
                                isLoadingOrientadores 
                                    ? "Carregando orientadores..." 
                                    : "Selecione um orientador"
                            } />
                        </SelectTrigger>
                        <SelectContent className="max-h-64 overflow-y-auto">
                            {isLoadingOrientadores ? (
                                <SelectItem value="loading" disabled>Carregando...</SelectItem>
                            ) : orientadoresError ? (
                                <SelectItem value="error" disabled>Erro ao carregar orientadores</SelectItem>
                            ) : orientadoresData?.content?.length === 0 ? (
                                <SelectItem value="empty" disabled>Nenhum orientador disponível</SelectItem>
                            ) : orientadoresData?.content?.map((orientador) => (
                                <SelectItem key={orientador.id} value={orientador.id}>
                                    {orientador.nome}
                                </SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                )}
            />
            {errors.orientadorId && <p className="text-sm font-medium text-destructive">{errors.orientadorId.message}</p>}
        </div>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="mensagemOrientador">Mensagem para o Orientador</Label>
        <textarea
          id="mensagemOrientador"
          {...register("mensagemOrientador")}
          className="min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          placeholder="Escreva uma mensagem para o orientador explicando sobre seu projeto e por que gostaria que ele fosse seu orientador..."
        />
        {errors.mensagemOrientador && <p className="text-sm font-medium text-destructive">{errors.mensagemOrientador.message}</p>}
      </div>

      <Button 
        type="submit" 
        disabled={isSubmitting} 
        className="w-full"
        onClick={(e) => {
          console.log("🔥 BOTÃO CLICADO!");
          console.log("📋 Dados do formulário:", getValues());
          console.log("❌ Erros de validação:", errors);
        }}
      >
        {isSubmitting ? <Spinner className="mr-2 h-4 w-4 animate-spin" /> : null}
        {user?.papel === "ALUNO" ? "Enviar Solicitação" : "Salvar"}
      </Button>

      <OrientadorSuggestionDialog
        open={suggestionsOpen}
        onOpenChange={setSuggestionsOpen}
        isLoading={isLoadingIa}
        sugestoes={iaSuggestions}
        mensagemSistema={iaMensagem}
        modelo={iaModelo}
        orientadoresDetalhes={orientadoresMap}
        onSelect={handleSelecionarOrientador}
      />
    </form>
  );
}
