import type { ReactNode } from 'react';
import { useFetchCurrentUser } from '@features/auth/hooks/useFetchCurrentUser';

interface CurrentUserInitializerProps {
    children: ReactNode;
}

/**
 * Component để khởi tạo thông tin user hiện tại
 * Lấy dữ liệu từ API /auth/user và lưu vào global store
 * 
 * Sử dụng: Bọc component này ở mức cao (AppBootstrap, MainLayout, etc.)
 * 
 * @example
 * <CurrentUserInitializer>
 *   <YourApp />
 * </CurrentUserInitializer>
 */
export const CurrentUserInitializer = ({ children }: CurrentUserInitializerProps) => {
    const { isLoading, error } = useFetchCurrentUser();

    // Có thể hiển thị loading state nếu cần
    if (isLoading) {
        return <div className="flex items-center justify-center min-h-screen">Loading user...</div>;
    }

    // Có thể hiển thị error state nếu cần
    if (error) {
        console.warn('Failed to fetch user:', error);
        // Không block app, chỉ log warning
    }

    return <>{children}</>;
};
