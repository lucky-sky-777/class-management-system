import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";
import type { LoginRequest, RegisterRequest, AuthResponse, RegisterResponse, SignOutResponse, UserResponse } from "@features/auth/types";

export const authApi = {
    signIn: async (data: LoginRequest): Promise<ResponseDTO<AuthResponse>> => {
        return apiClient.post<ResponseDTO<AuthResponse>>("/auth/signin", data);
    },

    signUp: async (data: RegisterRequest): Promise<ResponseDTO<RegisterResponse>> => {
        return apiClient.post<ResponseDTO<RegisterResponse>>("/auth/signup", data);
    },

    signOut: async (accesstoken: string, refreshtoken: string): Promise<ResponseDTO<SignOutResponse>> => {
        return apiClient.post<ResponseDTO<SignOutResponse>>("/auth/signout", null, {
            headers: {
                Authorization: `Bearer ${accesstoken}`,
                "X-Refresh-Token": refreshtoken,
            },
        });
    },

    callbackGoogle: async (code: string): Promise<ResponseDTO<AuthResponse>> => {
        return apiClient.get<ResponseDTO<AuthResponse>>(`/auth/google/callback?code=${code}`);
    },

    getMe: async (): Promise<ResponseDTO<UserResponse>> => {
        return apiClient.get<ResponseDTO<UserResponse>>("/auth/user");
    }
};
