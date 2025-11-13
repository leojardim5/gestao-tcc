import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { Card, CardContent } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { PapelUsuario, TccWorkspaceOverview, GoogleDocComment } from "@/interfaces";
import { ensureTccWorkspaceDocument, listTccDocumentComments } from "@/services/tccs";
import { useToast } from "@/hooks/useToast";
import { useSessionStore } from "@/store/session";
import { useTccNotificationsStore, selectPendingCountForTcc } from "@/store/tccNotifications";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { formatDateTime } from "@/utils/date";
import { useCallback, useEffect, useMemo, useRef } from "react";
import { ArrowUpRight, FileText, Plus } from "lucide-react";

interface DocumentTabProps {
  tccId: string;
  overview: TccWorkspaceOverview;
}

export const DocumentTab = ({ tccId, overview }: DocumentTabProps) => {
  const hasDocument = Boolean(overview.googleFileId && overview.googleWebViewLink);
  const userRole = useSessionStore((state) => state.user?.papel ?? null);
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const attemptedRef = useRef(false);

  const setNotificationCount = useTccNotificationsStore((state) => state.setCountForTcc);

  const canManageDocument = useMemo(
    () => userRole === PapelUsuario.ORIENTADOR || userRole === PapelUsuario.COORDENADOR,
    [userRole]
  );

  const { mutate: ensureDocument, isPending, isError } = useMutation({
    mutationFn: () => ensureTccWorkspaceDocument(tccId),
    onSuccess: (data) => {
      queryClient.setQueryData(["tccs", tccId, "workspace", "overview"], data);
      attemptedRef.current = true;
      if (data.googleFileId) {
        showToast("Documento vinculado com sucesso.", "success");
      } else {
        showToast(
          "Não foi possível criar o documento agora. Verifique o espaço disponível na conta Google e tente novamente.",
          "warning"
        );
      }
    },
    onError: (error) => {
      console.error("[DocumentTab] Falha ao criar documento:", error);
      showToast("Não foi possível criar o documento automaticamente. Tente novamente mais tarde.", "error");
      attemptedRef.current = true;
    },
  });

  useEffect(() => {
    if (!hasDocument && canManageDocument && !attemptedRef.current) {
      attemptedRef.current = true;
      ensureDocument();
    }
  }, [hasDocument, canManageDocument, ensureDocument]);

  const { data: comments = [] } = useQuery<GoogleDocComment[]>({
    queryKey: ["tccs", tccId, "workspace", "document", "comments"],
    queryFn: () => listTccDocumentComments(tccId),
    enabled: hasDocument,
    refetchInterval: 60000,
  });

  useEffect(() => {
    if (hasDocument) {
      const unresolved = comments.filter((comment) => !comment.resolved).length;
      setNotificationCount(tccId, unresolved);
    }
  }, [comments, hasDocument, setNotificationCount, tccId]);

  const handleOpenDocument = useCallback(() => {
    const targetUrl = overview.googleWebEditLink ?? overview.googleWebViewLink;
    if (targetUrl) {
      window.open(targetUrl, "_blank", "noopener,noreferrer");
    }
  }, [overview.googleWebEditLink, overview.googleWebViewLink]);

  const handleCreateNewGoogleDoc = useCallback(() => {
    window.open("https://docs.google.com/document/u/0/create", "_blank", "noopener,noreferrer");
  }, []);

  if (isPending) {
    return (
      <Card className="border-dashed">
        <CardContent className="flex items-center gap-3 p-6 text-sm text-muted-foreground">
          <Spinner className="h-4 w-4" />
          <span>Gerando documento do TCC…</span>
        </CardContent>
      </Card>
    );
  }

  if (!hasDocument) {
    return (
      <Card className="border-dashed border-muted-foreground/40 bg-muted/10">
        <CardContent className="space-y-4 p-6 text-sm text-muted-foreground">
          <p>
            O documento do TCC será criado automaticamente assim que o orientador acessar a workspace. Depois disso, o link
            aparecerá aqui.
          </p>
          {canManageDocument && (
            <Button variant="outline" size="sm" onClick={() => ensureDocument()} disabled={isPending}>
              Tentar criar agora
            </Button>
          )}
          {isError && (
            <p className="text-sm text-destructive">
              Não foi possível criar o documento automaticamente. Verifique as configurações da integração e tente novamente.
            </p>
          )}
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="border-muted-foreground/20">
      <CardContent className="flex h-full flex-col gap-4 p-4">
        <div className="flex items-start gap-3">
          <span className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10 text-primary">
            <FileText className="h-5 w-5" aria-hidden="true" />
          </span>
          <div className="flex-1">
            <div className="flex flex-wrap items-center gap-2">
              <Badge variant="outline" className="text-xs">Google Docs</Badge>
              {overview.googleDocCriadoEm && (
                <span className="text-xs text-muted-foreground">
                  Criado em {formatDateTime(overview.googleDocCriadoEm)}
                </span>
              )}
            </div>
            <h3 className="mt-1.5 text-base font-semibold text-foreground">Documento principal do TCC</h3>
            <p className="mt-0.5 text-xs text-muted-foreground">
              Gerencie os arquivos vinculados ao trabalho. O documento oficial permanece no topo da lista.
            </p>
          </div>
        </div>

        <div className="space-y-2.5">
          <button
            type="button"
            onClick={handleOpenDocument}
            className="flex w-full items-center justify-between rounded-lg border border-slate-300 bg-slate-300 px-3 py-2.5 text-left shadow-sm transition hover:bg-slate-300/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
          >
            <div>
              <p className="text-sm font-medium text-slate-900 line-clamp-1">{overview.titulo ?? "Documento do TCC"}</p>
              <p className="text-xs text-slate-600">Abrir no Google Docs</p>
            </div>
            <span className="text-slate-600 flex-shrink-0 ml-2">
              <ArrowUpRight className="h-4 w-4" aria-hidden="true" />
            </span>
          </button>

          <button
            type="button"
            onClick={handleCreateNewGoogleDoc}
            className="flex w-full items-center justify-between rounded-lg border border-slate-300 border-dashed bg-slate-300 px-3 py-2.5 text-left text-sm font-medium text-slate-700 transition hover:bg-slate-300/90 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
          >
            <span>Novo Doc</span>
            <Plus className="h-4 w-4" aria-hidden="true" />
          </button>
        </div>
      </CardContent>
    </Card>
  );
};


