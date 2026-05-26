import axios from "axios";
import type { AxiosResponse } from "axios";
import { handler401, handler403 } from "@services/api-client/handlers";
import type { ApiResponse } from "@shared/utils/common";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { BASE_URL } from "@services/api-client/config";

let isRefreshing = false;
let failedQueue: { resolve: (value?: unknown) => void; reject: (reason?: any) => void; }[] = [];

const processQueue = (error: any, token: string | null = null) => {
    failedQueue.forEach(prom => {
        if (error) {
            prom.reject(error);
        } else {
            prom.resolve(token);
        }
    });

    failedQueue = [];
};

export const responseInterceptor = (
    response: AxiosResponse<ApiResponse<any>>
): AxiosResponse => {
    // Some successful responses might contain code 401/403 in custom formats
    if (response.data.code === 401) { 
        handler401();
    }
    if (response.data.code === 403) {
        handler403(response.data.message);
    }
    return response;
};

export const responseErrorInterceptor = async (error: unknown): Promise<any> => {
    if (!axios.isAxiosError(error)) {
        return Promise.reject(error);
    }
    if (!error.response) {
        return Promise.reject(new Error("Network error: Unable to connect to the server"));
    }
    
    const originalRequest = error.config;
    const data = error.response.data as Record<string, unknown> | undefined;
    
    if (error.response.status === 401 || (data && data.code == "401")) {
        // Skip refresh if the failed request was a refresh request itself
        if (originalRequest?.url?.includes("/auth/refresh") || originalRequest?.url?.includes("/auth/signin")) {
            handler401();
            return Promise.reject(error);
        }

        if (originalRequest && !(originalRequest as any)._retry) {
            if (isRefreshing) {
                return new Promise((resolve, reject) => {
                    failedQueue.push({ resolve, reject });
                })
                .then(token => {
                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${token}`;
                    }
                    return axios(originalRequest);
                })
                .catch(err => {
                    return Promise.reject(err);
                });
            }

            (originalRequest as any)._retry = true;
            isRefreshing = true;

            const refreshToken = storage.get<string>(AUTH_STORAGE_KEY.REFRESH);
            if (!refreshToken) {
                isRefreshing = false;
                handler401();
                return Promise.reject(error);
            }

            try {
                const response = await axios.post<ApiResponse<{ access_token: string, refresh_token: string }>>(
                    `${BASE_URL}/auth/refresh`,
                    {},
                    {
                        headers: {
                            "X-Refresh-Token": refreshToken,
                        },
                    }
                );

                if (response.data.success && response.data.data) {
                    const newAccessToken = response.data.data.access_token;
                    const newRefreshToken = response.data.data.refresh_token;

                    storage.set(AUTH_STORAGE_KEY.TOKEN, newAccessToken);
                    storage.set(AUTH_STORAGE_KEY.REFRESH, newRefreshToken);

                    processQueue(null, newAccessToken);

                    if (originalRequest.headers) {
                        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
                    }
                    return axios(originalRequest);
                } else {
                    processQueue(new Error("Refresh failed"));
                    handler401();
                    return Promise.reject(error);
                }
            } catch (refreshError) {
                processQueue(refreshError);
                handler401();
                return Promise.reject(refreshError);
            } finally {
                isRefreshing = false;
            }
        } else {
            handler401();
            return Promise.reject(error);
        }
    }
    
    if (error.response.status === 403 || (data && data.code == "403")) {
        handler403(error.response.data?.message as string | null);
    }
    
    return Promise.reject(error);
};
