"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listNotificacoes, markNotificacaoAsRead } from "@/services/notificacoes";
import { getConvitesPendentes, responderConvite } from "@/services/convites";
import { useSessionStore } from "@/store/session";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { Spinner } from "@/components/ui/Spinner";
import { Card, CardContent } from "@/components/ui/Card";
import { formatDateTime } from "@/utils/date";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { Bell, Mail, CheckCircle, XCircle, Clock } from "lucide-react";
import { StatusConvite, Notificacao } from "@/interfaces";
import { useState } from "react";
import { isOrientador } from "@/utils/guards";

export default function NotificacoesPage() {
  const queryClient = useQueryClient();
  const { user } = useSessionStore();
  const { showToast } = useToast();
  const [rejectOpen, setRejectOpen] = useState(false);
  const [rejectMotivo, setRejectMotivo] = useState("");
  const [rejectConviteId, setRejectConviteId] = useState<string | null>(null);

  const unreadKey = ["notificacoes", { usuarioId: user?.id, lidas: false }] as const;
  const allKey = ["notificacoes", { usuarioId: user?.id }] as const;

  const { data: notificacoes, isLoading: isLoadingNotificacoes } = useQuery({
    queryKey: unreadKey,
    queryFn: () => listNotificacoes({ usuarioId: user?.id || "", lidas: false }),
    enabled: !!user?.id,
  });

  const { data: convites, isLoading: isLoadingConvites } = useQuery({
    queryKey: ["convites-pendentes", user?.id],
    queryFn: () => getConvitesPendentes(user?.id || ""),
    enabled: !!user?.id && isOrientador(user?.papel),
  });

  const { mutate: markAsRead } = useMutation({
    mutationFn: (id: string) => markNotificacaoAsRead(id),
    onSuccess: (_, id) => {
      const sidebarKey = ["notificacoes", { usuarioId: user?.id, lidas: false }, "sidebar"] as const;

      queryClient.setQueryData<Notificacao[] | undefined>(unreadKey, (old) =>
        old ? old.filter((n) => n.id !== id) : old
      );
      queryClient.setQueryData<Notificacao[] | undefined>(sidebarKey, (old) =>
        old ? old.filter((n) => n.id !== id) : old
      );
      queryClient.setQueryData<Notificacao[] | undefined>(allKey, (old) =>
        old ? old.map((n) => (n.id === id ? { ...n, lida: true } : n)) : old
      );

      queryClient.invalidateQueries({
        predicate: ({ queryKey }) => Array.isArray(queryKey) && queryKey[0] === "notificacoes",
      });
    },
  });

  const { mutate: responder, isPending: isRespondendo } = useMutation({
    mutationFn: ({ conviteId, status, motivo }: { conviteId: string; status: StatusConvite; motivo?: string }) =>
      responderConvite(user?.id || "", conviteId, motivo ? { status, motivo } : { status }),
    onSuccess: () => {
      showToast("Resposta enviada com sucesso!", "success");
      queryClient.invalidateQueries({ queryKey: ["convites-pendentes"] });
      queryClient.invalidateQueries({ queryKey: ["notificacoes"] });
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const handleAceitar = (conviteId: string) => {
    responder({ conviteId, status: StatusConvite.ACEITO });
  };

  const handleRejeitar = (conviteId: string) => {
    setRejectConviteId(conviteId);
    setRejectMotivo("");
    setRejectOpen(true);
  };

  const isLoading = isLoadingNotificacoes || (isOrientador(user?.papel) && isLoadingConvites);

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Notificações</h1>
      
      {isLoading ? (
        <div className="flex justify-center"><Spinner /></div>
      ) : (
        <div className="space-y-6">
          {/* Convites de Orientação */}
          {isOrientador(user?.papel) && (
            <div>
              <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
                <Mail className="h-5 w-5 text-blue-500" />
                Solicitações de Orientação
              </h2>

              {convites && convites.length > 0 ? (
                <div className="space-y-4">
                  {convites.map((convite) => (
                    <Card key={convite.id} className="border-blue-200 bg-blue-50">
                      <CardContent className="p-6">
                        <div className="flex items-start justify-between">
                          <div className="flex-1">
                            <div className="flex items-center gap-2 mb-2">
                              <h3 className="text-lg font-semibold">{convite.tccTitulo}</h3>
                              <Badge variant="secondary" className="flex items-center gap-1">
                                <Clock className="h-3 w-3" />
                                Pendente
                              </Badge>
                            </div>

                            <div className="space-y-2 text-sm text-gray-600">
                              <p><strong>Aluno:</strong> {convite.alunoNome} ({convite.alunoEmail})</p>
                              <p><strong>Data do convite:</strong> {new Date(convite.dataEnvio).toLocaleDateString('pt-BR')}</p>
                            </div>

                            {convite.mensagem && (
                              <div className="mt-4 p-4 bg-white rounded-lg border">
                                <h4 className="font-medium text-gray-900 mb-2">Mensagem do aluno:</h4>
                                <p className="text-gray-700 italic">&ldquo;{convite.mensagem}&rdquo;</p>
                              </div>
                            )}
                          </div>

                          <div className="flex gap-2 ml-4">
                            <Button
                              onClick={() => handleAceitar(convite.id)}
                              disabled={isRespondendo}
                              className="bg-green-600 hover:bg-green-700 text-white"
                            >
                              <CheckCircle className="h-4 w-4 mr-2" />
                              Aceitar
                            </Button>
                            <Button
                              onClick={() => handleRejeitar(convite.id)}
                              disabled={isRespondendo}
                              variant="destructive"
                            >
                              <XCircle className="h-4 w-4 mr-2" />
                              Rejeitar
                            </Button>
                          </div>
                        </div>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              ) : (
                <Card className="border border-dashed border-blue-200 bg-blue-50/60">
                  <CardContent className="p-8">
                    <div className="flex flex-col items-center text-center gap-3 text-muted-foreground">
                      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-blue-100 text-blue-600">
                        <Mail className="h-6 w-6" />
                      </div>
                      <div>
                        <h3 className="text-base font-semibold text-blue-800">Nenhum convite de orientação por aqui</h3>
                        <p className="text-sm text-blue-700">
                          Assim que um aluno enviar um convite, ele aparecerá neste painel para você aceitar ou rejeitar.
                        </p>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              )}
            </div>
          )}

          {rejectOpen && (
            <div className="fixed inset-0 z-50">
              <div className="absolute inset-0 bg-black/70" onClick={() => setRejectOpen(false)} />
              <div className="absolute left-1/2 top-1/2 w-full max-w-lg -translate-x-1/2 -translate-y-1/2 rounded-lg border bg-white p-6 shadow-2xl">
                <h3 className="text-lg font-semibold mb-2">Rejeitar convite</h3>
                <p className="text-sm text-gray-600 mb-3">Opcionalmente, informe o motivo da rejeição:</p>
                <textarea
                  value={rejectMotivo}
                  onChange={(e) => setRejectMotivo(e.target.value)}
                  className="w-full rounded border bg-white text-gray-900 placeholder:text-gray-400 p-2 text-sm"
                  rows={4}
                  placeholder="Ex.: Não estou disponível para orientar no período solicitado"
                />
                <div className="mt-4 flex justify-end gap-2">
                  <Button variant="outline" onClick={() => setRejectOpen(false)}>Cancelar</Button>
                  <Button
                    variant="destructive"
                    disabled={isRespondendo || !rejectConviteId}
                    onClick={() => {
                      if (!rejectConviteId) return;
                      responder({ conviteId: rejectConviteId, status: StatusConvite.REJEITADO, motivo: rejectMotivo || undefined });
                      setRejectOpen(false);
                    }}
                  >
                    Confirmar rejeição
                  </Button>
                </div>
              </div>
            </div>
          )}

          {/* Notificações Gerais */}
          <div>
            <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
              <Bell className="h-5 w-5 text-gray-500" />
              Notificações Gerais
            </h2>
            {notificacoes && notificacoes.length > 0 ? (
              <div className="space-y-4">
                {notificacoes.map((notificacao) => (
                  <Card key={notificacao.id} className={`${!notificacao.lida ? 'bg-blue-50' : ''}`}>
                    <CardContent className="p-4 flex items-start gap-4">
                      <Bell className="h-6 w-6 text-blue-500 mt-1" />
                      <div className="flex-1">
                          <p>{notificacao.mensagem}</p>
                          <span className="text-xs text-muted-foreground">
                            {notificacao.criadoEm ? formatDateTime(notificacao.criadoEm) : 'Data inválida'}
                          </span>
                      </div>
                      {!notificacao.lida && (
                        <Button variant="outline" size="sm" onClick={() => markAsRead(notificacao.id)}>
                          Marcar como lida
                        </Button>
                      )}
                    </CardContent>
                  </Card>
                ))}
              </div>
            ) : (
              <Card className="p-6 text-center">
                <Bell className="mx-auto h-12 w-12 text-gray-400 mb-4" />
                <h3 className="text-lg font-semibold text-gray-900 mb-2">Nenhuma notificação</h3>
                <p className="text-gray-600">
                  Você não possui notificações no momento.
                </p>
              </Card>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
