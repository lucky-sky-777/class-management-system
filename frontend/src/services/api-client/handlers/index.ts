import { storage } from "@shared/storages";
import { useAuthStore } from "@features/auth/hooks/useAuthStore";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { toast } from "react-toastify";

export function handler401() {
    storage.remove(AUTH_STORAGE_KEY.TOKEN);
    storage.remove(AUTH_STORAGE_KEY.REFRESH);
    useAuthStore.getState().logout();
}

export function handler403(message?: string | null) {
    toast.error( message || "bạn không có quyền làm việc này.");
}