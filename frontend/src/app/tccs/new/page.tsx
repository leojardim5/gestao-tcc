"use client";

import { useMutation } from "@tanstack/react-query";
import { useState, useEffect } from "react";
import { createTcc } from "@/services/tccs";
import { enviarConvite } from "@/services/convites";
import { TccForm, TccFormInputs } from "@/components/tccs/TccForm";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { TccCreateRequest, EnviarConviteRequest } from "@/interfaces";
import { useSessionStore } from "@/store/session";

export default function NewTccPage() {
  console.log("🎯 COMPONENTE NewTccPage CARREGADO!");
  
  const { showToast } = useToast();
  const router = useRouter();
  const { user } = useSessionStore();
  const [resetForm, setResetForm] = useState(false);

  // Prefetch TCCs list page
  useEffect(() => {
    router.prefetch("/tccs");
  }, [router]);

  const { mutate: create, isLoading: isCreating } = useMutation({
    mutationFn: (data: TccCreateRequest) => {
      console.log("🌐 FAZENDO REQUISIÇÃO PARA CRIAR TCC:", data);
      return createTcc(data);
    },
    onSuccess: (tcc, variables) => {
      console.log("✅ TCC CRIADO COM SUCESSO!", tcc);
      console.log("👤 Papel do usuário:", user?.papel);
      
      if (user?.papel === "ALUNO") {
        console.log("🎓 Usuário é ALUNO, verificando dados do formulário...");
        // Para alunos, enviar convite após criar TCC
        const formData = JSON.parse(sessionStorage.getItem('tccFormData') || '{}');
        console.log("📋 Dados do formulário:", formData);
        
        if (formData.orientadorId && formData.mensagemOrientador) {
          const convitePayload: EnviarConviteRequest = {
            orientadorId: formData.orientadorId,
            tccId: tcc.id,
            mensagem: formData.mensagemOrientador
          };
          
          console.log("📨 Enviando convite:", convitePayload);
          
          enviarConviteMutation({ 
            alunoId: user.id, 
            data: convitePayload 
          });
        } else {
          console.log("❌ Dados do formulário incompletos, não enviando convite");
          showToast("TCC criado com sucesso!", "success");
          sessionStorage.removeItem('tccFormData');
          setResetForm(true);
          setTimeout(() => setResetForm(false), 100);
        }
      } else {
        console.log("👨‍🏫 Usuário não é aluno, redirecionando...");
        showToast("TCC criado com sucesso!", "success");
        router.push("/tccs");
      }
    },
    onError: (error) => {
      console.error("❌ ERRO AO CRIAR TCC:", error);
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const { mutate: enviarConviteMutation, isLoading: isEnviandoConvite } = useMutation({
    mutationFn: ({ alunoId, data }: { alunoId: string; data: EnviarConviteRequest }) => {
      console.log("🌐 FAZENDO REQUISIÇÃO PARA ENVIAR CONVITE:", { alunoId, data });
      return enviarConvite(alunoId, data);
    },
    onSuccess: (result) => {
      console.log("✅ CONVITE ENVIADO COM SUCESSO!", result);
      showToast("Solicitação enviada com sucesso! O orientador receberá uma notificação.", "success");
      sessionStorage.removeItem('tccFormData'); // Limpar dados temporários
      // Resetar formulário para permitir nova solicitação
      setResetForm(true);
      setTimeout(() => setResetForm(false), 100); // Reset flag after form is reset
    },
    onError: (error) => {
      console.error("❌ ERRO AO ENVIAR CONVITE:", error);
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const handleFormSubmit = (data: TccFormInputs) => {
    console.log("🚀 FORMULÁRIO SUBMETIDO!", data);
    console.log("👤 Usuário:", user);
    
    // Para alunos, salvar dados do formulário para usar depois
    if (user?.papel === "ALUNO") {
      console.log("📝 Salvando dados do formulário no sessionStorage");
      sessionStorage.setItem('tccFormData', JSON.stringify(data));
    }
    
    // Para alunos, criar TCC com orientador e depois enviar convite
    const tccPayload = { 
      ...data, 
      dataInicio: new Date(data.dataInicio).toISOString(),
      orientadorId: data.orientadorId, // Sempre incluir orientadorId
    };
    
    console.log("📦 Payload do TCC:", tccPayload);
    console.log("🔄 Chamando createTcc...");
    
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
      <TccForm 
        onSubmit={handleFormSubmit} 
        isSubmitting={isCreating} 
        onReset={resetForm ? () => {} : undefined}
      />
    </div>
  );
}