"use client";

import { useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/Tabs";
import { Spinner } from "@/components/ui/Spinner";
import { CronogramaTab } from "@/components/tccs/workspace/CronogramaTab";
import { WorkspaceHeader } from "@/components/tccs/workspace/WorkspaceHeader";
import { getTccWorkspaceOverview } from "@/services/tccs";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useSessionStore } from "@/store/session";
import { useTccNotificationsStore } from "@/store/tccNotifications";

interface TccWorkspacePageProps {
  params: { id: string };
}

export default function TccWorkspacePage({ params }: TccWorkspacePageProps) {
  const { id } = params;
  const { showToast } = useToast();
  const userRole = useSessionStore((state) => state.user?.papel ?? null);
  const dismissNotification = useTccNotificationsStore((state) => state.dismiss);

  const { data: overview, isLoading, error, refetch, isRefetching } = useQuery({
    queryKey: ["tccs", id, "workspace", "overview"],
    queryFn: () => getTccWorkspaceOverview(id),
    enabled: !!id,
  });

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

      <Tabs defaultValue="cronograma" className="space-y-4">
        <TabsList>
          <TabsTrigger value="cronograma">Cronograma</TabsTrigger>
          <TabsTrigger value="versoes" disabled>
            Versões
          </TabsTrigger>
          <TabsTrigger value="feedbacks" disabled>
            Feedbacks
          </TabsTrigger>
          <TabsTrigger value="mensagens" disabled>
            Mensagens
          </TabsTrigger>
        </TabsList>

        <TabsContent value="cronograma" className="mt-0">
          <CronogramaTab tccId={overview.id} userRole={userRole} />
        </TabsContent>
        <TabsContent value="versoes">
          <div className="rounded-md border border-dashed border-muted-foreground/40 px-6 py-12 text-center text-sm text-muted-foreground">
            A aba de versões será liberada nas próximas sprints.
          </div>
        </TabsContent>
        <TabsContent value="feedbacks">
          <div className="rounded-md border border-dashed border-muted-foreground/40 px-6 py-12 text-center text-sm text-muted-foreground">
            Central de feedbacks em desenvolvimento.
          </div>
        </TabsContent>
        <TabsContent value="mensagens">
          <div className="rounded-md border border-dashed border-muted-foreground/40 px-6 py-12 text-center text-sm text-muted-foreground">
            Mensagens e interações serão implementadas futuramente.
          </div>
        </TabsContent>
      </Tabs>
    </div>
  );
}

