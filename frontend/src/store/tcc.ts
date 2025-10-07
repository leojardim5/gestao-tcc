import { create } from 'zustand';
import { StatusTcc } from '@/interfaces';

interface TccFilters {
  status?: StatusTcc;
  orientadorId?: string;
  alunoId?: string;
  termo?: string;
}

interface TccStoreState {
  filters: TccFilters;
  setFilters: (filters: TccFilters) => void;
}

export const useTccStore = create<TccStoreState>((set) => ({
  filters: {},
  setFilters: (filters) => set({ filters }),
}));
