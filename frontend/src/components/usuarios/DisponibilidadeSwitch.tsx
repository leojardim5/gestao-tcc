"use client";

import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { updateDisponibilidade } from "@/services/usuarios";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useSessionStore } from "@/store/session";

interface DisponibilidadeSwitchProps {
  userId: string;
  initialValue: boolean;
}

export function DisponibilidadeSwitch({ userId, initialValue }: DisponibilidadeSwitchProps) {
  const [isAvailable, setIsAvailable] = useState(initialValue);
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const { user, setSession } = useSessionStore();

  const { mutate: updateAvailability, isPending } = useMutation({
    mutationFn: (disponivel: boolean) => updateDisponibilidade(userId, disponivel),
    onSuccess: (updatedUser) => {
      queryClient.invalidateQueries({ queryKey: ["usuarios"] });
      showToast("Disponibilidade atualizada com sucesso!", "success");
      
      // Update session with new availability
      if (user) {
        setSession(user.token || "", {
          ...user,
          disponivelParaOrientacao: updatedUser.disponivelParaOrientacao || false
        });
      }
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message, "error");
      // Revert the state on error
      setIsAvailable(!isAvailable);
    },
  });

  const handleToggle = () => {
    const newValue = !isAvailable;
    setIsAvailable(newValue);
    updateAvailability(newValue);
  };

  return (
    <div className="flex items-center space-x-2">
      <label className="text-sm font-medium">
        Disponível para orientação:
      </label>
      <button
        onClick={handleToggle}
        disabled={isPending}
        className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
          isAvailable ? "bg-blue-600" : "bg-gray-200"
        }`}
      >
        <span
          className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
            isAvailable ? "translate-x-6" : "translate-x-1"
          }`}
        />
      </button>
      {isPending && (
        <span className="text-sm text-gray-500">Atualizando...</span>
      )}
    </div>
  );
}