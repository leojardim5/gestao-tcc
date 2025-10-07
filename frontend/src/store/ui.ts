import { create } from 'zustand';

export type ToastType = 'success' | 'error' | 'info' | 'warning';

interface Toast {
  id: number;
  message: string;
  type: ToastType;
}

interface UiState {
  toastQueue: Toast[];
  addToast: (message: string, type: ToastType) => void;
  removeToast: (id: number) => void;

  isModalOpen: boolean;
  modalContent: React.ReactNode | null;
  openModal: (content: React.ReactNode) => void;
  closeModal: () => void;
}

export const useUiStore = create<UiState>((set) => ({
  toastQueue: [],
  addToast: (message, type) => {
    const id = Date.now();
    set((state) => ({ toastQueue: [...state.toastQueue, { id, message, type }] }));
    setTimeout(() => {
      set((state) => ({ toastQueue: state.toastQueue.filter((toast) => toast.id !== id) }));
    }, 5000);
  },
  removeToast: (id) => {
    set((state) => ({ toastQueue: state.toastQueue.filter((toast) => toast.id !== id) }));
  },

  isModalOpen: false,
  modalContent: null,
  openModal: (content) => set({ isModalOpen: true, modalContent: content }),
  closeModal: () => set({ isModalOpen: false, modalContent: null }),
}));
