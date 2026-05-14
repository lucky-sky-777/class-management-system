import { useEffect, useState } from 'react';
import { authApi } from '@features/auth/api';
import { useAuthStore } from '@features/auth/hooks/useAuthStore';
import type { User } from '@shared/domain/user';

interface UseFetchCurrentUserReturn {
    user: User | null;
    isLoading: boolean;
    error: Error | null;
    refetch: () => Promise<void>;
}

/**
 * Hook để lấy thông tin user hiện tại từ API /auth/user
 * Tự động lưu vào global auth store
 */
export const useFetchCurrentUser = (): UseFetchCurrentUserReturn => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<Error | null>(null);
    const { user, setUser } = useAuthStore();

    const fetchUser = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const response = await authApi.getMe();
            
            if (response.data) {
                // Chuyển đổi UserResponse thành User domain object
                const userData: User = {
                    id: response.data.id,
                    username: response.data.username,
                    displayName: response.data.display_name,
                    avatarUrl: response.data.avatar_url,
                    phone: response.data.phone,
                    email: response.data.email,
                    joinedAt: response.data.joined_at,
                };
                setUser(userData);
            }
        } catch (err) {
            const error = err instanceof Error ? err : new Error('Failed to fetch user');
            setError(error);
            console.error('Error fetching current user:', error);
        } finally {
            setIsLoading(false);
        }
    };

    // Tự động fetch khi component mount
    useEffect(() => {
        fetchUser();
    }, []);

    return {
        user: user || null,
        isLoading,
        error,
        refetch: fetchUser,
    };
};
