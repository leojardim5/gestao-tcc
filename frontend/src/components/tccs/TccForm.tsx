"use client";

import { z } from "zod";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useMemo, useState } from "react";
import { Tcc, Usuario, IaSuggestionItem } from "@/interfaces";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/Select";
import { useMutation, useQuery } from "@tanstack/react-query";
import { listUsuarios, listOrientadoresDisponiveis } from "@/services/usuarios";
import { sugerirOrientadoresIa } from "@/services/ia";
import { handleApiError } from "@/services/api";
import { PapelUsuario } from "@/interfaces";
import { Spinner } from "@/components/ui/Spinner";
import { Sparkles } from "lucide-react";
import { useToast } from "@/hooks/useToast";

const createTccSchema = (isAluno: boolean) => z.object({
  titulo: z.string().min(1, "Título é obrigatório"),
  tema: z.string().min(1, "Tema é obrigatório"),
  curso: z.string().min(1, "Curso é obrigatório"),
  alunoId: z.string().min(1, "Aluno é obrigatório"),
  orientadorId: z.string().min(1, "Orientador é obrigatório"), // Sempre obrigatório
  dataInicio: z.string().min(1, "Data de início é obrigatória"),
  mensagemOrientador: isAluno 
    ? z.string().min(10, "Mensagem deve ter pelo menos 10 caracteres")
    : z.string().optional(),
});

export type TccFormInputs = z.infer<ReturnType<typeof createTccSchema>>;

interface TccFormProps {
  onSubmit: (data: TccFormInputs) => void;
  defaultValues?: Partial<Tcc>;
  isSubmitting: boolean;
  onReset?: () => void;
}

import { useSessionStore } from "@/store/session";

