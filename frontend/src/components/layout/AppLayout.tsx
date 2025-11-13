"use client";

import { usePathname } from "next/navigation";
import { Sidebar } from "./Sidebar";
import { Header } from "./Header";
import { ToastContainer } from "@/components/ui/ToastContainer";

export function AppLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const isLoginPage = pathname === "/login";

  if (isLoginPage) {
    return (
      <>
        {children}
        <ToastContainer />
      </>
    );
  }

  return (
    <div className="flex min-h-screen bg-slate-50">
      <Sidebar />
      <main className="flex-1 flex flex-col md:ml-64 w-full md:w-auto">
        <Header />
        <div className="h-screen overflow-y-auto pt-24 pb-4 md:pb-6 px-4 md:px-6">{children}</div>
      </main>
      <ToastContainer />
    </div>
  );
}
