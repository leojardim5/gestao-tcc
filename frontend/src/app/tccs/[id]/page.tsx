"use client";

import { useQuery } from "@tanstack/react-query";
import { getTcc } from "@/services/tccs";
import { Spinner } from "@/components/ui/Spinner";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/Card";
import { Badge } from "@/components/ui/Badge";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/Tabs";
import { SubmissoesTab } from "@/components/tccs/details/SubmissoesTab";
import { ReunioesTab } from "@/components/tccs/details/ReunioesTab";
import { ComentariosTab } from "@/components/tccs/details/ComentariosTab";
import { formatDate } from "@/utils/date";
import { StatusTcc } from "@/interfaces";

interface TccDetailPageProps {
  params: { id: string };
}

const statusVariant: { [key in StatusTcc]: "default" | "secondary" | "destructive" } = {
    [StatusTcc.RASCUNHO]: "secondary",
    [StatusTcc.EM_ANDAMENTO]: "default",
    [StatusTcc.AGUARDANDO_DEFESA]: "default",
    [StatusTcc.CONCLUIDO]: "secondary",
};

export default function TccDetailPage({ params }: TccDetailPageProps) {
  const { id } = params;
  const { data: tcc, isLoading } = useQuery({
    queryKey: ["tcc", id],
    queryFn: () => getTcc(id),
  });

  if (isLoading) {
    return <div className="flex justify-center"><Spinner className="h-8 w-8" /></div>;
  }

  if (!tcc) {
    return <p>TCC não encontrado.</p>;
  }

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <div className="flex items-start justify-between">
            <div>
                <CardTitle className="text-2xl">{tcc.titulo}</CardTitle>
                <CardDescription>Curso de {tcc.curso}</CardDescription>
            </div>
            <Badge variant={statusVariant[tcc.status]}>{tcc.status.replace(/_/g, ' ')}</Badge>
          </div>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4">
            <div><strong>Aluno:</strong> {tcc.aluno.nome}</div>
            <div><strong>Orientador:</strong> {tcc.orientador.nome}</div>
            <div><strong>Tema:</strong> {tcc.tema}</div>
            <div><strong>Data de Início:</strong> {formatDate(tcc.dataInicio)}</div>
        </CardContent>
      </Card>

      <Tabs defaultValue="submissoes">
        <TabsList>
          <TabsTrigger value="submissoes">Submissões</TabsTrigger>
          <TabsTrigger value="reunioes">Reuniões</TabsTrigger>
          <TabsTrigger value="comentarios">Comentários</TabsTrigger>
        </TabsList>
        <TabsContent value="submissoes">
          <SubmissoesTab tccId={id} />
        </TabsContent>
        <TabsContent value="reunioes">
          <ReunioesTab tccId={id} />
        </TabsContent>
        <TabsContent value="comentarios">
          <ComentariosTab tccId={id} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
