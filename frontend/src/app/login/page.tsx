"use client";

import { z } from "zod";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSessionStore } from "@/store/session";
import { Button } from "@/components/ui/Button";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/Card";
import { Input } from "@/components/ui/Input";
import { Label } from "@/components/ui/Label";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/Select";
import { PapelUsuario } from "@/interfaces";

const loginSchema = z.object({
  email: z.string().email({ message: "Email inválido" }),
  nome: z.string().min(1, { message: "Nome é obrigatório" }),
  papel: z.nativeEnum(PapelUsuario),
});

type LoginFormInputs = z.infer<typeof loginSchema>;

export default function LoginPage() {
  const router = useRouter();
  const loginFake = useSessionStore((state) => state.loginFake);

  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<LoginFormInputs>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "aluno@example.com",
      nome: "Aluno Fulano",
      papel: PapelUsuario.ALUNO,
    },
  });

  const onSubmit = (data: LoginFormInputs) => {
    loginFake(data);
    router.push("/dashboard");
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-background">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle className="text-2xl">Login</CardTitle>
          <CardDescription>
            Entre com um email e selecione um papel para simular o login.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="nome">Nome</Label>
              <Input id="nome" {...register("nome")} />
              {errors.nome && <p className="text-sm font-medium text-destructive">{errors.nome.message}</p>}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" {...register("email")} />
              {errors.email && <p className="text-sm font-medium text-destructive">{errors.email.message}</p>}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="papel">Papel</Label>
              <Controller
                name="papel"
                control={control}
                render={({ field }) => (
                  <Select onValueChange={field.onChange} defaultValue={field.value}>
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione um papel" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value={PapelUsuario.ALUNO}>Aluno</SelectItem>
                      <SelectItem value={PapelUsuario.ORIENTADOR}>Orientador</SelectItem>
                      <SelectItem value={PapelUsuario.COORDENADOR}>Coordenador</SelectItem>
                    </SelectContent>
                  </Select>
                )}
              />
              {errors.papel && <p className="text-sm font-medium text-destructive">{errors.papel.message}</p>}
            </div>
            <Button type="submit" className="w-full">Entrar</Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
