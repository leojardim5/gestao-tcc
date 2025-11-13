import { create } from "zustand";
import { persist } from "zustand/middleware";

interface TccNotificationState {
  counts: Record<string, number>;
  dismissed: Record<string, boolean>;
  setCounts: (counts: Record<string, number>) => void;
  dismiss: (id: string) => void;
  reset: () => void;
  setCountForTcc: (id: string, count: number) => void;
}

export const useTccNotificationsStore = create<TccNotificationState>()(
  persist(
    (set, get) => ({
      counts: {},
      dismissed: {},
      setCounts: (counts) =>
        set((state) => {
          const nextCounts: Record<string, number> = { ...counts };
          const nextDismissed: Record<string, boolean> = { ...state.dismissed };

          // Remove entries not present anymore
          Object.keys(nextDismissed).forEach((id) => {
            if (!(id in nextCounts)) {
              delete nextDismissed[id];
            }
          });

          Object.entries(nextCounts).forEach(([id, value]) => {
            if (value <= 0 && nextDismissed[id]) {
              delete nextDismissed[id];
            }
          });

          return { counts: nextCounts, dismissed: nextDismissed };
        }),
      dismiss: (id) =>
        set((state) => ({
          dismissed: { ...state.dismissed, [id]: true },
        })),
      reset: () => set({ counts: {}, dismissed: {} }),
      setCountForTcc: (id, count) =>
        set((state) => {
          const nextCounts = { ...state.counts };
          const nextDismissed = { ...state.dismissed };

          if (count <= 0) {
            delete nextCounts[id];
            delete nextDismissed[id];
          } else {
            nextCounts[id] = count;
            nextDismissed[id] = false;
          }

          return { counts: nextCounts, dismissed: nextDismissed };
        }),
    }),
    {
      name: "tcc-notifications",
      version: 1,
    },
  ),
);

export const selectPendingCountForTcc = (tccId: string) => (state: TccNotificationState) =>
  state.dismissed[tccId] ? 0 : state.counts[tccId] ?? 0;

export const selectTotalPendingTccs = (state: TccNotificationState) =>
  Object.entries(state.counts).reduce((total, [id, count]) => {
    if (state.dismissed[id]) {
      return total;
    }
    return total + count;
  }, 0);

