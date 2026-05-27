import type { CreateAxiosDefaults, AxiosRequestConfig } from "axios";

export const BASE_URL = "https://class-management-system-backend.fly.dev/api";

export const configAxios: CreateAxiosDefaults = {
    baseURL: BASE_URL,
    headers: {
        "Content-Type": "application/json",
    },
};

export interface ApiClientOptions extends Omit<RequestInit, "headers" | "signal"> {
    headers?: Record<string, string>;
    params?: Record<string, unknown>;
    timeout?: number;
    signal?: AbortSignal | null;
    axiosConfig?: Omit<AxiosRequestConfig, "url" | "method" | "data" | "headers" | "signal" | "params" | "timeout">;
}
