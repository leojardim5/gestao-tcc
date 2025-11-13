"use client";

import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Button } from "@/components/ui/Button";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/Form";
import { Input } from "@/components/ui/Input";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/Select";
import { useMutation } from "@tanstack/react-query";
import { login, signup } from "@/services/usuarios";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { useSessionStore } from "@/store/session";
import { PapelUsuario } from "@/interfaces";
import { Book, GraduationCap, User, Mail, Lock, LogIn, UserPlus } from "lucide-react";

const loginSchema = z.object({
  email: z.string().email({ message: "Email inválido" }),
  senha: z.string().min(6, { message: "A senha deve ter no mínimo 6 caracteres" }),
});

const signupSchema = z.object({
  nome: z.string().min(2, { message: "O nome deve ter no mínimo 2 caracteres" }),
  email: z.string().email({ message: "Email inválido" }),
  senha: z.string().min(6, { message: "A senha deve ter no mínimo 6 caracteres" }),
  confirmarSenha: z.string(),
  papel: z.nativeEnum(PapelUsuario, { message: "Papel é obrigatório" }),
}).refine((data) => data.senha === data.confirmarSenha, {
  message: "As senhas não coincidem",
  path: ["confirmarSenha"],
});

type LoginFormValues = z.infer<typeof loginSchema>;
type SignupFormValues = z.infer<typeof signupSchema>;

