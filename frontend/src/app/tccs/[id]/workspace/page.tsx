"use client";

import { useEffect, useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { getTccWorkspaceOverview } from "@/services/tccs";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useTccNotificationsStore } from "@/store/tccNotifications";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/Tabs";
import { Spinner } from "@/components/ui/Spinner";
import { CronogramaTab } from "@/components/tccs/workspace/CronogramaTab";
import { WorkspaceHeader } from "@/components/tccs/workspace/WorkspaceHeader";
import { DocumentTab } from "@/components/tccs/workspace/DocumentTab";
import { MensagensTab } from "@/components/tccs/workspace/MensagensTab";
import { listarMensagens } from "@/services/mensagens";
import { useSessionStore } from "@/store/session";

interface TccWorkspacePageProps {
  params: { id: string };
}

const getLastReadMessageId = (tccId: string): string | null => {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(`tcc_${tccId}_last_read_message`);
};

export default function TccWorkspacePage({ params }: TccWorkspacePageProps) {
  const { id } = params;
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const userRole = useSessionStore((state) => state.user?.papel ?? null);
  const { user } = useSessionStore();
  const dismissNotification = useTccNotificationsStore((state) => state.dismiss);
  const [activeTab, setActiveTab] = useState("trilha-progresso");

  const { data: overview, isLoading, error, refetch, isRefetching } = useQuery({
    queryKey: ["tccs", id, "workspace", "overview"],
    queryFn: () => getTccWorkspaceOverview(id),
    enabled: !!id,
  });

  // Verificar mensagens não lidas (sempre, mas com intervalo maior quando a aba está aberta)
  const { data: mensagens } = useQuery({
    queryKey: ["tccs", id, "mensagens", "check"],
    queryFn: () => listarMensagens(id),
    enabled: !!id,
    refetchInterval: activeTab === "mensagens" ? false : 10000, // Verifica a cada 10 segundos quando a aba não está aberta
  });

  // Calcular mensagens não lidas
  const temMensagensNaoLidas = (() => {
    if (!mensagens || !user || mensagens.length === 0) {
      return false;
    }
    
    // Não mostrar notificação se a aba está aberta
    if (activeTab === "mensagens") {
      return false;
    }
    
    const lastReadId = getLastReadMessageId(id);
    const ultimaMensagem = mensagens[mensagens.length - 1];
    
    // Se não há última mensagem lida, há não lidas se a última não é do usuário
    if (!lastReadId) {
      const temNaoLidas = ultimaMensagem.autorId !== user.id;
      console.log("🔔 [MENSAGENS] Sem última lida. Tem não lidas?", temNaoLidas, "Última mensagem de:", ultimaMensagem.autorNome);
      return temNaoLidas;
    }
    
    // Verificar se há mensagens mais recentes que a última lida
    const lastReadIndex = mensagens.findIndex(m => m.id === lastReadId);
    if (lastReadIndex === -1) {
      // Se não encontrou a última lida, verificar se a última não é do usuário
      const temNaoLidas = ultimaMensagem.autorId !== user.id;
      console.log("🔔 [MENSAGENS] Última lida não encontrada. Tem não lidas?", temNaoLidas);
      return temNaoLidas;
    }
    
    // Verificar se há mensagens após a última lida que não são do usuário
    const mensagensNaoLidas = mensagens.slice(lastReadIndex + 1).filter(m => m.autorId !== user.id);
    const temNaoLidas = mensagensNaoLidas.length > 0;
    console.log("🔔 [MENSAGENS] Verificando não lidas:", {
      totalMensagens: mensagens.length,
      lastReadIndex,
      mensagensNaoLidas: mensagensNaoLidas.length,
      temNaoLidas
    });
    return temNaoLidas;
  })();

  const handleMensagensLidas = () => {
    // Invalidar query para atualizar o badge
    queryClient.invalidateQueries({ queryKey: ["tccs", id, "mensagens", "check"] });
  };

  useEffect(() => {
    if (error) {
      const { message } = handleApiError(error);
      showToast(message || "Não foi possível carregar os dados do workspace.", "error");
    }
  }, [error, showToast]);

  useEffect(() => {
    if (id) {
      dismissNotification(id);
    }
  }, [dismissNotification, id]);

  // Quando sair da aba de mensagens, verificar novamente
  useEffect(() => {
    if (activeTab !== "mensagens") {
      queryClient.invalidateQueries({ queryKey: ["tccs", id, "mensagens", "check"] });
    }
  }, [activeTab, id, queryClient]);

  if (isLoading || isRefetching) {
    return (
      <div className="flex h-full min-h-[60vh] items-center justify-center">
        <Spinner className="h-8 w-8 text-muted-foreground" />
      </div>
    );
  }

  if (!overview) {
    return (
      <div className="space-y-4">
        <div className="rounded-md border border-destructive/30 bg-destructive/10 p-6 text-sm text-destructive">
          Não foi possível carregar as informações do workspace.{" "}
          <button
            onClick={() => refetch()}
            className="font-semibold text-destructive underline-offset-2 hover:underline"
          >
            Tentar novamente
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <WorkspaceHeader overview={overview} />

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="mb-0">
          <TabsTrigger value="trilha-progresso">
            Trilha de Progresso
          </TabsTrigger>
          <TabsTrigger value="documento">
            Documento do TCC
          </TabsTrigger>
          <TabsTrigger value="mensagens" className="relative">
            Mensagens
            {temMensagensNaoLidas && (
              <span className="absolute top-0 right-0 h-2.5 w-2.5 rounded-full bg-red-500 border-2 border-white transform translate-x-1/2 -translate-y-1/2"></span>
            )}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="trilha-progresso" className="p-0">
          <div className="rounded-b-lg border-x border-b border-border bg-background p-6 shadow-sm -mt-[1px]">
            <CronogramaTab tccId={overview.id} userRole={userRole} />
          </div>
        </TabsContent>
        <TabsContent value="documento" className="p-0">
          <div className="rounded-b-lg border-x border-b border-border bg-background p-6 shadow-sm -mt-[1px]">
            <DocumentTab tccId={overview.id} overview={overview} />
          </div>
        </TabsContent>
        <TabsContent value="mensagens" className="p-0">
          <div className="rounded-b-lg border-x border-b border-border bg-background p-6 shadow-sm -mt-[1px]">
            <MensagensTab tccId={overview.id} onMensagensLidas={handleMensagensLidas} />
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}

