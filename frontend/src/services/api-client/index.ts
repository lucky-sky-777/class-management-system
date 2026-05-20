import axios from "axios";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { jwtDecode } from "jwt-decode";
import { useAuthStore } from "@features/auth/hooks/useAuthStore";
import { handler403, handler404 } from "@services/api-client/handlers";
import { configAxios } from "@services/api-client/config";

const axiosInstance = axios.create(configAxios);

axiosInstance.interceptors.request.use(
    (config) => {
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
            } catch (error) {
                storage.remove(AUTH_STORAGE_KEY.TOKEN);
                storage.remove(AUTH_STORAGE_KEY.REFRESH);
                useAuthStore.getState().logout();
            }
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (!error.response) {
            return Promise.reject(new Error("Network error: Unable to connect to the server"));
        }
        if (error.response.status === 404 || error.response.data.code == "404") {
            handler404()
        }
        if (error.response.status === 403 || error.response.data.code == "403") {
            handler403();
        }
        return Promise.reject(error);
    }
);

let tempHeaders: Record<string, string> = {};

export const apiClient = {
    /**
     * Set a header for API requests
     * @param key Header name
     * @param value Header value
     * @param permanent If true (default), the header will be set for all subsequent requests. If false, it will only be set for the next immediate request.
     */
    setHeader(key: string, value: string, permanent: boolean = true) {
        if (permanent) {
            axiosInstance.defaults.headers.common[key] = value;
        } else {
            tempHeaders[key] = value;
        }
    },

    async get<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
        const currentHeaders = { ...tempHeaders, ...(options.headers as any) };
        tempHeaders = {};
        try {
            const response = await axiosInstance.get<T>(endpoint, {
                headers: currentHeaders,
                signal: options.signal as any,
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(
                    `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim(),
                );
            }
            throw error;
        }
    },

    async post<T>(
        endpoint: string,
        body: any,
        options: RequestInit = {},
    ): Promise<T> {
        const currentHeaders = { ...tempHeaders, ...(options.headers as any) };
        tempHeaders = {};
        try {
            const response = await axiosInstance.post<T>(endpoint, body, {
                headers: currentHeaders,
                signal: options.signal as any,
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(
                    `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim(),
                );
            }
            throw error;
        }
    },

    async patch<T>(
        endpoint: string,
        body: any,
        options: RequestInit = {},
    ): Promise<T> {
        const currentHeaders = { ...tempHeaders, ...(options.headers as any) };
        tempHeaders = {};
        try {
            const response = await axiosInstance.patch<T>(endpoint, body, {
                headers: currentHeaders,
                signal: options.signal as any,
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(
                    `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim(),
                );
            }
            throw error;
        }
    },

    async put<T>(
        endpoint: string,
        body: any,
        options: RequestInit = {},
    ): Promise<T> {
        const currentHeaders = { ...tempHeaders, ...(options.headers as any) };
        tempHeaders = {};
        try {
            const response = await axiosInstance.put<T>(endpoint, body, {
                headers: currentHeaders,
                signal: options.signal as any,
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(
                    `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim(),
                );
            }
            throw error;
        }
    },

    async delete<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
        const currentHeaders = { ...tempHeaders, ...(options.headers as any) };
        tempHeaders = {};
        try {
            const response = await axiosInstance.delete<T>(endpoint, {
                headers: currentHeaders,
                signal: options.signal as any,
            });
            return response.data;
        } catch (error) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(
                    `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim(),
                );
            }
            throw error;
        }
    },
};
export { BASE_URL } from "./config";