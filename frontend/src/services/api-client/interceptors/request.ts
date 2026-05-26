import type { InternalAxiosRequestConfig } from "axios";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";

export const requestInterceptor = (
    config: InternalAxiosRequestConfig
): InternalAxiosRequestConfig => {
    const token = storage.get<string>(AUTH_STORAGE_KEY.TOKEN);
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
};

export const requestErrorInterceptor = (error: unknown): Promise<never> => {
    return Promise.reject(error);
};
