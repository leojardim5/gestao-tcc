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
import { useMutation } from "@tanstack/react-query";
import { login, signup } from "@/services/usuarios";
import { useToast } from "@/hooks/useToast";
import { handleApiError } from "@/services/api";
import { useRouter } from "next/navigation";
import { useSessionStore } from "@/store/session";

const loginSchema = z.object({
  email: z.string().email({ message: "Email inválido" }),
  senha: z.string().min(6, { message: "A senha deve ter no mínimo 6 caracteres" }),
});

const signupSchema = z.object({
  nome: z.string().min(2, { message: "O nome deve ter no mínimo 2 caracteres" }),
  email: z.string().email({ message: "Email inválido" }),
  senha: z.string().min(6, { message: "A senha deve ter no mínimo 6 caracteres" }),
  confirmarSenha: z.string(),
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
    },
  });





  const { mutate: doLogin, isLoading: isLoggingIn } = useMutation({
    mutationFn: login,
    onSuccess: (data) => { // Modified onSuccess to handle AuthResponse
      console.log("AuthResponse data received:", data);
      showToast("Login realizado com sucesso!", "success");
      setSession(data.token, { id: data.id, nome: data.nome, email: data.email, papel: data.papel });
      router.push("/dashboard");
    },
    onError: (error) => {
      console.error("Login mutation error:", error);
      const { message } = handleApiError(error);
      showToast(message, "error");
    },
  });

  const { mutate: doSignup, isLoading: isSigningUp } = useMutation({
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
    <div className="flex min-h-screen items-center justify-center bg-gray-100">
      <div className="w-full max-w-md rounded-lg bg-white p-8 shadow-md">
        <h2 className="mb-6 text-center text-2xl font-bold">
          {isLogin ? "Login" : "Cadastro"}
        </h2>
        {isLogin ? (
          <Form {...loginForm}>
            <form onSubmit={loginForm.handleSubmit(onLoginSubmit)} className="space-y-4">
              <FormField
                control={loginForm.control}
                name="email"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Email</FormLabel>
                    <FormControl>
                      <Input placeholder="seu@email.com" {...field} />
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
                    <FormLabel>Senha</FormLabel>
                    <FormControl>
                      <Input type="password" placeholder="********" {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />
              <Button type="submit" className="w-full" disabled={isLoggingIn}>
                {isLoggingIn ? "Entrando..." : "Entrar"}
              </Button>
            </form>
          </Form>
        ) : (
          <Form {...signupForm}>
            <form onSubmit={signupForm.handleSubmit(onSignupSubmit)} className="space-y-4">
              <div>
                <label htmlFor="signup-nome">Nome</label>
                <input
                  id="signup-nome"
                  type="text"
                  placeholder="Seu Nome"
                  {...signupForm.register("nome")}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                />
                {signupForm.formState.errors.nome && (
                  <p className="text-sm font-medium text-destructive">
                    {signupForm.formState.errors.nome.message}
                  </p>
                )}
              </div>
              <div>
                <label htmlFor="signup-email">Email</label>
                <input
                  id="signup-email"
                  type="email"
                  placeholder="seu@email.com"
                  {...signupForm.register("email")}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                />
                {signupForm.formState.errors.email && (
                  <p className="text-sm font-medium text-destructive">
                    {signupForm.formState.errors.email.message}
                  </p>
                )}
              </div>
              <div>
                <label htmlFor="signup-senha">Senha</label>
                <input
                  id="signup-senha"
                  type="password"
                  placeholder="********"
                  {...signupForm.register("senha")}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                />
                {signupForm.formState.errors.senha && (
                  <p className="text-sm font-medium text-destructive">
                    {signupForm.formState.errors.senha.message}
                  </p>
                )}
              </div>
              <div>
                <label htmlFor="signup-confirmarSenha">Confirmar Senha</label>
                <input
                  id="signup-confirmarSenha"
                  type="password"
                  placeholder="********"
                  {...signupForm.register("confirmarSenha")}
                  className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
                />
                {signupForm.formState.errors.confirmarSenha && (
                  <p className="text-sm font-medium text-destructive">
                    {signupForm.formState.errors.confirmarSenha.message}
                  </p>
                )}
              </div>
              <Button type="submit" className="w-full" disabled={isSigningUp}>
                {isSigningUp ? "Cadastrando..." : "Cadastrar"}
              </Button>
            </form>
          </Form>
        )}
        <div className="mt-4 text-center">
          <Button variant="link" onClick={() => setIsLogin(!isLogin)}>
            {isLogin ? "Não tem uma conta? Cadastre-se" : "Já tem uma conta? Faça login"}
          </Button>
        </div>
      </div>
    </div>
  );
}