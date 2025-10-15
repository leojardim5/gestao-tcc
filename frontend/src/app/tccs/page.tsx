"use client";

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

export default function TccsPage() {
  const router = useRouter();
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const { searchParams, setQueryParams } = useQueryParams();
  const page = Number(searchParams.get("page") ?? 1) - 1;

  const { data, isLoading } = useQuery({
    queryKey: ["tccs", { page }],
    queryFn: () => listTccs({ page }),
  });

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
        <h1 className="text-2xl font-bold">TCCs</h1>
        <Link href="/tccs/new">
          <Button>Novo TCC</Button>
        </Link>
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
