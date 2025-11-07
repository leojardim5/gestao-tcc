"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { cn } from "@/lib/utils";
import { useSessionStore } from "@/store/session";
import { isOrientador } from "@/utils/guards";
import { Book, Home, Users, Bell, Calendar, Settings } from "lucide-react";
import { listNotificacoes } from "@/services/notificacoes";

const navLinks = [
  { href: "/dashboard", label: "Dashboard", icon: Home, allowed: () => true },
  { href: "/tccs", label: "TCCs", icon: Book, allowed: () => true },
  { href: "/reunioes", label: "Reuniões", icon: Calendar, allowed: () => true },
  { href: "/notificacoes", label: "Notificações", icon: Bell, allowed: () => true },
  { href: "/orientadores", label: "Orientadores", icon: Users, allowed: () => true },
  { href: "/orientador", label: "Painel Orientador", icon: Settings, allowed: (papel) => isOrientador(papel) },
];

export function Sidebar() {
  const pathname = usePathname();
  const { user } = useSessionStore();
  const { data: unreadNotificacoes } = useQuery({
    queryKey: ["notificacoes", { usuarioId: user?.id, lidas: false }, "sidebar"],
    queryFn: () => listNotificacoes({ usuarioId: user?.id || "", lidas: false }),
    enabled: !!user?.id,
    refetchInterval: 30000,
  });
  const unreadCount = unreadNotificacoes?.length ?? 0;

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
          const showBadge = link.href === "/notificacoes" && unreadCount > 0;
          return (
            <Link
              key={link.href}
              href={link.href}
              className={cn(
                "flex items-center justify-between rounded-lg px-3 py-2 text-muted-foreground transition-all hover:text-primary",
                isActive && "bg-muted text-primary"
              )}
            >
              <span className="flex items-center gap-3">
                <link.icon className="h-5 w-5" />
                {link.label}
              </span>
              {showBadge && (
                <span className="ml-3 inline-flex min-w-[1.5rem] items-center justify-center rounded-full bg-red-500 px-2 py-1 text-xs font-semibold text-white">
                  {unreadCount > 99 ? "99+" : unreadCount}
                </span>
              )}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