export default function LoginPage() {
  const [isLogin, setIsLogin] = useState(true);
  const { showToast } = useToast();
  const router = useRouter();
  const { setSession } = useSessionStore();

  const loginForm = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      email: "",
      senha: "",
    },
  });

  const signupForm = useForm<SignupFormValues>({
    resolver: zodResolver(signupSchema), // Uncommented this line
    defaultValues: {
      nome: "",
      email: "",
      senha: "",
      confirmarSenha: "",
      papel: PapelUsuario.ALUNO,
    },
  });





  const { mutate: doLogin, isPending: isLoggingIn } = useMutation({
    mutationFn: login,
    onSuccess: (data) => { // Modified onSuccess to handle AuthResponse
      console.log("AuthResponse data received:", data);
      showToast("Login realizado com sucesso!", "success");
      setSession(data.token, {
        id: data.usuario.id, 
        nome: data.usuario.nome, 
        email: data.usuario.email, 
        papel: data.usuario.papel,
        ativo: data.usuario.ativo,
        disponivelParaOrientacao: data.usuario.disponivelParaOrientacao || false,
        perfilOrientador: data.usuario.perfilOrientador
      });
      router.push("/dashboard");
    },
    onError: (error) => {
      console.error("Login mutation error:", error);
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const { mutate: doSignup, isPending: isSigningUp } = useMutation({
    mutationFn: signup,
    onSuccess: () => { // Modified onSuccess to handle AuthResponse
      showToast("Cadastro realizado com sucesso! Faça o login.", "success");
      setIsLogin(true);
    },
    onError: (error) => {
      console.error("Signup mutation error:", error);
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const onLoginSubmit = (data: LoginFormValues) => {
    try {
      doLogin(data);
    } catch (e) {
      console.error("Synchronous login error:", e);
      showToast("Erro inesperado ao tentar logar.", "error");
    }
  };

  const onSignupSubmit = (data: SignupFormValues) => {
    try {
      doSignup(data);
    } catch (e) {
      console.error("Synchronous signup error:", e);
      showToast("Erro inesperado ao tentar cadastrar.", "error");
    }
  };

  return (
    <div className="flex min-h-screen flex-col lg:flex-row">
      {/* Left Side - Branding */}
      <div className="flex w-full flex-col items-center justify-center bg-gradient-to-br from-slate-800 via-slate-700 to-slate-800 p-8 lg:w-1/2 lg:p-12">
        <div className="max-w-md space-y-8 text-white">
          <div className="space-y-4">
            <div className="flex items-center gap-3">
              <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-white/20 backdrop-blur-sm">
                <Book className="h-10 w-10 text-white" />
              </div>
              <h1 className="text-4xl font-bold tracking-tight lg:text-5xl">Siga-TCC</h1>
            </div>
            <p className="text-base text-white/90 lg:text-lg">
              Plataforma inteligente para acompanhamento e gestão de Trabalhos de Conclusão de Curso
            </p>
          </div>

          <div className="grid grid-cols-2 gap-4 pt-8">
            <div className="flex flex-col items-center gap-3 rounded-lg bg-white/10 p-4 backdrop-blur-sm">
              <GraduationCap className="h-8 w-8 text-white" />
              <p className="text-sm font-medium">Gestão Acadêmica</p>
            </div>
            <div className="flex flex-col items-center gap-3 rounded-lg bg-white/10 p-4 backdrop-blur-sm">
              <User className="h-8 w-8 text-white" />
              <p className="text-sm font-medium">Acompanhamento</p>
            </div>
            <div className="flex flex-col items-center gap-3 rounded-lg bg-white/10 p-4 backdrop-blur-sm">
              <Mail className="h-8 w-8 text-white" />
              <p className="text-sm font-medium">Comunicação</p>
            </div>
            <div className="flex flex-col items-center gap-3 rounded-lg bg-white/10 p-4 backdrop-blur-sm">
              <Lock className="h-8 w-8 text-white" />
              <p className="text-sm font-medium">Segurança</p>
            </div>
          </div>
        </div>
      </div>

      {/* Right Side - Login Form */}
      <div className="flex w-full items-center justify-center bg-slate-50 p-4 lg:w-1/2">
        <div className="w-full max-w-md">

          <div className="rounded-xl border border-slate-200 bg-slate-100 p-8 shadow-lg">
            <div className="mb-6 flex items-center gap-2">
              {isLogin ? (
                <>
                  <LogIn className="h-5 w-5 text-primary" />
                  <h2 className="text-xl font-semibold">Entrar</h2>
                </>
              ) : (
                <>
                  <UserPlus className="h-5 w-5 text-primary" />
                  <h2 className="text-xl font-semibold">Cadastrar</h2>
                </>
              )}
            </div>

            {isLogin ? (
              <Form {...loginForm}>
              <form onSubmit={loginForm.handleSubmit(onLoginSubmit)} className="space-y-5">
                <FormField
                  control={loginForm.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Mail className="h-4 w-4" />
                        Email
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="seu@email.com" {...field} className="h-11" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={loginForm.control}
                  name="senha"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Lock className="h-4 w-4" />
                        Senha
                      </FormLabel>
                      <FormControl>
                        <Input type="password" placeholder="********" {...field} className="h-11" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button type="submit" className="w-full h-11 text-base" disabled={isLoggingIn}>
                  {isLoggingIn ? (
                    <>
                      <span className="mr-2">Entrando...</span>
                    </>
                  ) : (
                    <>
                      <LogIn className="mr-2 h-4 w-4" />
                      Entrar
                    </>
                  )}
                </Button>
              </form>
              </Form>
            ) : (
              <Form {...signupForm}>
              <form onSubmit={signupForm.handleSubmit(onSignupSubmit)} className="space-y-5">
                <FormField
                  control={signupForm.control}
                  name="nome"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <User className="h-4 w-4" />
                        Nome completo
                      </FormLabel>
                      <FormControl>
                        <Input placeholder="Seu Nome" {...field} className="h-11" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <FormField
                  control={signupForm.control}
                  name="email"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <Mail className="h-4 w-4" />
                        Email
                      </FormLabel>
                      <FormControl>
                        <Input type="email" placeholder="seu@email.com" {...field} className="h-11" />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <div className="grid gap-5 sm:grid-cols-2">
                  <FormField
                    control={signupForm.control}
                    name="senha"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="flex items-center gap-2">
                          <Lock className="h-4 w-4" />
                          Senha
                        </FormLabel>
                        <FormControl>
                          <Input type="password" placeholder="********" {...field} className="h-11" />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                  <FormField
                    control={signupForm.control}
                    name="confirmarSenha"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="flex items-center gap-2">
                          <Lock className="h-4 w-4" />
                          Confirmar
                        </FormLabel>
                        <FormControl>
                          <Input type="password" placeholder="********" {...field} className="h-11" />
                        </FormControl>
                        <FormMessage />
                      </FormItem>
                    )}
                  />
                </div>
                <FormField
                  control={signupForm.control}
                  name="papel"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel className="flex items-center gap-2">
                        <GraduationCap className="h-4 w-4" />
                        Tipo de usuário
                      </FormLabel>
                      <Select onValueChange={field.onChange} defaultValue={field.value}>
                        <FormControl>
                          <SelectTrigger className="h-11">
                            <SelectValue placeholder="Selecione seu papel" />
                          </SelectTrigger>
                        </FormControl>
                        <SelectContent>
                          <SelectItem value={PapelUsuario.ALUNO}>Aluno</SelectItem>
                          <SelectItem value={PapelUsuario.ORIENTADOR}>Orientador</SelectItem>
                        </SelectContent>
                      </Select>
                      <FormMessage />
                    </FormItem>
                  )}
                />
                <Button type="submit" className="w-full h-11 text-base" disabled={isSigningUp}>
                  {isSigningUp ? (
                    <>
                      <span className="mr-2">Cadastrando...</span>
                    </>
                  ) : (
                    <>
                      <UserPlus className="mr-2 h-4 w-4" />
                      Criar conta
                    </>
                  )}
                </Button>
              </form>
              </Form>
            )}

            <div className="mt-6 border-t border-border pt-6">
              <Button
                variant="ghost"
                onClick={() => setIsLogin(!isLogin)}
                className="w-full text-sm text-muted-foreground hover:text-foreground"
              >
                {isLogin ? (
                  <>
                    Não tem uma conta?{" "}
                    <span className="ml-1 font-semibold text-primary">Cadastre-se</span>
                  </>
                ) : (
                  <>
                    Já tem uma conta?{" "}
                    <span className="ml-1 font-semibold text-primary">Faça login</span>
                  </>
                )}
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}