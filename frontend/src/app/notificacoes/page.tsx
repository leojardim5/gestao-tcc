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
import { StatusConvite } from "@/interfaces";
import { isOrientador } from "@/utils/guards";

export default function NotificacoesPage() {
  const queryClient = useQueryClient();
  const { user } = useSessionStore();
  const { showToast } = useToast();

  const { data: notificacoes, isLoading: isLoadingNotificacoes } = useQuery({
    queryKey: ["notificacoes", user?.id],
    queryFn: () => listNotificacoes({ usuarioId: user?.id || "" }),
    enabled: !!user?.id,
  });

  const { data: convites, isLoading: isLoadingConvites } = useQuery({
    queryKey: ["convites-pendentes", user?.id],
    queryFn: () => getConvitesPendentes(user?.id || ""),
    enabled: !!user?.id && isOrientador(user?.papel),
  });

  const { mutate: markAsRead } = useMutation({
    mutationFn: markNotificacaoAsRead,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["notificacoes"] });
    },
  });

  const { mutate: responder, isPending: isRespondendo } = useMutation({
    mutationFn: ({ conviteId, status }: { conviteId: string; status: StatusConvite }) =>
      responderConvite(user?.id || "", conviteId, { status }),
    onSuccess: () => {
      showToast("Resposta enviada com sucesso!", "success");
      queryClient.invalidateQueries({ queryKey: ["convites-pendentes"] });
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
    responder({ conviteId, status: StatusConvite.REJEITADO });
  };

  const isLoading = isLoadingNotificacoes || isLoadingConvites;

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-bold">Notificações</h1>
      
      {isLoading ? (
        <div className="flex justify-center"><Spinner /></div>
      ) : (
        <div className="space-y-6">
          {/* Convites de Orientação */}
          {isOrientador(user?.papel) && convites && convites.length > 0 && (
            <div>
              <h2 className="text-lg font-semibold mb-4 flex items-center gap-2">
                <Mail className="h-5 w-5 text-blue-500" />
                Solicitações de Orientação
              </h2>
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
                              <p className="text-gray-700 italic">"{convite.mensagem}"</p>
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
                          <span className="text-xs text-muted-foreground">{formatDateTime(notificacao.criadoEm)}</span>
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
