"use client";

import { Badge } from "@/components/ui/Badge";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/Card";
import { StatusTcc, TccWorkspaceOverview } from "@/interfaces";
import { formatDate } from "@/utils/date";
import Link from "next/link";

interface WorkspaceHeaderProps {
  overview: TccWorkspaceOverview;
}

const statusLabel: Record<StatusTcc, string> = {
  [StatusTcc.RASCUNHO]: "Rascunho",
  [StatusTcc.PENDENTE_APROVACAO]: "Pendente de Aprovação",
  [StatusTcc.EM_ANDAMENTO]: "Em Andamento",
  [StatusTcc.AGUARDANDO_DEFESA]: "Aguardando Defesa",
  [StatusTcc.CONCLUIDO]: "Concluído",
};

const statusVariant: Record<StatusTcc, "default" | "secondary" | "destructive" | "warning" | "success"> = {
  [StatusTcc.RASCUNHO]: "secondary",
  [StatusTcc.PENDENTE_APROVACAO]: "warning",
  [StatusTcc.EM_ANDAMENTO]: "success",
  [StatusTcc.AGUARDANDO_DEFESA]: "default",
  [StatusTcc.CONCLUIDO]: "secondary",
};

const InfoGroup = ({ title, children }: { title: string; children: React.ReactNode }) => (
  <div className="space-y-1">
    <p className="text-xs font-medium uppercase tracking-wide text-muted-foreground">{title}</p>
    <div className="text-sm text-foreground">{children ?? "—"}</div>
  </div>
);

export const WorkspaceHeader = ({ overview }: WorkspaceHeaderProps) => {
  return (
    <Card>
      <CardHeader className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <CardTitle className="text-2xl font-semibold">{overview.titulo}</CardTitle>
          <CardDescription className="mt-1 text-base">{overview.tema}</CardDescription>
        </div>
        <Badge variant={statusVariant[overview.status]}>{statusLabel[overview.status]}</Badge>
      </CardHeader>
      <CardContent className="space-y-6">
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          <InfoGroup title="Curso">
            {overview.curso}
          </InfoGroup>
          <InfoGroup title="Aluno">
            {overview.aluno ? (
              <div className="space-y-0.5">
                <p>{overview.aluno.nome}</p>
                <p className="text-xs text-muted-foreground">{overview.aluno.email}</p>
              </div>
            ) : "—"}
          </InfoGroup>
          <InfoGroup title="Orientador">
            {overview.orientador ? (
              <div className="space-y-0.5">
                <p>{overview.orientador.nome}</p>
                <p className="text-xs text-muted-foreground">{overview.orientador.email}</p>
              </div>
            ) : "—"}
          </InfoGroup>
          <InfoGroup title="Coorientador">
            {overview.coorientador ? (
              <div className="space-y-0.5">
                <p>{overview.coorientador.nome}</p>
                <p className="text-xs text-muted-foreground">{overview.coorientador.email}</p>
              </div>
            ) : "—"}
          </InfoGroup>
          <InfoGroup title="Início">
            {overview.dataInicio ? formatDate(overview.dataInicio) : "—"}
          </InfoGroup>
          <InfoGroup title="Entrega Prevista">
            {overview.dataEntregaPrevista ? formatDate(overview.dataEntregaPrevista) : "—"}
          </InfoGroup>
        </div>

        {overview.googleDocsVinculado && overview.googleWebViewLink && (
          <div className="flex items-center justify-between gap-4 rounded-md border border-primary/40 bg-primary/5 px-4 py-3 text-sm">
            <div className="text-primary">
              Documento do TCC vinculado no Google Drive.
            </div>
            <Link
              href={overview.googleWebViewLink}
              target="_blank"
              rel="noopener noreferrer"
              className="text-sm font-medium text-primary underline hover:text-primary/80"
            >
              Abrir documento
            </Link>
          </div>
        )}
      </CardContent>
    </Card>
  );
};

