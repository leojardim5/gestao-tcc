import { create } from 'zustand';
import { createJSONStorage, persist } from 'zustand/middleware';
import { PapelUsuario, Usuario } from '@/interfaces';

interface SessionState {
  token: string | null;
  user: Omit<Usuario, 'criadoEm' | 'ativo'> | null;
  setSession: (token: string, user: Omit<Usuario, 'criadoEm' | 'ativo'>) => void;
  logout: () => void;
}

export const useSessionStore = create<SessionState>()(
  persist(
    (set, get) => ({
      token: null,
      user: null,
      setSession: (token, user) => {
        console.log("Setting session:", { token, user });
        set({ token, user });
      },
      logout: () => {
        console.log("Logging out");
        set({ token: null, user: null });
      },
    }),
    {
      name: 'session-storage', // name of the item in the storage (must be unique)
      storage: createJSONStorage(() => localStorage), // (optional) by default, 'localStorage' is used
      onRehydrateStorage: (state) => {
        console.log("Rehydrating storage:", state);
        return (state, error) => {
          if (error) {
            console.error("An error happened during rehydration", error);
          } else {
            console.log("Rehydration finished", state);
          }
        };
      },
    }
  )
);
