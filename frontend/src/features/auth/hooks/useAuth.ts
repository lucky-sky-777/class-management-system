import { useAuthInternal } from '@features/auth/hooks/useAuthInternal';
import { useAuthStore } from '@features/auth/hooks/useAuthStore';
import { useCallback } from 'react';


/**
 * useAuth: Public hook để sử dụng ở các component bên ngoài feature auth.
 * Cung cấp thông tin user hiện tại và các hàm login/logout cơ bản.
 */
export const useAuth = () => {
    const { user, isAuthenticated } = useAuthStore();
    const { login, signup, logout, isLoading, error } = useAuthInternal();

    const handleLogin = useCallback(async (username: string, password: string) => {
        return await login({ username, password });
    }, [login]);

    const handleSignup = useCallback(async (username: string, password: string, displayname: string) => {
        const res = await signup(username, password, displayname);
        return res.success;
    }, [signup]);

    return {
        user,
        isAuthenticated,
        login: handleLogin,
        signup: handleSignup,
        logout,
        isLoading,
        error,
    };
};
