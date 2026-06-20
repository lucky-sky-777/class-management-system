import { useState, useCallback } from "react";
import { authApi } from "@features/auth/api";
import type {
  LoginRequest,
  RegisterRequest
} from "@features/auth/types";
import type { User } from "@shared/domain/user";
import { useAuthStore } from "./useAuthStore";

import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { UserType } from "@shared/domain/enums";
import { ApiError } from "@services/api-client";
import type { ChangePasswordRequest } from "@features/auth/types";

/**
 * useAuthInternal: Chỉ dùng nội bộ trong feature auth (LoginPage, RegisterPage)
 * Chứa các logic xử lý form, loading state và error handling.
 */
export const useAuthInternal = () => {
  const { setUser, logout: storeLogout } = useAuthStore();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // đăng nhập
  // File: useAuthInternal.ts
  const login = useCallback(
    async (data: LoginRequest) => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await authApi.signIn(data);
        if (response.success && response.data) {
          console.log("Dữ liệu thật từ Backend trả về:", response.data);

          const token = response.data;

          //  luu token vào localStorage thông qua storage abstraction
          storage.set(AUTH_STORAGE_KEY.TOKEN, token.access_token);
          storage.set(AUTH_STORAGE_KEY.REFRESH, token.refresh_token);
          return true;
        } else {
          if (response.code === 401) {
            setError("Tên đăng nhập hoặc mật khẩu không đúng");
          } else {
            setError(response.message); 
          }
          return false;
        }
      } catch (err) {
        if (err instanceof ApiError) {
          if (err.status === 401) {
            setError("Tên đăng nhập hoặc mật khẩu không đúng");
          } else {
            setError(err.message || "Lỗi đăng nhập");
          }
        }
        return false;
      } finally {
        setIsLoading(false);
      }
    },
    [setUser],
  );

  // đăng kí
  const signup = useCallback(
    async (username: string, password: string, displayname: string) => {
      setIsLoading(true);
      setError(null);
      try {
        const data: RegisterRequest = {
          username,
          password,
          display_name: displayname,
        };
        const response = await authApi.signUp(data);
        if (response.success && response.data) {
          const authData = response.data as any;

          const userData: User = {
            id: authData.id || 1,
            username: data.username,
            displayName: data.display_name,
            type: UserType.INTERNAL,
            avatarUrl: "",
            joinedAt: new Date().toISOString(),
            token: authData.accessToken,
          };

          setUser(userData);
          return true;
        } else {
          setError(response.message);
          return false;
        }
      } catch (err) {
        if (err instanceof ApiError) {
          if (err.code === 409 || err.status === 409) {
            setError("Tên đăng nhập đã tồn tại, vui lòng chọn tên khác");
            return false
          }
          setError(err.message || "Lỗi đăng ký");
        }
        return false;
      } finally {
        setIsLoading(false);
      }
    },
    [setUser],
  );

  // đăng xuất
  const logout = useCallback(async () => {
    setIsLoading(true);
    try {
      
      const accessToken = storage.get<string>(AUTH_STORAGE_KEY.TOKEN) || "";
      const refreshToken = storage.get<string>(AUTH_STORAGE_KEY.REFRESH) || "";
      await authApi.signOut(accessToken, refreshToken);
    } catch (err) {
      console.error("Lỗi đăng xuất:", err);
      setError("Lỗi đăng xuất, vui lòng thử lại");
    } finally {
      storeLogout();
      setIsLoading(false);
    }
  }, [storeLogout]);

  //doi mat khau
  const changePassword = useCallback(
    async (oldPassword: string, newPassword: string) => {
      setIsLoading(true);
      setError(null);
      try {
        const data: ChangePasswordRequest = {
          old_password: oldPassword,
          new_password: newPassword,
        };
        
        await authApi.changePassword(data);
        return true;
      } catch (err) {
        if (err instanceof ApiError) {
          setError(err.message || "Lỗi đổi mật khẩu");
        } else {
          setError("Đã có lỗi xảy ra");
        }
        return false;
      } finally {
        setIsLoading(false);
      }
    },
    []
  );

  // lây token với mã auth code (OAuth2)
  const fetchTokenWithAuthCode = useCallback(
    async (code: string, provider: string) => {
      setIsLoading(true);
      setError(null);
      try {
        const response = await authApi.fetchJWTTokenWithAuthCode(code, provider);
        if (response.success && response.data) {
          const token = response.data;
          storage.set(AUTH_STORAGE_KEY.TOKEN, token.access_token);
          storage.set(AUTH_STORAGE_KEY.REFRESH, token.refresh_token);
          return true;
        } else {
          setError(response.message);
          return false;
        }
      } catch (err) {
        if (err instanceof ApiError) {
          setError(err.message || "Lỗi xác thực với mã OAuth");
        }
        return false;
      }
      finally {
        setIsLoading(false);
      }
    },[]);

  return {

    isLoading,
    error,
    login,
    signup,
    logout,
    changePassword,
    fetchTokenWithAuthCode,
  };
};
