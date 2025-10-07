import { useUiStore, ToastType } from '@/store/ui';

export const useToast = () => {
  const addToast = useUiStore((state) => state.addToast);

  const showToast = (message: string, type: ToastType) => {
    addToast(message, type);
  };

  return { showToast };
};
