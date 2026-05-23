import type { InternalAxiosRequestConfig } from "axios";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { jwtDecode } from "jwt-decode";
import { useAuthStore } from "@features/auth/hooks/useAuthStore";

export const requestInterceptor = (
    config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
    const token = storage.get<string>(AUTH_STORAGE_KEY.TOKEN);
    if (token) {
        try {
            const decoded = jwtDecode(token);
            const currentTime = Date.now() / 1000;
            if (decoded.exp && decoded.exp < currentTime) {
                storage.remove(AUTH_STORAGE_KEY.TOKEN);
                storage.remove(AUTH_STORAGE_KEY.REFRESH);
                useAuthStore.getState().logout();
            } else {
                config.headers.Authorization = `Bearer ${token}`;
            }
        } catch {
            storage.remove(AUTH_STORAGE_KEY.TOKEN);
            storage.remove(AUTH_STORAGE_KEY.REFRESH);
            useAuthStore.getState().logout();
        }
    }
    return config;
};

export const requestErrorInterceptor = (error: unknown): Promise<never> => {
    return Promise.reject(error);
};
