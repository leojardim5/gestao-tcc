"use client";

import { useQuery } from "@tanstack/react-query";
import { listReunioesByTcc } from "@/services/reunioes";
import { Spinner } from "@/components/ui/Spinner";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/Table";
import { formatDateTime } from "@/utils/date";

interface ReunioesTabProps {
  tccId: string;
}

export function ReunioesTab({ tccId }: ReunioesTabProps) {
  const { data, isLoading } = useQuery({
    queryKey: ["reunioes", tccId],
    queryFn: () => listReunioesByTcc(tccId),
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
              <TableHead>Tema</TableHead>
              <TableHead>Data</TableHead>
              <TableHead>Tipo</TableHead>
              <TableHead>Local/Link</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((reuniao) => (
              <TableRow key={reuniao.id}>
                <TableCell>{reuniao.tema}</TableCell>
                <TableCell>{formatDateTime(reuniao.data)}</TableCell>
                <TableCell>{reuniao.tipo}</TableCell>
                <TableCell>{reuniao.link || reuniao.local}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <p>Nenhuma reunião encontrada.</p>
      )}
    </div>
  );
}
