"use client";

import { useMutation } from "@tanstack/react-query";
import { createTcc } from "@/services/tccs";
import { TccForm, TccFormInputs } from "@/components/tccs/TccForm";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { TccCreateRequest } from "@/interfaces";

export default function NewTccPage() {
  const { showToast } = useToast();
  const router = useRouter();

  const { mutate: create, isLoading: isCreating } = useMutation({
    mutationFn: (data: TccCreateRequest) => createTcc(data),
    onSuccess: () => {
      showToast("TCC criado com sucesso!", "success");
      router.push("/tccs");
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const handleFormSubmit = (data: TccFormInputs) => {
    const payload = { ...data, dataInicio: new Date(data.dataInicio).toISOString() };
    create(payload);
  };

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Novo TCC</h1>
      <TccForm onSubmit={handleFormSubmit} isSubmitting={isCreating} />
    </div>
  );
}