export function TccForm({ onSubmit, defaultValues, isSubmitting, onReset }: TccFormProps) {
  const { user } = useSessionStore();
  const { showToast } = useToast();

  const isAluno = user?.papel === PapelUsuario.ALUNO;

  const { data: alunosData } = useQuery({
    queryKey: ["usuarios", { papel: PapelUsuario.ALUNO }],
    queryFn: () => listUsuarios({ papel: PapelUsuario.ALUNO }),
    enabled: !isAluno, // Only fetch if the user is not an Aluno
  });

  // BUSCAR ORIENTADORES REAIS DO BACKEND
  const { data: orientadoresData, isLoading: isLoadingOrientadores, error: orientadoresError } = useQuery({
    queryKey: ['orientadores-disponiveis'],
    queryFn: () => listOrientadoresDisponiveis(),
    retry: 1, // Tentar apenas 1 vez
  });

  const { register, handleSubmit, control, reset, watch, formState: { errors }, getValues } = useForm<TccFormInputs>({
    resolver: zodResolver(createTccSchema(isAluno)),
    defaultValues: {
      ...defaultValues,
      alunoId: isAluno ? user.id : defaultValues?.aluno?.id,
      orientadorId: defaultValues?.orientador?.id,
      dataInicio: defaultValues?.dataInicio?.split('T')[0] || new Date().toISOString().split('T')[0] // Data atual como padrão
    },
  });

  // Reset form when onReset is called
  useEffect(() => {
    if (onReset) {
      reset({
        alunoId: isAluno ? user.id : undefined,
        orientadorId: undefined,
        dataInicio: new Date().toISOString().split('T')[0], // Data atual
        titulo: "",
        tema: "",
        curso: "",
        mensagemOrientador: ""
      });
    }
  }, [onReset, reset, isAluno, user?.id]);

  const orientadoresMap = useMemo(() => {
    const map: Record<string, Usuario> = {};
    orientadoresData?.content?.forEach((orientador) => {
      map[orientador.id] = orientador;
    });
    return map;
  }, [orientadoresData?.content]);

  const [iaSuggestions, setIaSuggestions] = useState<IaSuggestionItem[]>([]);
  const [selectOpen, setSelectOpen] = useState(false);

  const { mutate: sugerirOrientadores, isPending: isLoadingIa } = useMutation({
    mutationFn: sugerirOrientadoresIa,
    onSuccess: (data) => {
      console.log("✅ ========== SUCESSO na chamada da IA ==========");
      console.log("📊 Dados recebidos:", data);
      console.log("📝 Sugestões brutas:", data.sugestoes);
      console.log("🤖 Modelo usado:", data.modelo);
      console.log("💬 Mensagem do sistema:", data.mensagemSistema);
      
      const orderedSugestoes = [...(data.sugestoes ?? [])].sort(
        (a, b) => (b.score ?? 0) - (a.score ?? 0)
      );
      console.log("📊 Sugestões ordenadas:", orderedSugestoes);
      
      setIaSuggestions(orderedSugestoes);
      setSelectOpen(true);
      console.log("🎯 Select aberto:", true);
      console.log("📋 Estado atualizado com", orderedSugestoes.length, "sugestões");
      
      if (data.mensagemSistema) {
        showToast(data.mensagemSistema, "info");
      }
      console.log("✅ ========== FIM SUCESSO ==========");
    },
    onError: (error: unknown) => {
      console.error("❌ ========== ERRO na chamada da IA ==========");
      console.error("🚨 Erro completo:", error);
      console.error("📋 Tipo do erro:", typeof error);
      console.error("📋 Erro stringificado:", JSON.stringify(error, null, 2));
      
      const { message } = handleApiError(error);
      console.error("💬 Mensagem de erro tratada:", message);
      
      showToast(message || "Não foi possível obter sugestões da IA.", "error");
      console.error("❌ ========== FIM ERRO ==========");
    },
  });

  const extrairPalavrasChave = (texto: string) => {
    return Array.from(
      new Set(
        texto
          .toLowerCase()
          .split(/[\s,.;:/]+/)
          .filter((token) => token.length > 3)
      )
    ).slice(0, 12);
  };

  const [
    tituloValue,
    temaValue,
    cursoValue,
    mensagemValue,
    alunoIdValue,
    orientadorIdValue,
  ] = watch(["titulo", "tema", "curso", "mensagemOrientador", "alunoId", "orientadorId"]);

  const camposObrigatoriosPreenchidos =
    tituloValue?.trim() &&
    temaValue?.trim() &&
    cursoValue?.trim() &&
    mensagemValue?.trim();

  const mensagemMinimaValida = (mensagemValue?.trim().length ?? 0) >= 10;

  const podeUsarIa =
    Boolean(camposObrigatoriosPreenchidos) &&
    mensagemMinimaValida &&
    (isAluno ? Boolean(user?.id) : Boolean(alunoIdValue));

  const handleSolicitarSugestoes = () => {
    console.log("🚀 ========== INÍCIO handleSolicitarSugestoes ==========");
    const values = getValues();
    console.log("📋 Valores do formulário:", values);
    console.log("👤 Usuário atual:", user);
    console.log("🎓 É aluno?", isAluno);
    
    const alunoIdSelecionado = isAluno ? user.id : values.alunoId;
    console.log("🆔 Aluno ID selecionado:", alunoIdSelecionado);

    if (!alunoIdSelecionado) {
      console.warn("⚠️ Aluno não selecionado!");
      showToast("Selecione um aluno antes de pedir sugestões.", "warning");
      return;
    }

    if (!values.titulo || !values.tema || !values.curso || !values.mensagemOrientador) {
      console.warn("⚠️ Campos obrigatórios não preenchidos:", {
        titulo: !!values.titulo,
        tema: !!values.tema,
        curso: !!values.curso,
        mensagemOrientador: !!values.mensagemOrientador
      });
      showToast("Preencha título, tema, curso e a mensagem/descrição para gerar sugestões.", "warning");
      return;
    }

    if (values.mensagemOrientador.trim().length < 10) {
      console.warn("⚠️ Mensagem muito curta:", values.mensagemOrientador.trim().length);
      showToast("Escreva pelo menos 10 caracteres na mensagem/descrição antes de pedir sugestões da IA.", "warning");
      return;
    }

    const alunoNome = isAluno
      ? user.nome
      : alunosData?.content?.find((aluno) => aluno.id === alunoIdSelecionado)?.nome ?? "Aluno";
    console.log("👨‍🎓 Nome do aluno:", alunoNome);

    const palavrasChave = extrairPalavrasChave(
      [values.tema, values.curso, values.mensagemOrientador].filter(Boolean).join(" ")
    );
    console.log("🔑 Palavras-chave extraídas:", palavrasChave);

    const payload = {
      alunoId: alunoIdSelecionado,
      alunoNome,
      curso: values.curso,
      titulo: values.titulo,
      tema: values.tema,
      resumo: values.mensagemOrientador || values.tema,
      mensagem: values.mensagemOrientador,
      palavrasChave,
    };
    console.log("📦 Payload completo para IA:", payload);
    console.log("🔄 Chamando sugerirOrientadores...");
    
    sugerirOrientadores(payload);
    console.log("✅ ========== FIM handleSolicitarSugestoes ==========");
  };

  const sugestaoPorId = useMemo(() => {
    const map = new Map<string, IaSuggestionItem & { ordem: number }>();
    iaSuggestions.forEach((sugestao, index) => {
      map.set(sugestao.orientadorId, { ...sugestao, ordem: index });
    });
    return map;
  }, [iaSuggestions]);

  const orientadorSelecionado = orientadorIdValue ? orientadoresMap[orientadorIdValue] : undefined;
  const sugestaoSelecionada = orientadorIdValue ? sugestaoPorId.get(orientadorIdValue) : undefined;

  const orientadoresOrdenados = useMemo(() => {
    const lista = orientadoresData?.content ? [...orientadoresData.content] : [];
    return lista.sort((a, b) => {
      const sa = sugestaoPorId.get(a.id);
      const sb = sugestaoPorId.get(b.id);
      if (sa && sb) {
        return sa.ordem - sb.ordem;
      }
      if (sa) return -1;
      if (sb) return 1;
      return a.nome.localeCompare(b.nome);
    });
  }, [orientadoresData?.content, sugestaoPorId]);

  const getScoreColor = (score?: number) => {
    if (score === undefined) return "text-slate-400";
    if (score >= 80) return "text-emerald-400";
    if (score >= 60) return "text-lime-400";
    if (score >= 40) return "text-amber-400";
    return "text-rose-400";
  };

  const getScoreBadgeStyles = (score?: number) => {
    if (score === undefined) return "bg-slate-200 text-slate-500";
    if (score >= 80) return "bg-emerald-500/15 text-emerald-500 border-emerald-500/40";
    if (score >= 60) return "bg-lime-500/15 text-lime-600 border-lime-500/40";
    if (score >= 40) return "bg-amber-500/15 text-amber-600 border-amber-500/40";
    return "bg-rose-500/15 text-rose-500 border-rose-500/40";
  };

  return (
    <form onSubmit={handleSubmit((data) => {
      console.log("📝 FORMULÁRIO SUBMETIDO VIA handleSubmit!", data);
      onSubmit(data);
    })} className="grid gap-4">
      <div className="grid gap-2">
        <Label htmlFor="titulo">Título</Label>
        <Input id="titulo" {...register("titulo")} />
        {errors.titulo && <p className="text-sm font-medium text-destructive">{errors.titulo.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
            <Label htmlFor="tema">Tema</Label>
            <Input id="tema" {...register("tema")} />
            {errors.tema && <p className="text-sm font-medium text-destructive">{errors.tema.message}</p>}
        </div>
        <div className="grid gap-2">
            <Label htmlFor="curso">Curso</Label>
            <Input id="curso" {...register("curso")} />
            {errors.curso && <p className="text-sm font-medium text-destructive">{errors.curso.message}</p>}
        </div>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="dataInicio">Data de Solicitação</Label>
        <Input 
          id="dataInicio" 
          type="date" 
          {...register("dataInicio")} 
          readOnly
          className="bg-gray-50"
        />
        {errors.dataInicio && <p className="text-sm font-medium text-destructive">{errors.dataInicio.message}</p>}
      </div>

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
            <Label htmlFor="alunoId">Aluno</Label>
            {isAluno ? (
              <div>
                <Input defaultValue={user?.nome} disabled />
                <input type="hidden" {...register("alunoId")} value={user?.id} />
              </div>
            ) : (
              <Controller
                  name="alunoId"
                  control={control}
                  render={({ field }) => (
                      <Select onValueChange={field.onChange} defaultValue={field.value} disabled={!!defaultValues}>
                          <SelectTrigger><SelectValue placeholder="Selecione um aluno" /></SelectTrigger>
                          <SelectContent className="max-h-64 overflow-y-auto">
                              {alunosData?.content.map((aluno) => (
                                  <SelectItem key={aluno.id} value={aluno.id}>{aluno.nome}</SelectItem>
                              ))}
                          </SelectContent>
                      </Select>
                  )}
              />
            )}
            {errors.alunoId && <p className="text-sm font-medium text-destructive">{errors.alunoId.message}</p>}
        </div>
        <div className="grid gap-2">
            <div className="flex items-center justify-between gap-2">
              <Label htmlFor="orientadorId">Orientador</Label>
              <Button
                type="button"
                variant="outline"
                size="sm"
                className="flex items-center gap-2 border-emerald-500/50 text-emerald-500 hover:border-emerald-500 hover:text-emerald-600"
                onClick={handleSolicitarSugestoes}
                disabled={
                  isLoadingIa ||
                  !podeUsarIa ||
                  isLoadingOrientadores ||
                  !!orientadoresError
                }
              >
                {isLoadingIa ? (
                  <>
                    <Spinner className="h-4 w-4" />
                    Consultando…
                  </>
                ) : (
                  <>
                    <Sparkles className="h-4 w-4" />
                    Sugerir orientadores
                  </>
                )}
              </Button>
            </div>
            <Controller
                name="orientadorId"
                control={control}
                render={({ field }) => (
                    <Select
                      onValueChange={(value) => {
                        field.onChange(value);
                        setSelectOpen(false);
                        const orientador = orientadoresMap[value];
                        if (orientador) {
                          showToast(`Orientador ${orientador.nome} selecionado.`, "success");
                        }
                      }}
                      value={field.value}
                      open={selectOpen}
                      onOpenChange={setSelectOpen}
                    >
                        <SelectTrigger className="min-h-[52px] py-2 text-left">
                          {orientadorSelecionado ? (
                            <div className="flex w-full items-center justify-between gap-3 min-w-0">
                              <div className="flex min-w-0 flex-col text-left flex-1">
                                <span className="text-sm font-medium break-words">{orientadorSelecionado.nome}</span>
                                {sugestaoSelecionada?.justificativa && (
                                  <span className="text-[11px] text-muted-foreground line-clamp-1 break-words mt-0.5">
                                    {sugestaoSelecionada.justificativa}
                                  </span>
                                )}
                              </div>
                              {sugestaoSelecionada?.score !== undefined && (
                                <span
                                  className={`shrink-0 inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-xs font-semibold whitespace-nowrap ${getScoreBadgeStyles(sugestaoSelecionada.score)}`}
                                >
                                  <span
                                    className={`h-2 w-2 rounded-full ${getScoreColor(sugestaoSelecionada.score).replace("text", "bg")}`}
                                  />
                                  {`${Math.round(sugestaoSelecionada.score)}%`}
                                </span>
                              )}
                            </div>
                          ) : (
                            <SelectValue
                              placeholder={
                                isLoadingOrientadores 
                                    ? "Carregando orientadores..." 
                                    : "Selecione um orientador"
                              }
                            />
                          )}
                        </SelectTrigger>
                        <SelectContent className="max-h-64 overflow-y-auto min-w-[var(--radix-select-trigger-width)] max-w-[500px]">
                            {isLoadingOrientadores ? (
                                <SelectItem value="loading" disabled>Carregando...</SelectItem>
                            ) : orientadoresError ? (
                                <SelectItem value="error" disabled>Erro ao carregar orientadores</SelectItem>
                            ) : orientadoresOrdenados.length === 0 ? (
                                <SelectItem value="empty" disabled>Nenhum orientador disponível</SelectItem>
                            ) : orientadoresOrdenados.map((orientador) => {
                                const sugestao = sugestaoPorId.get(orientador.id);
                                const scoreTexto = sugestao ? `${Math.round(sugestao.score)}%` : undefined;
                                return (
                                    <SelectItem 
                                        key={orientador.id} 
                                        value={orientador.id}
                                        className="py-2.5"
                                    >
                                        <div className="flex items-start justify-between gap-3 w-full min-w-0">
                                          <div className="flex flex-col text-left min-w-0 flex-1">
                                            <span className="font-medium text-sm mb-1 break-words">{orientador.nome}</span>
                                            {sugestao?.justificativa && (
                                              <span className="text-[11px] text-muted-foreground break-words leading-relaxed">
                                                {sugestao.justificativa}
                                              </span>
                                            )}
                                          </div>
                                          {scoreTexto && (
                                            <span
                                              className={`shrink-0 inline-flex items-center gap-1 rounded-full border px-2 py-0.5 text-xs font-semibold whitespace-nowrap ${getScoreBadgeStyles(sugestao?.score)}`}
                                            >
                                              <span
                                                className={`h-2 w-2 rounded-full ${getScoreColor(sugestao?.score).replace("text", "bg")}`}
                                              />
                                              {scoreTexto}
                                            </span>
                                          )}
                                        </div>
                                    </SelectItem>
                                );
                            })}
                        </SelectContent>
                    </Select>
                )}
            />
            {errors.orientadorId && <p className="text-sm font-medium text-destructive">{errors.orientadorId.message}</p>}
        </div>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="mensagemOrientador">Mensagem para o Orientador</Label>
        <textarea
          id="mensagemOrientador"
          {...register("mensagemOrientador")}
          className="min-h-[100px] w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50"
          placeholder="Escreva uma mensagem para o orientador explicando sobre seu projeto e por que gostaria que ele fosse seu orientador..."
        />
        {errors.mensagemOrientador && <p className="text-sm font-medium text-destructive">{errors.mensagemOrientador.message}</p>}
      </div>

      <Button 
        type="submit" 
        disabled={isSubmitting} 
        className="w-full"
        onClick={(e) => {
          console.log("🔥 BOTÃO CLICADO!");
          console.log("📋 Dados do formulário:", getValues());
          console.log("❌ Erros de validação:", errors);
        }}
      >
        {isSubmitting ? <Spinner className="mr-2 h-4 w-4 animate-spin" /> : null}
        {user?.papel === "ALUNO" ? "Enviar Solicitação" : "Salvar"}
      </Button>
    </form>
  );
}
