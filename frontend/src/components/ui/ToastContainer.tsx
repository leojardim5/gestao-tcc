"use client";

import { useUiStore } from "@/store/ui";
import { Toast, ToastDescription, ToastProvider, ToastViewport } from "@/components/ui/Toast";
import { CheckCircle, XCircle, Info, AlertTriangle } from "lucide-react";

export function ToastContainer() {
  const { toastQueue, removeToast } = useUiStore();

  const getToastIcon = (type: string) => {
    switch (type) {
      case "success":
        return <CheckCircle className="h-5 w-5 text-green-500" />;
      case "error":
        return <XCircle className="h-5 w-5 text-red-500" />;
      case "warning":
        return <AlertTriangle className="h-5 w-5 text-yellow-500" />;
      case "info":
        return <Info className="h-5 w-5 text-blue-500" />;
      default:
        return <Info className="h-5 w-5 text-blue-500" />;
    }
  };

  const getToastVariant = (type: string) => {
    switch (type) {
      case "error":
        return "destructive";
      default:
        return "default";
    }
  };

  return (
    <ToastProvider>
      <ToastViewport />
      {toastQueue.map((toast) => (
        <Toast
          key={toast.id}
          variant={getToastVariant(toast.type)}
          className="mb-2"
          onOpenChange={(open) => {
            if (!open) {
              removeToast(toast.id);
            }
          }}
        >
          <div className="flex items-center gap-2">
            {getToastIcon(toast.type)}
            <ToastDescription>{toast.message}</ToastDescription>
          </div>
        </Toast>
      ))}
    </ToastProvider>
  );
}
