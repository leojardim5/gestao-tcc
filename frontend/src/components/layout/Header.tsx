"use client";

import { useSessionStore } from "@/store/session";
import { Button } from "@/components/ui/Button";
import { useRouter } from "next/navigation";

export function Header() {
  const { user, logout } = useSessionStore();
  const router = useRouter();

  const handleLogout = () => {
    logout();
    router.push("/login");
  };

  return (
    <header className="flex h-16 items-center justify-between border-b bg-background px-4 md:px-6">
      <div>{/* Breadcrumbs can go here */}</div>
      <div className="flex items-center gap-4">
        {user && (
          <div className="text-right">
            <p className="font-semibold">{user.nome}</p>
            <p className="text-xs text-muted-foreground">{user.email}</p>
          </div>
        )}
        <Button variant="ghost" size="icon" onClick={handleLogout}>
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="h-5 w-5"><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"/><polyline points="16 17 21 12 16 7"/><line x1="21" x2="9" y1="12" y2="12"/></svg>
        </Button>
      </div>
    </header>
  );
}
