export interface LoginRequest {
    username: string;
    password?: string;
}

export interface RegisterRequest {
    username: string;
    display_name?: string;
    password?: string;
    email?: string;
    avatar_url?: string;
}

export interface AuthResponse {
    access_token: string;
    refresh_token: string;
}

export interface RegisterResponse {
    user_id?: number;
    id?: number;
    username: string;
}

export interface SignOutRequest {
    accessToken: string;
}

export interface SignOutResponse {
    success: boolean;
}

export interface UserResponse {
    id: string;
    username: string;
    display_name?: string;
    provider?: string;
    avatar_url?: string;
    phone?: string;
    email?: string;
    school?: string;
    major?: string;
    joined_at?: string;
}

export interface ChangePasswordRequest {
    old_password?: string;
    new_password?: string;
}

export interface UpdateUserRequest {
    display_name?: string;
    avatar_url?: string;
    school?: string;
    major?: string;
    phone?: string;
}
