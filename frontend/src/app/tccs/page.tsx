"use client";

import { useEffect, useMemo } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listTccs, removeTcc } from "@/services/tccs";
import { TccTable } from "@/components/tccs/TccTable";
import { Pagination } from "@/components/Pagination";
import { useQueryParams } from "@/hooks/useQueryParams";
import { Spinner } from "@/components/ui/Spinner";
import { Button } from "@/components/ui/Button";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useSessionStore } from "@/store/session";
import { PapelUsuario } from "@/interfaces";
import { getCronogramaResumos } from "@/services/cronograma";
import { useTccNotificationsStore, selectTotalPendingTccs } from "@/store/tccNotifications";
import { Badge } from "@/components/ui/Badge";

export default function TccsPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const { searchParams, setQueryParams } = useQueryParams();
  const { user } = useSessionStore();
  const page = Number(searchParams.get("page") ?? 1) - 1;
  const setNotificationCounts = useTccNotificationsStore((state) => state.setCounts);
  const totalPendingNotifications = useTccNotificationsStore(selectTotalPendingTccs);

  const { data, isLoading } = useQuery({
    queryKey: ["tccs", { page }],
    queryFn: () => listTccs({ page }),
  });

  const tccIds = useMemo(
    () => (data?.content ?? []).map((tcc) => tcc.id),
    [data?.content],
  );

  const { data: cronogramaResumos } = useQuery({
    queryKey: ["tccs", "cronograma-resumos", tccIds],
    queryFn: () => getCronogramaResumos(tccIds),
    enabled: tccIds.length > 0,
    staleTime: 60_000,
  });

  useEffect(() => {
    if (tccIds.length === 0) {
      setNotificationCounts({});
    }
  }, [tccIds, setNotificationCounts]);

  useEffect(() => {
    if (cronogramaResumos) {
      const counts: Record<string, number> = {};
      Object.entries(cronogramaResumos).forEach(([id, resumo]) => {
        counts[id] = resumo?.pendentes ?? 0;
      });
      setNotificationCounts(counts);
    }
  }, [cronogramaResumos, setNotificationCounts]);

  const { mutate: remove } = useMutation({
    mutationFn: (id: string) => removeTcc(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["tccs"] });
      showToast("TCC excluído com sucesso!", "success");
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <h1 className="text-2xl font-bold">TCCs</h1>
          {totalPendingNotifications > 0 && (
            <Badge variant="warning" className="text-xs font-semibold">
              {totalPendingNotifications} pendências
            </Badge>
          )}
        </div>
        {user?.papel === PapelUsuario.ALUNO && (
          <Link href="/tccs/new">
            <Button className="bg-emerald-500 hover:bg-emerald-600 text-white shadow-sm">
              Novo TCC
            </Button>
          </Link>
        )}
      </div>

      {isLoading ? (
        <div className="flex justify-center"><Spinner className="h-8 w-8" /></div>
      ) : data && data.content.length > 0 ? (
        <>
            <TccTable tccs={data.content} onEdit={(tcc) => router.push(`/tccs/${tcc.id}`)} onDelete={(id) => remove(id)} />
            <Pagination 
                currentPage={data.number + 1}
                totalPages={data.totalPages}
                goToPage={(p) => setQueryParams({ page: p })}
            />
        </>
      ) : (
        <p>Nenhum TCC encontrado.</p>
      )}
    </div>
  );
}
