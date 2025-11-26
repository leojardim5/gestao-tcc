"use client";

import { Tcc, StatusTcc } from "@/interfaces";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/Table";
import { Button } from "@/components/ui/Button";
import { Badge } from "@/components/ui/Badge";
import { formatDate } from "@/utils/date";
import { MoreHorizontal, FileText } from "lucide-react";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger } from "@/components/ui/DropdownMenu";
import Link from "next/link";
import { useTccNotificationsStore, selectPendingCountForTcc } from "@/store/tccNotifications";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

interface TccTableProps {
  tccs: Tcc[];
  onEdit: (tcc: Tcc) => void;
  onDelete: (id: string) => void;
}

const statusVariant: { [key in StatusTcc]: "default" | "secondary" | "destructive" | "warning" | "success" } = {
    [StatusTcc.RASCUNHO]: "secondary",
    [StatusTcc.PENDENTE_APROVACAO]: "warning", // Amarelo para pendente
    [StatusTcc.EM_ANDAMENTO]: "success", // Verde para aceito/em andamento
    [StatusTcc.AGUARDANDO_DEFESA]: "default",
    [StatusTcc.CONCLUIDO]: "secondary",
};

export function TccTable({ tccs, onEdit, onDelete }: TccTableProps) {
  const router = useRouter();

  // Prefetch all workspace pages when TCCs are loaded
  useEffect(() => {
    tccs.forEach((tcc) => {
      router.prefetch(`/tccs/${tcc.id}/workspace`);
    });
  }, [tccs, router]);

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
        {tccs.map((tcc) => {
          const workspaceUrl = `/tccs/${tcc.id}/workspace`;
          return (
            <TableRow key={tcc.id}>
              <TableCell className="font-medium">
                <Link 
                  href={workspaceUrl} 
                  prefetch={true}
                  className="hover:underline"
                >
                  {tcc.titulo}
                </Link>
                <TccNotificationBadge tccId={tcc.id} />
              </TableCell>
            <TableCell>{tcc.alunoNome || 'N/A'}</TableCell>
            <TableCell>{tcc.orientadorNome || 'N/A'}</TableCell>
            <TableCell>
              <Badge variant={statusVariant[tcc.status]}>
                {tcc.status === StatusTcc.PENDENTE_APROVACAO ? 'Pendente' : 
                 tcc.status === StatusTcc.EM_ANDAMENTO ? 'Aceito' :
                 tcc.status.replace(/_/g, ' ')}
              </Badge>
            </TableCell>
            <TableCell>{formatDate(tcc.dataInicio)}</TableCell>
            <TableCell>
              <div className="flex items-center gap-2">
                {/* Ícone para abrir documento do Google Docs */}
                {tcc.googleWebViewLink && (
                  <Button
                    variant="ghost"
                    className="h-8 w-8 p-0"
                    title="Abrir documento do Google Docs"
                    onClick={() => {
                      const docUrl = tcc.googleWebEditLink || tcc.googleWebViewLink;
                      if (docUrl) {
                        window.open(docUrl, '_blank', 'noopener,noreferrer');
                      }
                    }}
                  >
                    <span className="sr-only">Abrir documento</span>
                    <FileText className="h-4 w-4 text-blue-600 hover:text-blue-700" />
                  </Button>
                )}
                {/* Menu de ações (3 pontos) */}
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
                      <Link 
                        href={workspaceUrl}
                        prefetch={true}
                      >
                        Abrir Workspace
                      </Link>
                    </DropdownMenuItem>
                    {tcc.googleWebViewLink && (
                      <DropdownMenuItem
                        onClick={() => {
                          const docUrl = tcc.googleWebEditLink || tcc.googleWebViewLink;
                          if (docUrl) {
                            window.open(docUrl, '_blank', 'noopener,noreferrer');
                          }
                        }}
                      >
                        Abrir Documento
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => onDelete(tcc.id)} className="text-red-600">
                      Excluir
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </TableCell>
          </TableRow>
          );
        })}
      </TableBody>
    </Table>
  );
}

function TccNotificationBadge({ tccId }: { tccId: string }) {
  const pendingCount = useTccNotificationsStore(selectPendingCountForTcc(tccId));

  if (!pendingCount || pendingCount <= 0) {
    return null;
  }

  return (
    <span className="ml-2 inline-flex min-w-[1.5rem] items-center justify-center rounded-full bg-orange-500 px-2 py-0.5 text-[11px] font-semibold text-white">
      {pendingCount > 99 ? "99+" : pendingCount}
    </span>
  );
}