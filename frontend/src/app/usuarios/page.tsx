"use client";

import { useQuery } from "@tanstack/react-query";
import { listUsuarios } from "@/services/usuarios";
import { Spinner } from "@/components/ui/Spinner";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/Table";
import { Badge } from "@/components/ui/Badge";
import { formatDateTime } from "@/utils/date";

export default function UsuariosPage() {
  const { data, isLoading } = useQuery({
    queryKey: ["usuarios"],
    queryFn: () => listUsuarios(),
  });

  return (
    <div className="space-y-4">
      <h1 className="text-2xl font-bold">Usuários</h1>
      {isLoading ? (
        <div className="flex justify-center"><Spinner /></div>
      ) : data && data.content.length > 0 ? (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Nome</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Papel</TableHead>
              <TableHead>Ativo</TableHead>
              <TableHead>Criado em</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.content.map((usuario) => (
              <TableRow key={usuario.id}>
                <TableCell>{usuario.nome}</TableCell>
                <TableCell>{usuario.email}</TableCell>
                <TableCell>{usuario.papel}</TableCell>
                <TableCell>
                  <Badge variant={usuario.ativo ? "default" : "destructive"}>
                    {usuario.ativo ? "Sim" : "Não"}
                  </Badge>
                </TableCell>
                <TableCell>{formatDateTime(usuario.criadoEm)}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      ) : (
        <p>Nenhum usuário encontrado.</p>
      )}
    </div>
  );
}
