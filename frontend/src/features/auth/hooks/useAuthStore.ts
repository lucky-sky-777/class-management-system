import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import type { User } from '@shared/domain/user';
import { jwtDecode } from 'jwt-decode';

interface AuthState {
    user: User | null;
    isAuthenticated: boolean;
    setUser: (user: User | null) => void;
    logout: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set) => ({
            user: null,
            isAuthenticated: false,
            setUser: (user) => set({ user, isAuthenticated: !!user }),
            logout: () => set({ user: null, isAuthenticated: false }),
        }),
        {
            name: 'auth-storage',
            storage: createJSONStorage(() => localStorage),
            onRehydrateStorage: () => (state) => {
                if (state && state.user && state.user.token) {
                    try {
                        const decoded = jwtDecode(state.user.token);
                        const currentTime = Date.now() / 1000;
                        if (decoded.exp && decoded.exp < currentTime) {
                            state.logout();
                        }
                    } catch (error) {
                        state.logout();
                    }
                }
            },
        }
    )
);
