import axios from "axios";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { jwtDecode } from "jwt-decode";
import { useAuthStore } from "@features/auth/hooks/useAuthStore";

/**
 * Base API Client using axios
 * Handles base URL, default headers, and common error handling
 */

export const BASE_URL = "http://localhost:8080/api";

const axiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

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
    if (error.response && error.response.status === 401) {
      storage.remove(AUTH_STORAGE_KEY.TOKEN);
      storage.remove(AUTH_STORAGE_KEY.REFRESH);
      useAuthStore.getState().logout();
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

// Sample usage for the requested endpoint
export const fetchAlice = () => apiClient.get("/users/alice");
