"use client";

import { useQuery } from "@tanstack/react-query";
import { listSubmissoesByTcc } from "@/services/submissoes";
import { Spinner } from "@/components/ui/Spinner";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/Table";
import { Badge } from "@/components/ui/Badge";
import { formatDateTime } from "@/utils/date";
import { StatusSubmissao } from "@/interfaces";

interface SubmissoesTabProps {
  tccId: string;
}

const statusVariant: { [key in StatusSubmissao]: "default" | "secondary" | "destructive" } = {
    [StatusSubmissao.EM_REVISAO]: "default",
    [StatusSubmissao.APROVADO]: "secondary",
    [StatusSubmissao.REPROVADO]: "destructive",
};

export function SubmissoesTab({ tccId }: SubmissoesTabProps) {
  const { data, isLoading } = useQuery({
    queryKey: ["submissoes", tccId],
    queryFn: () => listSubmissoesByTcc(tccId),
  });

  if (isLoading) {
    return <div className="flex justify-center"><Spinner /></div>;
  }

  return (
    <div>
      {data && data.length > 0 ? (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Versão</TableHead>
              <TableHead>Título</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Data</TableHead>
              <TableHead>Link</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((submissao) => (
              <TableRow key={submissao.id}>
                <TableCell>{submissao.versao}</TableCell>
                <TableCell>{submissao.titulo}</TableCell>
                <TableCell>
                    <Badge variant={statusVariant[submissao.status]}>{submissao.status.replace(/_/g, ' ')}</Badge>
                </TableCell>
                <TableCell>{formatDateTime(submissao.dataSubmissao)}</TableCell>
                <TableCell>
                  <a href={submissao.arquivoUrl} target="_blank" rel="noopener noreferrer" className="text-blue-600 hover:underline">
                    Acessar
                  </a>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <p>Nenhuma submissão encontrada.</p>
      )}
    </div>
  );
}
