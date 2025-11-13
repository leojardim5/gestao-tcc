"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { cn } from "@/lib/utils";
import { useSessionStore } from "@/store/session";
import { isOrientador } from "@/utils/guards";
import { Book, Home, Users, Bell, Calendar, Settings } from "lucide-react";
import { listNotificacoes } from "@/services/notificacoes";
import { useTccNotificationsStore, selectTotalPendingTccs } from "@/store/tccNotifications";
import { useEffect } from "react";

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
  const router = useRouter();
  const { user } = useSessionStore();
  const totalTccPendencias = useTccNotificationsStore(selectTotalPendingTccs);
  const { data: unreadNotificacoes } = useQuery({
    queryKey: ["notificacoes", { usuarioId: user?.id, lidas: false }, "sidebar"],
    queryFn: () => listNotificacoes({ usuarioId: user?.id || "", lidas: false }),
    enabled: !!user?.id,
    refetchInterval: 30000,
  });
  const unreadCount = unreadNotificacoes?.length ?? 0;

  // Prefetch all navigation links when component mounts
  useEffect(() => {
    if (user) {
      navLinks.forEach((link) => {
        if (link.allowed(user.papel)) {
          router.prefetch(link.href);
        }
      });
    }
  }, [user, router]);

  return (
    <aside className="fixed left-0 top-0 z-40 hidden h-screen w-64 flex-col border-r border-slate-800 bg-slate-800 shadow-sm md:flex">
      <div className="mb-8 flex items-center gap-2 p-4">
        <Book className="h-8 w-8 text-blue-400" />
        <h1 className="text-2xl font-bold text-white">Siga-TCC</h1>
      </div>
      <nav className="flex flex-col gap-2 overflow-y-auto px-4 pb-4">
        {navLinks.map((link) => {
          if (!user || !link.allowed(user.papel)) {
            return null;
          }
          const isActive = pathname.startsWith(link.href);
          const showBadge =
            (link.href === "/notificacoes" && unreadCount > 0) ||
            (link.href === "/tccs" && totalTccPendencias > 0);
          const badgeCount =
            link.href === "/notificacoes"
              ? unreadCount
              : totalTccPendencias;
          return (
            <Link
              key={link.href}
              href={link.href}
              prefetch={true}
              className={cn(
                "flex items-center justify-between rounded-lg px-3 py-2 text-slate-300 transition-all hover:bg-slate-700 hover:text-white",
                isActive && "bg-blue-600 text-white"
              )}
            >
              <span className="flex items-center gap-3">
                <link.icon className="h-5 w-5" />
                {link.label}
              </span>
              {showBadge && (
                <span className="ml-3 inline-flex min-w-[1.5rem] items-center justify-center rounded-full bg-red-500 px-2 py-1 text-xs font-semibold text-white">
                  {badgeCount > 99 ? "99+" : badgeCount}
                </span>
              )}
            </Link>
          );
        })}
      </nav>
    </aside>
  );
}
