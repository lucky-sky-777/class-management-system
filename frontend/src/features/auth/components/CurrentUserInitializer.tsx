import type { ReactNode } from "react";
import { useFetchCurrentUser } from "@features/auth/hooks/useFetchCurrentUser";

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
export const CurrentUserInitializer = ({
  children,
}: CurrentUserInitializerProps) => {
  const { isLoading, error } = useFetchCurrentUser();

  // Có thể hiển thị loading state nếu cần
  if (isLoading) {
    return (
      <div className="fixed inset-0 z-[9999] flex flex-col items-center justify-center bg-[var(--bg-paper)] transition-colors duration-300">
        <div className="flex flex-col items-center gap-4 animate-in fade-in duration-300">
          {/* Biểu tượng Logo chữ E nhún nhảy đồng bộ với hệ thống */}
          <div className="w-12 h-12 rounded-xl bg-[var(--primary)] flex items-center justify-center font-serif font-semibold text-white text-2xl shadow-md animate-bounce">
            E
          </div>
          {/* Vòng xoay Spinner tinh tế bằng CSS tokens */}
          <div className="w-6 h-6 border-3 border-[var(--primary-border)] border-t-transparent rounded-full animate-spin"></div>
          <p className="text-xs font-bold uppercase tracking-[0.15em] text-[var(--ink-3)] mt-1 animate-pulse">
            Đang khởi tạo hệ thống...
          </p>
        </div>
      </div>
    );
  }
  // Có thể hiển thị error state nếu cần
  if (error) {
    console.warn("Failed to fetch user:", error);
    // Không block app, chỉ log warning
  }

  return <>{children}</>;
};
