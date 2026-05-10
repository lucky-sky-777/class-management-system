import axios from "axios";

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
    // 1. Kiểm tra xem bạn lưu token dưới key nào trong LocalStorage
    // Thông thường trong dự án của bạn là 'access_token' hoặc từ auth-storage của Zustand
    let token = localStorage.getItem("access_token");

    // 2. Nếu bạn dùng Zustand persist (auth-storage), hãy thử đoạn này:
    if (!token) {
      const authStorage = localStorage.getItem("auth-storage");
      if (authStorage) {
        const parsed = JSON.parse(authStorage);
        token = parsed.state?.user?.token || parsed.state?.token;
      }
    }

    // 3. Nếu có token thì gắn vào Header
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export const apiClient = {
  async get<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    try {
      const response = await axiosInstance.get<T>(endpoint, {
        headers: options.headers as any,
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
    try {
      const response = await axiosInstance.post<T>(endpoint, body, {
        headers: options.headers as any,
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
    try {
      const response = await axiosInstance.patch<T>(endpoint, body, {
        headers: options.headers as any,
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
    try {
      const response = await axiosInstance.delete<T>(endpoint, {
        headers: options.headers as any,
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
