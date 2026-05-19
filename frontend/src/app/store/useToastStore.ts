import { create } from "zustand";
import { ToastType } from "@shared/domain/enums";

interface ToastState {
  isOpen: boolean;
  message: string;
  type: ToastType;
  showToast: (message: string, type?: ToastType) => void;
  hideToast: () => void;
}

// Biến giữ timeout nằm ngoài để không bị reset khi render lại
let toastTimeoutId: ReturnType<typeof setTimeout>;

export const useToastStore = create<ToastState>((set) => ({
  isOpen: false,
  message: "",
  type: ToastType.INFO,
  
  showToast: (message, type = ToastType.INFO) => {
    set({ isOpen: true, message, type });
    
    // Xóa timeout cũ nếu người dùng bấm liên tục
    if (toastTimeoutId) clearTimeout(toastTimeoutId);
    
    // Tự động tắt sau 3.5s
    toastTimeoutId = setTimeout(() => {
      set({ isOpen: false });
    }, 3500);
  },

  hideToast: () => set({ isOpen: false }),
}));