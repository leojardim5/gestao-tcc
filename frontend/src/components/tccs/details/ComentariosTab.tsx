"use client";

import { useQuery } from "@tanstack/react-query";
import { getLatestSubmissao } from "@/services/submissoes";
import { listComentariosBySubmissao } from "@/services/comentarios";
import { Spinner } from "@/components/ui/Spinner";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { formatDateTime } from "@/utils/date";

interface ComentariosTabProps {
  tccId: string;
}

export function ComentariosTab({ tccId }: ComentariosTabProps) {
  const { data: latestSubmissao, isLoading: isLoadingSubmissao } = useQuery({
    queryKey: ["latestSubmissao", tccId],
    queryFn: () => getLatestSubmissao(tccId),
    retry: (failureCount, error: any) => {
        // Don't retry if the error is 404 (no submission found)
        if (error?.response?.status === 404) {
          return false;
        }
        return failureCount < 3;
      },
  });

  const submissaoId = latestSubmissao?.id;

  const { data: comentarios, isLoading: isLoadingComentarios } = useQuery({
    queryKey: ["comentarios", submissaoId],
    queryFn: () => listComentariosBySubmissao(submissaoId!),
    enabled: !!submissaoId,
  });

  if (isLoadingSubmissao) {
    return <div className="flex justify-center"><Spinner /></div>;
  }

  if (!latestSubmissao) {
    return <p>Nenhuma submissão encontrada para este TCC.</p>;
  }

  return (
    <div>
      <h3 className="text-lg font-semibold mb-4">Comentários na Versão {latestSubmissao.versao}</h3>
      {isLoadingComentarios ? (
        <div className="flex justify-center"><Spinner /></div>
      ) : comentarios && comentarios.length > 0 ? (
        <div className="space-y-4">
          {comentarios.map((comentario) => (
            <Card key={comentario.id}>
              <CardHeader className="pb-2">
                <div className="flex items-center justify-between">
                    <CardTitle className="text-base">{comentario.autor.nome}</CardTitle>
                    <span className="text-xs text-muted-foreground">{formatDateTime(comentario.criadoEm)}</span>
                </div>
              </CardHeader>
              <CardContent>
                <p>{comentario.comentario}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      ) : (
        <p>Nenhum comentário nesta submissão.</p>
      )}
    </div>
  );
}
