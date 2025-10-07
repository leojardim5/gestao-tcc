"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listNotificacoes, markNotificacaoAsRead } from "@/services/notificacoes";
import { useSessionStore } from "@/store/session";
import { Spinner } from "@/components/ui/Spinner";
import { Card, CardContent } from "@/components/ui/Card";
import { formatDateTime } from "@/utils/date";
import { Button } from "@/components/ui/Button";
import { Bell } from "lucide-react";

export default function NotificacoesPage() {
  const queryClient = useQueryClient();
  const { user } = useSessionStore();
  const fakeUserId = "f79a8a5d-3c20-4e38-bedf-2743e634f82b"; // Placeholder

  const { data, isLoading } = useQuery({
    queryKey: ["notificacoes", fakeUserId],
    queryFn: () => listNotificacoes({ usuarioId: fakeUserId }),
    enabled: !!user,
  });

  const { mutate: markAsRead } = useMutation(
    (id: string) => markNotificacaoAsRead(id),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["notificacoes"]);
      },
    }
  );

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Notificações</h1>
      {isLoading ? (
        <div className="flex justify-center"><Spinner /></div>
      ) : data && data.length > 0 ? (
        <div className="space-y-4">
          {data.map((notificacao) => (
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
        <p>Nenhuma notificação encontrada.</p>
      )}
    </div>
  );
}
