"use client";

import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { listUsuarios } from "@/services/usuarios";
import { PapelUsuario, Usuario } from "@/interfaces";
import { Spinner } from "@/components/ui/Spinner";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Mail, CalendarDays, UserCheck } from "lucide-react";
import { format } from "date-fns";
import { ptBR } from "date-fns/locale";

const FETCH_SIZE = 200;

export default function OrientadoresPage() {
  const { data, isLoading, isError } = useQuery({
    queryKey: ["orientadores", FETCH_SIZE],
    queryFn: () => listUsuarios({ size: FETCH_SIZE }),
  });

  const orientadores = useMemo<Usuario[]>(() => {
    if (!data?.content) {
      return [];
    }
    return data.content.filter((usuario) => usuario.papel === PapelUsuario.ORIENTADOR);
  }, [data?.content]);

  return (
    <div className="space-y-8">
      <div>
        <h1 className="text-3xl font-bold">Orientadores</h1>
        <p className="text-muted-foreground">
          Conheça os orientadores cadastrados e verifique quem está disponível para orientar novos projetos.
        </p>
      </div>

      {isLoading ? (
        <div className="flex h-48 items-center justify-center">
          <Spinner />
        </div>
      ) : isError ? (
        <Card className="border-rose-200 bg-rose-50">
          <CardContent className="p-8 text-center text-rose-700">
            Ocorreu um erro ao carregar os orientadores. Tente novamente mais tarde.
          </CardContent>
        </Card>
      ) : orientadores.length === 0 ? (
        <Card className="border-dashed border-muted bg-muted/30">
          <CardContent className="p-10 text-center text-muted-foreground">
            Nenhum orientador cadastrado até o momento.
          </CardContent>
        </Card>
      ) : (
        <div className="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
          {orientadores.map((orientador) => {
            const disponivel = Boolean(orientador.disponivelParaOrientacao);
            const disponibilidadeClasses = disponivel
              ? "border-emerald-200 bg-emerald-50 text-emerald-700"
              : "border-rose-200 bg-rose-50 text-rose-700";

            return (
              <Card key={orientador.id} className="flex h-full flex-col border border-muted">
                <CardHeader className="space-y-2">
                  <CardTitle className="flex items-center justify-between text-xl">
                    <span>{orientador.nome}</span>
                    <Badge className={`${disponibilidadeClasses} font-medium`}>
                      <UserCheck className="mr-1 h-4 w-4" />
                      {disponivel ? "Disponível" : "Indisponível"}
                    </Badge>
                  </CardTitle>
                  <p className="text-sm text-muted-foreground flex items-center gap-2">
                    <Mail className="h-4 w-4" />
                    <a href={`mailto:${orientador.email}`} className="hover:underline">
                      {orientador.email}
                    </a>
                  </p>
                </CardHeader>

                <CardContent className="flex-1 space-y-4">
                  {orientador.perfilOrientador ? (
                    <div className="rounded-md border border-dashed border-primary/20 bg-primary/5 p-4 text-sm text-muted-foreground">
                      {orientador.perfilOrientador}
                    </div>
                  ) : (
                    <p className="text-sm text-muted-foreground italic">
                      Este orientador ainda não adicionou um perfil detalhado.
                    </p>
                  )}
                </CardContent>

                <CardFooter className="flex items-center justify-between text-xs text-muted-foreground">
                  <div className="flex items-center gap-1">
                    <CalendarDays className="h-3.5 w-3.5" />
                    <span>
                      Cadastro em {format(new Date(orientador.criadoEm), "dd MMM yyyy", { locale: ptBR })}
                    </span>
                  </div>
                  <span className="capitalize">{orientador.papel.toLowerCase()}</span>
                </CardFooter>
              </Card>
            );
          })}
        </div>
      )}
    </div>
  );
}

