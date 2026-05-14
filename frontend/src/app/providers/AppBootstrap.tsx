import { CurrentUserInitializer } from '@features/auth/components';
import type { ReactNode } from 'react';

interface AppBootstrapProps {
    children: ReactNode;
}

/**
 * AppBootstrap Component
 * 
 * Khởi tạo các global state và initializer cho ứng dụng
 * - Tự động lấy thông tin user hiện tại từ API /auth/user
 * - Lưu user data vào global auth store
 * 
 * Sử dụng ở mức cao nhất của ứng dụng, thường trong main provider hoặc app root
 */
export const AppBootstrap = ({ children }: AppBootstrapProps) => {
    return (
        <CurrentUserInitializer>
            {children}
        </CurrentUserInitializer>
    );
};

