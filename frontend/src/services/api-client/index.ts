import axios from "axios";
import type { AxiosRequestConfig } from "axios";
import { axiosInstance } from "./core";
import type { ApiClientOptions } from "./config";


/**
 * Class representing an error returned from the API.
 * Extends the native Error class with additional status and business logic codes.
 */
export class ApiError extends Error {
    /**
     * HTTP Status code returned from the server (e.g., 400, 401, 403, 404, 500).
     * If no response was received, this will be 0.
     */
    status: number;

    /**
     * Specific business logic error code returned in the ResponseDTO from the backend (e.g., 1000).
     * This field is optional and may be undefined if no custom code is provided.
     */
    code?: number;

    /**
     * Creates an instance of ApiError.
     * 
     * @param message - The error message detailing the reason for the failure.
     * @param status - The HTTP Status code.
     * @param code - The custom business logic error code (optional).
     */
    constructor(message: string, status: number, code?: number) {
        super(message);
        this.name = "ApiError";
        this.status = status;
        this.code = code;

        // Restore prototype chain
        Object.setPrototypeOf(this, ApiError.prototype);
    }
}

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
        if (axios.isAxiosError(error)) {
            if (error.response) {
                const data = error.response.data as { message?: string; code?: number } | undefined;
                const status = error.response.status;
                const message = data?.message || error.response.statusText || `API Error: ${status}`;
                const code = data?.code;
                throw new ApiError(message, status, code);
            } else if (error.request) {
                throw new ApiError("No response received from the server", 0);
            } else {
                throw new ApiError(error.message, 0);
            }
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