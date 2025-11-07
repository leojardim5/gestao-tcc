"use client";

import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/Modal";
import { Badge } from "@/components/ui/Badge";
import { Button } from "@/components/ui/Button";
import { IaSuggestionItem, Usuario } from "@/interfaces";
import { Sparkles, CheckCircle2 } from "lucide-react";
import { cn } from "@/lib/utils";
import React from "react";

interface OrientadorSuggestionDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  isLoading: boolean;
  sugestoes: IaSuggestionItem[];
  mensagemSistema?: string;
  modelo?: string;
  orientadoresDetalhes?: Record<string, Usuario>;
  onSelect: (orientadorId: string) => void;
}

export function OrientadorSuggestionDialog({
  open,
  onOpenChange,
  isLoading,
  sugestoes,
  mensagemSistema,
  modelo,
  orientadoresDetalhes = {},
  onSelect,
}: OrientadorSuggestionDialogProps) {
  const renderScoreColor = (score: number) => {
    if (score >= 80) return "from-emerald-400 to-emerald-600";
    if (score >= 60) return "from-lime-400 to-emerald-500";
    if (score >= 40) return "from-amber-400 to-orange-500";
    return "from-rose-500 to-red-600";
  };

  const renderScoreTextColor = (score: number) => {
    if (score >= 80) return "text-emerald-600";
    if (score >= 60) return "text-lime-600";
    if (score >= 40) return "text-amber-600";
    return "text-rose-600";
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl border-none bg-gradient-to-br from-slate-900 via-slate-900 to-slate-800 text-slate-100 shadow-2xl">
        <DialogHeader>
          <DialogTitle className="flex items-center gap-3 text-2xl font-bold">
            <span className="inline-flex h-10 w-10 items-center justify-center rounded-full bg-emerald-500/20 text-emerald-400">
              <Sparkles className="h-5 w-5" />
            </span>
            Sugestões de Orientadores
          </DialogTitle>
          <DialogDescription className="text-slate-300">
            Analisamos seu TCC e o perfil dos professores disponíveis para indicar os melhores pares.
          </DialogDescription>
        </DialogHeader>

        {mensagemSistema && (
          <div className="mb-4 rounded-lg border border-emerald-400/40 bg-emerald-500/10 px-4 py-3 text-sm text-emerald-200">
            {mensagemSistema}
          </div>
        )}

        {modelo && (
          <span className="mb-4 inline-flex w-fit items-center gap-2 rounded-full border border-slate-700 bg-slate-800 px-3 py-1 text-xs text-slate-300">
            Modelo: {modelo}
          </span>
        )}

        {isLoading ? (
          <div className="flex h-40 flex-col items-center justify-center gap-3 text-slate-300">
            <div className="h-12 w-12 animate-spin rounded-full border-b-2 border-emerald-400" />
            <p>Consultando a IA para encontrar os melhores orientadores…</p>
          </div>
        ) : sugestoes.length === 0 ? (
          <div className="grid place-items-center rounded-xl border border-slate-700 bg-slate-800/60 p-10 text-center">
            <p className="text-lg font-medium text-slate-200">Nenhuma sugestão encontrada.</p>
            <p className="mt-2 text-sm text-slate-400">
              Tente ajustar o tema ou adicionar mais detalhes para uma recomendação mais precisa.
            </p>
          </div>
        ) : (
          <div className="flex max-h-[60vh] flex-col gap-4 overflow-y-auto pr-2">
            {sugestoes.map((sugestao, index) => {
              const orientadorDetalhe = orientadoresDetalhes[sugestao.orientadorId];
              const scoreColor = renderScoreColor(sugestao.score);
              const scoreText = renderScoreTextColor(sugestao.score);

              return (
                <div
                  key={sugestao.orientadorId}
                  className={cn(
                    "relative overflow-hidden rounded-2xl border border-slate-700 bg-slate-800/60 p-6 shadow-lg transition hover:border-emerald-400/40 hover:shadow-emerald-500/10",
                    index === 0 && "ring-2 ring-emerald-500/40"
                  )}
                >
                  <div className="flex flex-wrap items-center justify-between gap-4">
                    <div>
                      <h3 className="text-xl font-semibold text-white">{sugestao.orientadorNome}</h3>
                      <p className="text-sm text-slate-300">
                        {orientadorDetalhe?.perfilOrientador
                          ? orientadorDetalhe.perfilOrientador.substring(0, 180) + (orientadorDetalhe.perfilOrientador.length > 180 ? "…" : "")
                          : "Orientador com perfil ainda não preenchido."}
                      </p>
                    </div>
                    <div className="text-right">
                      <div className={`text-3xl font-bold ${scoreText}`}>{Math.round(sugestao.score)}%</div>
                      <p className="text-xs uppercase tracking-wider text-slate-400">Compatibilidade</p>
                    </div>
                  </div>

                  <div className="mt-4 h-2 w-full overflow-hidden rounded-full bg-slate-700">
                    <div
                      className={cn("h-full rounded-full bg-gradient-to-r", scoreColor)}
                      style={{ width: `${Math.max(5, Math.min(100, sugestao.score))}%` }}
                    />
                  </div>

                  <div className="mt-4 grid gap-3">
                    <p className="text-sm text-slate-200">{sugestao.justificativa}</p>
                    {sugestao.destaques && sugestao.destaques.length > 0 && (
                      <div className="flex flex-wrap gap-2">
                        {sugestao.destaques.slice(0, 6).map((destaque, idx) => (
                          <Badge key={idx} variant="secondary" className="bg-slate-700 text-slate-200">
                            {destaque}
                          </Badge>
                        ))}
                      </div>
                    )}
                  </div>

                  <div className="mt-6 flex flex-wrap items-center justify-between gap-3">
                    <div className="flex items-center gap-2 text-xs uppercase tracking-wider text-slate-400">
                      <CheckCircle2 className="h-4 w-4 text-emerald-400" />
                      Ativo na plataforma
                    </div>
                    <Button
                      onClick={() => onSelect(sugestao.orientadorId)}
                      className="bg-emerald-500 text-white hover:bg-emerald-600"
                    >
                      Selecionar orientador
                    </Button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
}

