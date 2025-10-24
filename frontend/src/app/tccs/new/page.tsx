"use client";

import { useMutation } from "@tanstack/react-query";
import { createTcc } from "@/services/tccs";
import { enviarConvite } from "@/services/convites";
import { TccForm, TccFormInputs } from "@/components/tccs/TccForm";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { TccCreateRequest, EnviarConviteRequest } from "@/interfaces";
import { useSessionStore } from "@/store/session";

export default function NewTccPage() {
  const { showToast } = useToast();
  const router = useRouter();
  const { user } = useSessionStore();

  const { mutate: create, isLoading: isCreating } = useMutation({
    mutationFn: (data: TccCreateRequest) => createTcc(data),
    onSuccess: (tcc, variables) => {
      if (user?.papel === "ALUNO" && variables.mensagemOrientador) {
        // Para alunos, enviar convite após criar TCC
        const convitePayload: EnviarConviteRequest = {
          orientadorId: variables.orientadorId,
          tccId: tcc.id,
          mensagem: variables.mensagemOrientador
        };
        
        enviarConviteMutation({ 
          alunoId: user.id, 
          data: convitePayload 
        });
      } else {
        showToast("TCC criado com sucesso!", "success");
        router.push("/tccs");
      }
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const { mutate: enviarConviteMutation, isLoading: isEnviandoConvite } = useMutation({
    mutationFn: ({ alunoId, data }: { alunoId: string; data: EnviarConviteRequest }) => 
      enviarConvite(alunoId, data),
    onSuccess: () => {
      showToast("Solicitação enviada com sucesso! O orientador receberá uma notificação.", "success");
      router.push("/tccs");
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const handleFormSubmit = (data: TccFormInputs) => {
    // Para alunos, incluir dados do convite no TCC
    const tccPayload = { 
      ...data, 
      dataInicio: new Date(data.dataInicio).toISOString(),
      orientadorId: user?.papel === "ALUNO" ? data.orientadorId : data.orientadorId,
      mensagemOrientador: user?.papel === "ALUNO" ? data.mensagemOrientador : undefined
    };
    
    create(tccPayload);
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Novo TCC</h1>
      {user?.papel === "ALUNO" && (
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
          <h3 className="font-semibold text-blue-900 mb-2">Sistema de Solicitações</h3>
          <p className="text-sm text-blue-700">
            Ao criar um TCC, você enviará uma solicitação para o orientador escolhido. 
            O orientador receberá uma notificação e poderá aceitar ou recusar a solicitação.
          </p>
        </div>
      )}
      <TccForm onSubmit={handleFormSubmit} isSubmitting={isCreating || isEnviandoConvite} />
    </div>
  );
}
