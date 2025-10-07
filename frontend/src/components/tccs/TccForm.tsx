"use client";

import { z } from "zod";
import { useForm, Controller } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Tcc, TccCreateRequest, TccUpdateRequest, Usuario } from "@/interfaces";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/Select";
import { useQuery } from "@tanstack/react-query";
import { listUsuarios } from "@/services/usuarios";
import { PapelUsuario } from "@/interfaces";

const tccSchema = z.object({
  titulo: z.string().min(1, "Título é obrigatório"),
  tema: z.string().min(1, "Tema é obrigatório"),
  curso: z.string().min(1, "Curso é obrigatório"),
  alunoId: z.string().min(1, "Aluno é obrigatório"),
  orientadorId: z.string().min(1, "Orientador é obrigatório"),
  dataInicio: z.string().min(1, "Data de início é obrigatória"),
});

export type TccFormInputs = z.infer<typeof tccSchema>;

interface TccFormProps {
  onSubmit: (data: TccFormInputs) => void;
  defaultValues?: Partial<Tcc>;
  isSubmitting: boolean;
}

export function TccForm({ onSubmit, defaultValues, isSubmitting }: TccFormProps) {
  const { data: alunosData } = useQuery({
    queryKey: ["usuarios", { papel: PapelUsuario.ALUNO }],
    queryFn: () => listUsuarios({ papel: PapelUsuario.ALUNO }),
  });

  const { data: orientadoresData } = useQuery({
    queryKey: ["usuarios", { papel: PapelUsuario.ORIENTADOR }],
    queryFn: () => listUsuarios({ papel: PapelUsuario.ORIENTADOR }),
  });

  const { register, handleSubmit, control, formState: { errors } } = useForm<TccFormInputs>({
    resolver: zodResolver(tccSchema),
    defaultValues: {
      ...defaultValues,
      alunoId: defaultValues?.aluno?.id,
      orientadorId: defaultValues?.orientador?.id,
      dataInicio: defaultValues?.dataInicio?.split('T')[0] // Format for input[type=date]
    },
  });

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
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

      <div className="grid grid-cols-2 gap-4">
        <div className="grid gap-2">
            <Label htmlFor="alunoId">Aluno</Label>
            <Controller
                name="alunoId"
                control={control}
                render={({ field }) => (
                    <Select onValueChange={field.onChange} defaultValue={field.value} disabled={!!defaultValues}>
                        <SelectTrigger><SelectValue placeholder="Selecione um aluno" /></SelectTrigger>
                        <SelectContent>
                            {alunosData?.content.map((aluno) => (
                                <SelectItem key={aluno.id} value={aluno.id}>{aluno.nome}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                )}
            />
            {errors.alunoId && <p className="text-sm font-medium text-destructive">{errors.alunoId.message}</p>}
        </div>
        <div className="grid gap-2">
            <Label htmlFor="orientadorId">Orientador</Label>
            <Controller
                name="orientadorId"
                control={control}
                render={({ field }) => (
                    <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <SelectTrigger><SelectValue placeholder="Selecione um orientador" /></SelectTrigger>
                        <SelectContent>
                            {orientadoresData?.content.map((orientador) => (
                                <SelectItem key={orientador.id} value={orientador.id}>{orientador.nome}</SelectItem>
                            ))}
                        </SelectContent>
                    </Select>
                )}
            />
            {errors.orientadorId && <p className="text-sm font-medium text-destructive">{errors.orientadorId.message}</p>}
        </div>
      </div>

      <div className="grid gap-2">
        <Label htmlFor="dataInicio">Data de Início</Label>
        <Input id="dataInicio" type="date" {...register("dataInicio")} />
        {errors.dataInicio && <p className="text-sm font-medium text-destructive">{errors.dataInicio.message}</p>}
      </div>

      <Button type="submit" disabled={isSubmitting} className="w-full">
        {isSubmitting ? <Spinner className="mr-2 h-4 w-4 animate-spin" /> : null}
        Salvar
      </Button>
    </form>
  );
}
