"use client";

import { useQuery, useMutation } from "@tanstack/react-query";
import { getTccById, updateTcc } from "@/services/tccs";
import { TccForm, TccFormInputs } from "@/components/tccs/TccForm";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { Spinner } from "@/components/ui/Spinner";
import { TccUpdateRequest } from "@/interfaces";

export default function TccDetailsPage({ params }: { params: { id: string } }) {
  const { id } = params;
  const { showToast } = useToast();
  const router = useRouter();

  const { data: tcc, isLoading } = useQuery({
    queryKey: ["tccs", id],
    queryFn: () => getTccById(id),
    enabled: !!id,
  });

  const { mutate: update, isLoading: isUpdating } = useMutation({
    mutationFn: (data: TccUpdateRequest) => updateTcc(id, data),
    onSuccess: () => {
      showToast("TCC atualizado com sucesso!", "success");
      router.push("/tccs");
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const handleFormSubmit = (data: TccFormInputs) => {
    const payload = { ...data, dataInicio: new Date(data.dataInicio).toISOString() };
    update(payload);
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Editar TCC</h1>

      {isLoading ? (
        <div className="flex justify-center"><Spinner className="h-8 w-8" /></div>
      ) : tcc ? (
        <TccForm
          onSubmit={handleFormSubmit}
          defaultValues={tcc}
          isSubmitting={isUpdating}
        />
      ) : (
        <p>TCC não encontrado.</p>
      )}
    </div>
  );
}