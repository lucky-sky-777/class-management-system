import axios from "axios";
import type { AxiosRequestConfig } from "axios";
import { axiosInstance } from "./core";
import type { ApiClientOptions } from "./config";

let tempHeaders: Record<string, string> = {};

/**
 * Unified request executor that handles headers merging, parameters, timeouts, and error handling.
 */
async function runRequest<T>(
    method: "get" | "post" | "put" | "patch" | "delete",
    endpoint: string,
    body?: unknown,
    options: ApiClientOptions = {}
): Promise<T> {
    const { headers = {}, params, timeout, signal, axiosConfig = {} } = options;

    // Merge permanent/temp/custom headers
    const currentHeaders = {
        ...tempHeaders,
        ...headers,
    };
    // Clear temporary headers
    tempHeaders = {};

    try {
        const config: AxiosRequestConfig = {
            url: endpoint,
            method,
            headers: currentHeaders,
            params,
            timeout,
            ...(signal ? { signal } : {}),
            ...axiosConfig,
        };

        if (body !== undefined) {
            config.data = body;
        }

        const response = await axiosInstance.request<T>(config);
        return response.data;
    } catch (error) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(
                `API Error: ${error.response.status} ${error.response.statusText || ""}`.trim()
            );
        }
        throw error;
    }
}

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

    async get<T>(endpoint: string, options: ApiClientOptions = {}): Promise<T> {
        return runRequest<T>("get", endpoint, undefined, options);
    },

    async post<T>(
        endpoint: string,
        body: unknown,
        options: ApiClientOptions = {}
    ): Promise<T> {
        return runRequest<T>("post", endpoint, body, options);
    },

    async patch<T>(
        endpoint: string,
        body: unknown,
        options: ApiClientOptions = {}
    ): Promise<T> {
        return runRequest<T>("patch", endpoint, body, options);
    },

    async put<T>(
        endpoint: string,
        body: unknown,
        options: ApiClientOptions = {}
    ): Promise<T> {
        return runRequest<T>("put", endpoint, body, options);
    },

    async delete<T>(endpoint: string, options: ApiClientOptions = {}): Promise<T> {
        return runRequest<T>("delete", endpoint, undefined, options);
    },
};

export { BASE_URL } from "./config";
export type { ApiClientOptions };