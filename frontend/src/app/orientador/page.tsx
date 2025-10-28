"use client";

import { useSessionStore } from "@/store/session";
import { DisponibilidadeSwitch } from "@/components/usuarios/DisponibilidadeSwitch";
import { Card } from "@/components/ui/Card";
import { useQuery, useMutation } from "@tanstack/react-query";
import { getTodosConvitesOrientador, responderConvite } from "@/services/convites";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { StatusConvite } from "@/interfaces";

export default function OrientadorPage() {
  const { user } = useSessionStore();
  const { showToast } = useToast();

  const { data: convites, isLoading, refetch } = useQuery({
    queryKey: ['convites-orientador', user?.id],
    queryFn: () => getTodosConvitesOrientador(user!.id),
    enabled: !!user?.id,
  });

  const responderMutation = useMutation({
    mutationFn: ({ conviteId, aceitar }: { conviteId: string; aceitar: boolean }) => {
      return responderConvite(user.id, conviteId, { aceitar });
    },
    onSuccess: (result, variables) => {
      showToast(
        variables.aceitar 
          ? "Convite aceito com sucesso!" 
          : "Convite rejeitado com sucesso!", 
        "success"
      );
      refetch();
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  if (!user) {
    return <div>Usuário não encontrado</div>;
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold">Painel do Orientador</h1>
        <p className="text-gray-600">Gerencie sua disponibilidade e convites de orientação</p>
      </div>

      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Configurações de Disponibilidade</h2>
        <p className="text-sm text-gray-600 mb-4">
          Ative esta opção para receber convites de orientação de alunos. 
          Quando ativada, você aparecerá na lista de orientadores disponíveis 
          quando alunos criarem novos TCCs.
        </p>
        
        <DisponibilidadeSwitch 
          userId={user.id} 
          initialValue={user.disponivelParaOrientacao || false} 
        />
      </Card>

      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Convites de Orientação</h2>
        
        {isLoading ? (
          <p>Carregando convites...</p>
        ) : convites && convites.length > 0 ? (
          <div className="space-y-4">
            {convites.map((convite) => (
              <div key={convite.id} className="border rounded-lg p-4">
                <div className="flex justify-between items-start mb-2">
                  <div>
                    <h3 className="font-semibold">{convite.tcc.titulo}</h3>
                    <p className="text-sm text-gray-600">
                      Aluno: {convite.aluno.nome}
                    </p>
                    <p className="text-sm text-gray-600">
                      Curso: {convite.tcc.curso}
                    </p>
                  </div>
                  <Badge variant={
                    convite.status === StatusConvite.PENDENTE ? "default" :
                    convite.status === StatusConvite.ACEITO ? "default" :
                    "destructive"
                  }>
                    {convite.status.replace(/_/g, ' ')}
                  </Badge>
                </div>
                
                {convite.mensagem && (
                  <p className="text-sm mb-3 p-2 bg-gray-50 rounded">
                    <strong>Mensagem:</strong> {convite.mensagem}
                  </p>
                )}
                
                {convite.status === StatusConvite.PENDENTE && (
                  <div className="flex gap-2">
                    <Button
                      size="sm"
                      onClick={() => responderMutation.mutate({ conviteId: convite.id, aceitar: true })}
                      disabled={responderMutation.isPending}
                    >
                      Aceitar
                    </Button>
                    <Button
                      size="sm"
                      variant="destructive"
                      onClick={() => responderMutation.mutate({ conviteId: convite.id, aceitar: false })}
                      disabled={responderMutation.isPending}
                    >
                      Rejeitar
                    </Button>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <p className="text-gray-600">Nenhum convite de orientação encontrado.</p>
        )}
      </Card>

      <Card className="p-6">
        <h2 className="text-lg font-semibold mb-4">Informações do Perfil</h2>
        <div className="space-y-2">
          <p><strong>Nome:</strong> {user.nome}</p>
          <p><strong>Email:</strong> {user.email}</p>
          <p><strong>Papel:</strong> {user.papel}</p>
          <p><strong>Status:</strong> {user.ativo ? "Ativo" : "Inativo"}</p>
        </div>
      </Card>
    </div>
  );
}
