import { create } from "zustand";
import type { ClassItems } from "@features/home/types";
import { homeAPI } from "@features/home/api";

interface UIState {
  isSidebarOpen: boolean;
  theme: "light" | "dark";
  toggleSidebar: () => void;
  setSidebarOpen: (isOpen: boolean) => void;
  toggleTheme: () => void;
}

export const useUIStore = create<UIState>((set) => ({
  // Mặc định: màn hình desktop thì mở, mobile thì đóng
  isSidebarOpen: window.innerWidth >= 768,
  theme: (localStorage.getItem("theme") as "light" | "dark") || "light",

  toggleSidebar: () =>
    set((state) => ({ isSidebarOpen: !state.isSidebarOpen })),

  setSidebarOpen: (isOpen: boolean) => set({ isSidebarOpen: isOpen }),

  toggleTheme: () =>
    set((state) => {
      const newTheme = state.theme === "light" ? "dark" : "light";
      localStorage.setItem("theme", newTheme);
      if (newTheme === "dark") {
        document.documentElement.classList.add("dark");
      } else {
        document.documentElement.classList.remove("dark");
      }
      return { theme: newTheme };
    }),
}));

// ĐỊNH NGHĨA INTERFACE CHO CLASS STORE
interface ClassStoreState {
  classes: ClassItems[];
  isLoading: boolean;
  error: string | null;
  fetchTracker: string | null;
  fetchClasses: (
    userId: number | string,
    isAuthenticated: boolean,
  ) => Promise<void>;
  clearClasses: () => void;
  forceRefresh: (
    isAuthenticated: boolean,
    userId: number | string,
  ) => Promise<void>;
}

// KHỞI TẠO CLASS STORE DÙNG CHUNG TOÀN CỤC
export const useClassStore = create<ClassStoreState>((set, get) => ({
  classes: [],
  isLoading: false,
  error: null,
  fetchTracker: null,

  // Luồng tự động chạy từ Hook: Bảo đảm 3 thằng gọi cùng lúc chỉ nổ đúng 1 API
  fetchClasses: async (userId, isAuthenticated) => {
    if (!isAuthenticated || !userId) return;

    if (get().fetchTracker === String(userId) || get().isLoading) return;

    try {
      set({ isLoading: true, error: null, fetchTracker: String(userId) });
      const data = await homeAPI.getClasses();
      set({ classes: data });
    } catch {
      set({ error: "Không thể tải danh sách lớp học", fetchTracker: null });
    } finally {
      set({ isLoading: false });
    }
  },

  // Luồng ép đồng bộ thủ công khi nhấn Refresh hoặc sau khi Thêm/Sửa/Xóa/Rời lớp
  forceRefresh: async (isAuthenticated, userId) => {
    if (!isAuthenticated || !userId) return;
    try {
      set({ isLoading: true, error: null });
      const data = await homeAPI.getClasses();
      set({ classes: data });
    } catch {
      set({ error: "Không thể tải danh sách lớp học" });
    } finally {
      set({ isLoading: false });
    }
  },

  // Dọn dẹp sạch sẽ dữ liệu cũ khi Logout tài khoản
  clearClasses: () => set({ classes: [], fetchTracker: null, error: null }),
}));
