import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';
import { PapelUsuario, Usuario } from '@/interfaces';

interface SessionState {
  user: Omit<Usuario, 'id' | 'criadoEm' | 'ativo'> | null;
  loginFake: (user: { email: string; papel: PapelUsuario; nome: string }) => void;
  logout: () => void;
}

// A simple user object for the fake login
const FAKE_USER_ID = 'f79a8a5d-3c20-4e38-bedf-2743e634f82b';

export const useSessionStore = create<SessionState>()(
  persist(
    (set) => ({
      user: null,
      loginFake: ({ email, papel, nome }) =>
        set({ user: { email, papel, nome } }),
      logout: () => set({ user: null }),
    }),
    {
      name: 'session-storage', // name of the item in the storage (must be unique)
      storage: createJSONStorage(() => localStorage), // (optional) by default, 'localStorage' is used
    }
  )
);
