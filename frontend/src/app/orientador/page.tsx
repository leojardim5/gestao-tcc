"use client";

import { useSessionStore } from "@/store/session";
import { DisponibilidadeSwitch } from "@/components/usuarios/DisponibilidadeSwitch";
import { Card } from "@/components/ui/Card";

export default function OrientadorPage() {
  const { user } = useSessionStore();

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
