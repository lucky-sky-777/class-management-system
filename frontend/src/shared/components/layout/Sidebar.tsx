// src/shared/components/layout/Sidebar.tsx
import { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import {
  Home,
  MonitorPlay,
  Users,
  ChevronDown,
  ChevronUp,
  Lock,
  Globe,
} from "lucide-react";
import { useHome } from "@features/home/hooks/useHome";
import { useUIStore, useToastStore } from "@app/store";
import { ToastType } from "@shared/domain/enums";

interface UIStoreState {
  isSidebarOpen: boolean;
  setSidebarOpen?: (isOpen: boolean) => void;
  toggleSidebar: (isOpen?: boolean) => void;
}

export const Sidebar = () => {
  const [isRegisteredOpen, setIsRegisteredOpen] = useState(true);
  // đã được Hook useHome quản lý an toàn từ bên trong dựa theo User ID.
  const { classes, isLoading, refresh } = useHome();

  const isSidebarOpen = useUIStore(
    (state: UIStoreState) => state.isSidebarOpen,
  );
  const setSidebarOpen = useUIStore(
    (state: UIStoreState) => state.setSidebarOpen || state.toggleSidebar,
  );

  const showToast = useToastStore((state) => state.showToast);

  const handlePendingClick = () => {
    showToast(
      "Yêu cầu tham gia của bạn đang chờ chủ nhóm duyệt.",
      ToastType.WARNING,
    );
  };

  // Chỉ giữ lại lắng nghe sự kiện phát ra từ các modal (tạo/xóa lớp) để đồng bộ dữ liệu
  useEffect(() => {
    const handleRefresh = () => {
      refresh();
    };
    window.addEventListener("refreshHomeClasses", handleRefresh);

    return () => {
      window.removeEventListener("refreshHomeClasses", handleRefresh);
    };
  }, [refresh]);

  const myClasses = classes || [];

  return (
    <>
      {isSidebarOpen && (
        <div
          className="fixed inset-0 bg-black/60 backdrop-blur-sm z-30 md:hidden transition-opacity"
          onClick={() => setSidebarOpen?.(false)}
        />
      )}

      <aside
        className={`bg-[var(--bg-surface)] border-r border-[var(--rule)] flex flex-col fixed md:sticky top-[64px] z-40 h-[calc(100vh-64px)] w-64 transition-transform duration-300 ease-in-out ${
          isSidebarOpen ? "translate-x-0" : "-translate-x-full md:-ml-64"
        }`}
      >
        <nav className="flex-1 py-2 overflow-y-auto p-2.5">
          <NavLink to="/" end>
            {({ isActive }: { isActive: boolean }) => (
              <div
                className={`flex items-center gap-3 px-3 py-2.5 rounded-[var(--r-xl)] transition-all font-medium ${
                  isActive
                    ? "bg-[var(--primary-fill)] text-[var(--primary-text)]"
                    : "text-[var(--ink-2)] hover:bg-[var(--bg-surface-2)] hover:text-[var(--ink-1)]"
                }`}
              >
                <Home size={20} className="shrink-0" />
                <span>Trang chủ</span>
                {isActive && (
                  <span className="ml-auto w-1.5 h-1.5 rounded-full bg-[var(--primary-text)]"></span>
                )}
              </div>
            )}
          </NavLink>

          <div className="mt-4">
            <div
              className="flex items-center justify-between px-3 py-2 cursor-pointer text-[var(--ink-2)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)] rounded-[var(--r-xl)] transition-colors"
              onClick={() => setIsRegisteredOpen(!isRegisteredOpen)}
            >
              <div className="flex items-center gap-3">
                <MonitorPlay size={20} className="shrink-0" />
                <span className="text-[10px] font-bold tracking-wider uppercase">
                  Đã đăng ký
                </span>
              </div>
              {isRegisteredOpen ? (
                <ChevronUp size={14} />
              ) : (
                <ChevronDown size={14} />
              )}
            </div>

            {isRegisteredOpen && (
              <ul className="mt-1 flex flex-col gap-0.5">
                {/* DANH SÁCH LỚP HỌC TRONG SIDEBAR */}
                {isLoading ? (
                  <li className="px-10 py-3">
                    <div className="skeleton h-4 w-full opacity-20 rounded-md"></div>
                  </li>
                ) : myClasses.length > 0 ? (
                  myClasses.map((item) => {
                    // 1. KIỂM TRA TRẠNG THÁI: Lớp này học sinh đã được duyệt chưa?
                    const isPending = item.status !== "JOINED";

                    return (
                      <li key={item.id}>
                        {isPending ? (
                          // 2. NẾU CHƯA DUYỆT: Hiện thẻ div mờ nhạt, gắn sự kiện onClick mở Toast
                          <div
                            onClick={handlePendingClick}
                            className="group flex items-center justify-between px-3 py-2.5 rounded-[var(--r-xl)] text-[var(--ink-3)] opacity-60 cursor-pointer hover:bg-[var(--bg-surface-2)] transition-all bg-[var(--bg-surface-2)]/50"
                            title="Lớp đang chờ giáo viên duyệt"
                          >
                            <div className="flex items-center gap-3 truncate">
                              <Users size={18} className="shrink-0" />
                              <span className="text-sm font-medium truncate leading-tight">
                                {item.name}
                              </span>
                            </div>
                            {/* Icon ổ khóa báo hiệu bị khóa */}
                            <Lock
                              size={12}
                              className="text-[var(--amber-text)] opacity-80"
                            />
                          </div>
                        ) : (
                          // 3. NẾU ĐÃ DUYỆT: Trả về NavLink bình thường
                          <NavLink to={`/class/${item.id}/diagram`}>
                            {({ isActive }: { isActive: boolean }) => (
                              <div
                                className={`group flex items-center justify-between px-3 py-2.5 rounded-[var(--r-xl)] transition-all ${
                                  isActive
                                    ? "bg-[var(--primary-fill)] text-[var(--primary-text)]"
                                    : "text-[var(--ink-2)] hover:bg-[var(--bg-surface-2)] hover:text-[var(--ink-1)]"
                                }`}
                              >
                                <div className="flex items-center gap-3 truncate">
                                  <Users size={18} className="shrink-0" />
                                  <span className="text-sm font-medium truncate leading-tight">
                                    {item.name}
                                  </span>
                                </div>

                                <div
                                  title={
                                    item.privacy === "PUBLIC"
                                      ? "Cộng đồng"
                                      : "Nhóm kín"
                                  }
                                >
                                  {item.privacy === "PUBLIC" ? (
                                    <Globe
                                      size={12}
                                      className={
                                        isActive
                                          ? "text-[var(--primary-text)] opacity-80"
                                          : "text-[var(--green-text)] opacity-60"
                                      }
                                    />
                                  ) : (
                                    <Lock
                                      size={12}
                                      className={
                                        isActive
                                          ? "text-[var(--primary-text)] opacity-80"
                                          : "text-[var(--amber-text)] opacity-80"
                                      }
                                    />
                                  )}
                                </div>
                              </div>
                            )}
                          </NavLink>
                        )}
                      </li>
                    );
                  })
                ) : (
                  <li className="px-10 py-4 text-xs text-[var(--ink-3)] opacity-80 italic">
                    Chưa tham gia lớp nào
                  </li>
                )}
              </ul>
            )}
          </div>
        </nav>
      </aside>
    </>
  );
};
