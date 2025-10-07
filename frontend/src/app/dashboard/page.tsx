"use client";

import { useQuery } from "@tanstack/react-query";
import { listTccs } from "@/services/tccs";
import { listNotificacoes } from "@/services/notificacoes";
import { useSessionStore } from "@/store/session";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/Card";
import { Spinner } from "@/components/ui/Spinner";
import { Book, Bell, FileText } from "lucide-react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function DashboardPage() {
  const { user } = useSessionStore();
  const router = useRouter();

  // A fake user ID is needed for services that require it.
  // In a real app, this would come from the logged-in user.
  const fakeUserId = "f79a8a5d-3c20-4e38-bedf-2743e634f82b";

  useEffect(() => {
    if (!user) {
      router.push("/login");
    }
  }, [user, router]);

  const { data: tccsData, isLoading: isLoadingTccs } = useQuery({
    queryKey: ["tccs", { size: 5 }],
    queryFn: () => listTccs({ size: 5 }),
    enabled: !!user,
  });

  const { data: notificacoesData, isLoading: isLoadingNotificacoes } = useQuery({
    queryKey: ["notificacoes", { usuarioId: fakeUserId, lidas: false }],
    queryFn: () => listNotificacoes({ usuarioId: fakeUserId, lidas: false }),
    enabled: !!user,
  });

  if (!user) {
    return <Spinner className="h-8 w-8" />;
  }

  return (
    <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">TCCs em Andamento</CardTitle>
          <Book className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isLoadingTccs ? (
            <Spinner />
          ) : (
            <div className="text-2xl font-bold">{tccsData?.totalElements ?? 0}</div>
          )}
          <p className="text-xs text-muted-foreground">Total de TCCs cadastrados</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Notificações Não Lidas</CardTitle>
          <Bell className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {isLoadingNotificacoes ? (
            <Spinner />
          ) : (
            <div className="text-2xl font-bold">{notificacoesData?.length ?? 0}</div>
          )}
          <p className="text-xs text-muted-foreground">Você tem novas notificações</p>
        </CardContent>
      </Card>
      <Card>
        <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
          <CardTitle className="text-sm font-medium">Submissões Recentes</CardTitle>
          <FileText className="h-4 w-4 text-muted-foreground" />
        </CardHeader>
        <CardContent>
          {/* This would require a specific endpoint or more complex client-side filtering */}
          <div className="text-2xl font-bold">0</div>
          <p className="text-xs text-muted-foreground">Nenhuma submissão nos últimos 7 dias</p>
        </CardContent>
      </Card>
    </div>
  );
}
