"use client";

import { useState, useRef, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { listarMensagens, criarMensagem } from "@/services/mensagens";
import { useSessionStore } from "@/store/session";
import { Button } from "@/components/ui/Button";
import { Spinner } from "@/components/ui/Spinner";
import { Send } from "lucide-react";
import { formatDateTime } from "@/utils/date";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";

interface MensagensTabProps {
  tccId: string;
  onMensagensLidas?: () => void;
}

const getLastReadMessageId = (tccId: string): string | null => {
  if (typeof window === "undefined") return null;
  return localStorage.getItem(`tcc_${tccId}_last_read_message`);
};

const setLastReadMessageId = (tccId: string, messageId: string) => {
  if (typeof window === "undefined") return;
  localStorage.setItem(`tcc_${tccId}_last_read_message`, messageId);
};

export function MensagensTab({ tccId, onMensagensLidas }: MensagensTabProps) {
  const { user } = useSessionStore();
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const [conteudo, setConteudo] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const textareaRef = useRef<HTMLTextAreaElement>(null);

  const { data: mensagens, isLoading } = useQuery({
    queryKey: ["tccs", tccId, "mensagens"],
    queryFn: () => listarMensagens(tccId),
    enabled: !!tccId,
    refetchInterval: 10000, // Refetch a cada 10 segundos
  });

  const { mutate: enviarMensagem, isPending: isEnviando } = useMutation({
    mutationFn: (data: { conteudo: string }) => criarMensagem(tccId, data),
    onSuccess: () => {
      setConteudo("");
      queryClient.invalidateQueries({ queryKey: ["tccs", tccId, "mensagens"] });
      if (textareaRef.current) {
        textareaRef.current.style.height = "auto";
      }
    },
    onError: (error) => {
      const { message } = handleApiError(error);
      showToast(message || "Erro ao enviar mensagem", "error");
    },
  });

  // Scroll para o final quando novas mensagens chegarem
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [mensagens]);

  // Marcar mensagens como lidas quando a aba for aberta
  useEffect(() => {
    if (mensagens && mensagens.length > 0 && user) {
      const ultimaMensagem = mensagens[mensagens.length - 1];
      const lastReadId = getLastReadMessageId(tccId);
      
      // Sempre atualizar para a última mensagem quando a aba está aberta
      if (ultimaMensagem.id !== lastReadId) {
        setLastReadMessageId(tccId, ultimaMensagem.id);
        onMensagensLidas?.();
      }
    }
  }, [mensagens, tccId, user, onMensagensLidas]);

  // Auto-resize do textarea
  useEffect(() => {
    if (textareaRef.current) {
      textareaRef.current.style.height = "auto";
      textareaRef.current.style.height = `${Math.min(textareaRef.current.scrollHeight, 80)}px`;
    }
  }, [conteudo]);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!conteudo.trim() || isEnviando) return;

    enviarMensagem({ conteudo: conteudo.trim() });
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSubmit(e);
    }
  };

  const isMinhaMensagem = (autorId: string) => autorId === user?.id;

  if (isLoading) {
    return (
      <div className="flex h-[300px] items-center justify-center">
        <Spinner className="h-6 w-6 text-muted-foreground" />
      </div>
    );
  }

  return (
    <div className="flex flex-col h-[350px] border rounded-md overflow-hidden bg-background">
      {/* Área de mensagens */}
      <div className="flex-1 overflow-y-auto p-1.5 space-y-1 bg-slate-50/50">
        {mensagens && mensagens.length === 0 ? (
          <div className="flex h-full items-center justify-center text-[10px] text-muted-foreground">
            <div className="text-center">
              <p className="font-medium">Nenhuma mensagem ainda</p>
            </div>
          </div>
        ) : (
          mensagens?.map((mensagem) => {
            const minhaMensagem = isMinhaMensagem(mensagem.autorId);
            return (
              <div
                key={mensagem.id}
                className={`flex ${minhaMensagem ? "justify-end" : "justify-start"}`}
              >
                <div
                  className={`max-w-[85%] rounded px-2 py-1 ${
                    minhaMensagem
                      ? "bg-blue-500 text-white"
                      : "bg-white border border-slate-200 text-slate-900"
                  }`}
                >
                  {!minhaMensagem && (
                    <div className="text-[9px] font-semibold mb-0.5 opacity-80 leading-tight">
                      {mensagem.autorNome}
                    </div>
                  )}
                  <div className="text-[11px] whitespace-pre-wrap break-words leading-snug">
                    {mensagem.conteudo}
                  </div>
                  <div
                    className={`text-[9px] mt-0.5 leading-tight ${
                      minhaMensagem ? "text-blue-100" : "text-muted-foreground"
                    }`}
                  >
                    {formatDateTime(mensagem.criadoEm)}
                  </div>
                </div>
              </div>
            );
          })
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Área de input */}
      <form onSubmit={handleSubmit} className="border-t bg-white p-1.5">
        <div className="flex gap-1 items-end">
          <textarea
            ref={textareaRef}
            value={conteudo}
            onChange={(e) => setConteudo(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder="Digite sua mensagem..."
            className="flex-1 min-h-[32px] max-h-[80px] resize-none rounded border border-input bg-background px-2 py-1 text-[11px] ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
            rows={1}
            disabled={isEnviando}
          />
          <Button
            type="submit"
            disabled={!conteudo.trim() || isEnviando}
            size="sm"
            className="h-[32px] w-[32px] p-0 shrink-0"
          >
            {isEnviando ? (
              <Spinner className="h-3 w-3" />
            ) : (
              <Send className="h-3 w-3" />
            )}
          </Button>
        </div>
        <p className="text-[9px] text-muted-foreground mt-0.5 text-right">
          {conteudo.length}/2000
        </p>
      </form>
    </div>
  );
}

