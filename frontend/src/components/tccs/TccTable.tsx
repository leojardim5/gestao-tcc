"use client";

import { Tcc, StatusTcc } from "@/interfaces";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/Table";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { formatDate } from "@/utils/date";
import { MoreHorizontal } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/DropdownMenu";
import Link from "next/link";

interface TccTableProps {
  tccs: Tcc[];
  onEdit: (tcc: Tcc) => void;
  onDelete: (id: string) => void;
}

const statusVariant: { [key in StatusTcc]: "default" | "secondary" | "destructive" } = {
    [StatusTcc.RASCUNHO]: "secondary",
    [StatusTcc.EM_ANDAMENTO]: "default",
    [StatusTcc.AGUARDANDO_DEFESA]: "default",
    [StatusTcc.CONCLUIDO]: "secondary",
};

export function TccTable({ tccs, onEdit, onDelete }: TccTableProps) {
  return (
    <Table>
      <TableHeader>
        <TableRow>
          <TableHead>Título</TableHead>
          <TableHead>Aluno</TableHead>
          <TableHead>Orientador</TableHead>
          <TableHead>Status</TableHead>
          <TableHead>Início</TableHead>
          <TableHead>Ações</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody>
        {tccs.map((tcc) => (
          <TableRow key={tcc.id}>
            <TableCell className="font-medium">
                <Link href={`/tccs/${tcc.id}`} className="hover:underline">
                    {tcc.titulo}
                </Link>
            </TableCell>
            <TableCell>{tcc.aluno.nome}</TableCell>
            <TableCell>{tcc.orientador.nome}</TableCell>
            <TableCell>
              <Badge variant={statusVariant[tcc.status]}>{tcc.status.replace(/_/g, ' ')}</Badge>
            </TableCell>
            <TableCell>{formatDate(tcc.dataInicio)}</TableCell>
            <TableCell>
              <DropdownMenu>
                <DropdownMenuTrigger asChild>
                  <Button variant="ghost" className="h-8 w-8 p-0">
                    <span className="sr-only">Open menu</span>
                    <MoreHorizontal className="h-4 w-4" />
                  </Button>
                </DropdownMenuTrigger>
                <DropdownMenuContent align="end">
                  <DropdownMenuLabel>Ações</DropdownMenuLabel>
                  <DropdownMenuItem onClick={() => onEdit(tcc)}>Editar</DropdownMenuItem>
                  <DropdownMenuItem asChild>
                    <Link href={`/tccs/${tcc.id}`}>Ver Detalhes</Link>
                  </DropdownMenuItem>
                  <DropdownMenuSeparator />
                  <DropdownMenuItem onClick={() => onDelete(tcc.id)} className="text-red-600">
                    Excluir
                  </DropdownMenuItem>
                </DropdownMenuContent>
              </DropdownMenu>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  );
}
