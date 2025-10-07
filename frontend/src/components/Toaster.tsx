"use client"

import {
  Toast, 
  ToastClose, 
  ToastDescription, 
  ToastProvider, 
  ToastTitle, 
  ToastViewport
} from "@/components/ui/Toast"
import { useUiStore } from "@/store/ui"

export function Toaster() {
  const { toastQueue } = useUiStore()

  return (
    <ToastProvider>
      {toastQueue.map(function ({ id, type, message }) {
        return (
          <Toast key={id} variant={type === 'error' ? 'destructive' : 'default'}>
            <div className="grid gap-1">
              <ToastTitle>{type.charAt(0).toUpperCase() + type.slice(1)}</ToastTitle>
              <ToastDescription>{message}</ToastDescription>
            </div>
            <ToastClose />
          </Toast>
        )
      })}
      <ToastViewport />
    </ToastProvider>
  )
}
