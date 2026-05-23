import axios from "axios";
import type { AxiosResponse } from "axios";
import { handler403, handler404 } from "@services/api-client/handlers";

export const responseInterceptor = (response: AxiosResponse): AxiosResponse => {
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
    if (error.response.status === 404 || (data && data.code == "404")) {
        handler404();
    }
    if (error.response.status === 403 || (data && data.code == "403")) {
        handler403();
    }
    return Promise.reject(error);
};

