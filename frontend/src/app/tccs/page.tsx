"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listTccs, createTcc, updateTcc, removeTcc } from "@/services/tccs";
import { Tcc, TccCreateRequest, TccUpdateRequest } from "@/interfaces";
import { TccTable } from "@/components/tccs/TccTable";
import { TccForm, TccFormInputs } from "@/components/tccs/TccForm";
import { Button } from "@/components/ui/Button";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/Modal";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { Pagination } from "@/components/Pagination";
import { useQueryParams } from "@/hooks/useQueryParams";
import { Spinner } from "@/components/ui/Spinner";

export default function TccsPage() {
  const queryClient = useQueryClient();
  const { showToast } = useToast();
  const { searchParams, setQueryParams } = useQueryParams();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTcc, setEditingTcc] = useState<Tcc | null>(null);

  const page = Number(searchParams.get("page") ?? 1) - 1;

  const { data, isLoading } = useQuery({
    queryKey: ["tccs", { page }],
    queryFn: () => listTccs({ page }),
  });

  const { mutate: create, isLoading: isCreating } = useMutation(
    (data: TccCreateRequest) => createTcc(data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["tccs"]);
        showToast("TCC criado com sucesso!", "success");
        setIsModalOpen(false);
      },
      onError: (error) => {
        const { message } = handleApiError(error);
        showToast(message, "error");
      },
    }
  );

  const { mutate: update, isLoading: isUpdating } = useMutation(
    ({ id, data }: { id: string; data: TccUpdateRequest }) => updateTcc(id, data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries(["tccs"]);
        showToast("TCC atualizado com sucesso!", "success");
        setIsModalOpen(false);
        setEditingTcc(null);
      },
      onError: (error) => {
        const { message } = handleApiError(error);
        showToast(message, "error");
      },
    }
  );

  const { mutate: remove } = useMutation((id: string) => removeTcc(id), {
    onSuccess: () => {
        queryClient.invalidateQueries(["tccs"]);
        showToast("TCC excluído com sucesso!", "success");
    },
    onError: (error) => {
        const { message } = handleApiError(error);
        showToast(message, "error");
    },
  });

  const handleFormSubmit = (data: TccFormInputs) => {
    const payload = { ...data, dataInicio: new Date(data.dataInicio).toISOString() };
    if (editingTcc) {
      update({ id: editingTcc.id, data: payload });
    } else {
      create(payload);
    }
  };

  const handleEdit = (tcc: Tcc) => {
    setEditingTcc(tcc);
    setIsModalOpen(true);
  };

  const handleDelete = (id: string) => {
    if (window.confirm("Tem certeza que deseja excluir este TCC?")) {
      remove(id);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">TCCs</h1>
        <Dialog open={isModalOpen} onOpenChange={setIsModalOpen}>
          <DialogTrigger asChild>
            <Button onClick={() => setEditingTcc(null)}>Novo TCC</Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>{editingTcc ? "Editar TCC" : "Novo TCC"}</DialogTitle>
            </DialogHeader>
            <TccForm
              onSubmit={handleFormSubmit}
              defaultValues={editingTcc ?? undefined}
              isSubmitting={isCreating || isUpdating}
            />
          </DialogContent>
        </Dialog>
      </div>

      {isLoading ? (
        <div className="flex justify-center"><Spinner className="h-8 w-8" /></div>
      ) : data && data.content.length > 0 ? (
        <>
            <TccTable tccs={data.content} onEdit={handleEdit} onDelete={handleDelete} />
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
