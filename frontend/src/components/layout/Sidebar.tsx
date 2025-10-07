"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";
import { useSessionStore } from "@/store/session";
import { isCoordenador, isOrientador } from "@/utils/guards";
import { Book, Home, Users, Bell, FileText, Calendar } from "lucide-react";

const navLinks = [
  { href: "/dashboard", label: "Dashboard", icon: Home, allowed: () => true },
  { href: "/tccs", label: "TCCs", icon: Book, allowed: () => true },
  { href: "/submissoes", label: "Submissões", icon: FileText, allowed: (papel) => isOrientador(papel) || isCoordenador(papel) },
  { href: "/reunioes", label: "Reuniões", icon: Calendar, allowed: () => true },
  { href: "/notificacoes", label: "Notificações", icon: Bell, allowed: () => true },
  { href: "/usuarios", label: "Usuários", icon: Users, allowed: (papel) => isCoordenador(papel) },
];

export function Sidebar() {
  const pathname = usePathname();
  const { user } = useSessionStore();

  return (
    <aside className="hidden w-64 flex-col border-r bg-background p-4 md:flex">
      <div className="mb-8 flex items-center gap-2">
        <Book className="h-8 w-8 text-primary" />
        <h1 className="text-2xl font-bold">Gestão TCC</h1>
      </div>
      <nav className="flex flex-col gap-2">
        {navLinks.map((link) => {
          if (!user || !link.allowed(user.papel)) {
            return null;
          }
          const isActive = pathname.startsWith(link.href);
          return (
            <Link
              key={link.href}
              href={link.href}
              className={cn(
                "flex items-center gap-3 rounded-lg px-3 py-2 text-muted-foreground transition-all hover:text-primary",
                isActive && "bg-muted text-primary"
              )}
            >
              <link.icon className="h-5 w-5" />
              {link.label}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
