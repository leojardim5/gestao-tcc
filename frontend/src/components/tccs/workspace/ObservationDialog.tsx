"use client";

import { useState, useEffect } from "react";
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/Modal";
import { Label } from "@/components/ui/Label";
import { Button } from "@/components/ui/Button";

interface ObservationDialogProps {
  open: boolean;
  title: string;
  description?: string;
  placeholder?: string;
  confirmLabel?: string;
  cancelLabel?: string;
  isSubmitting?: boolean;
  defaultObservation?: string;
  required?: boolean;
  requiredMessage?: string;
  onConfirm: (observation: string) => void;
  onCancel: () => void;
}

export const ObservationDialog = ({
  open,
  title,
  description,
  placeholder = "Adicione observações (opcional)",
  confirmLabel = "Confirmar",
  cancelLabel = "Cancelar",
  isSubmitting = false,
  defaultObservation = "",
  required = false,
  requiredMessage = "Este campo é obrigatório para esta ação.",
  onConfirm,
  onCancel,
}: ObservationDialogProps) => {
  const [observation, setObservation] = useState(defaultObservation);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (open) {
      setObservation(defaultObservation);
      setError(null);
    }
  }, [defaultObservation, open]);

  return (
    <Dialog open={open} onOpenChange={(value) => !value && !isSubmitting && onCancel()}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          {description && <DialogDescription>{description}</DialogDescription>}
        </DialogHeader>
        <div className="space-y-2">
          <Label htmlFor="observation">Observações</Label>
          <textarea
            id="observation"
            rows={4}
            className="w-full rounded-md border border-input bg-background px-3 py-2 text-sm shadow-sm focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-primary"
            placeholder={placeholder}
            value={observation}
            onChange={(event) => {
              setObservation(event.target.value);
              if (error) {
                setError(null);
              }
            }}
            disabled={isSubmitting}
          />
          {error ? (
            <p className="text-xs text-destructive">{error}</p>
          ) : (
            <p className="text-xs text-muted-foreground">
              {required
                ? requiredMessage
                : "Este campo é opcional. Deixe vazio caso não deseje adicionar nenhum comentário."}
            </p>
          )}
        </div>
        <DialogFooter>
          <Button type="button" variant="secondary" onClick={onCancel} disabled={isSubmitting}>
            {cancelLabel}
          </Button>
          <Button
            type="button"
            onClick={() => {
              const trimmed = observation.trim();
              if (required && trimmed.length === 0) {
                setError(requiredMessage);
                return;
              }
              onConfirm(trimmed);
            }}
            disabled={isSubmitting}
          >
            {isSubmitting ? "Enviando..." : confirmLabel}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

