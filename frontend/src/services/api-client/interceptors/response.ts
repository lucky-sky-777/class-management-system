import axios from "axios";
import type { AxiosResponse } from "axios";
import { handler401, handler403 } from "@services/api-client/handlers";
import type { ApiResponse } from "@shared/utils/common";

export const responseInterceptor = (
    response: AxiosResponse<ApiResponse<any>>
): AxiosResponse => {
    if (response.data.code === 401) { 
        handler401();
    }
    if (response.data.code === 403) {
        handler403(response.data.message);
    }
    return response;
};

export const responseErrorInterceptor = (error: unknown): Promise<never> => {
    if (!axios.isAxiosError(error)) {
        return Promise.reject(error);
    }
    if (!error.response) {
        return Promise.reject(new Error("Network error: Unable to connect to the server"));
    }
    const data = error.response.data as Record<string, unknown> | undefined;
    if (error.response.status === 401 || (data && data.code == "401")) {
        handler401();
    }
    if (error.response.status === 403 || (data && data.code == "403")) {
        handler403(error.response.data?.message as string | null);
    }
    return Promise.reject(error);
};

