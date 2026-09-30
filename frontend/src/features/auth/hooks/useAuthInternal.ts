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
        async (
            requestOrUsername: RegisterRequest | string,
            passwordParam?: string,
            displayNameParam?: string,
            emailParam?: string,
            avatarUrlParam?: string
        ): Promise<{ success: boolean; userId?: number; error?: string }> => {
            setIsLoading(true);
            setError(null);
            try {
                let data: RegisterRequest;
                if (typeof requestOrUsername === "string") {
                    data = {
                        username: requestOrUsername,
                        password: passwordParam,
                        display_name: displayNameParam,
                        email: emailParam,
                        avatar_url: avatarUrlParam,
                    };
                } else {
                    data = requestOrUsername;
                }

                const response = await authApi.signUp(data);
                if (response.success && response.data) {
                    const userId = response.data.user_id || response.data.id;

                    // Tự động đăng nhập để lấy token nếu có password
                    if (data.password) {
                        try {
                            const loginResponse = await authApi.signIn({
                                username: data.username,
                                password: data.password,
                            });
                            if (loginResponse.success && loginResponse.data) {
                                const token = loginResponse.data;
                                storage.set(AUTH_STORAGE_KEY.TOKEN, token.access_token);
                                storage.set(AUTH_STORAGE_KEY.REFRESH, token.refresh_token);

                                const userData: User = {
                                    id: userId ? String(userId) : undefined,
                                    username: data.username,
                                    displayName: data.display_name,
                                    type: UserType.INTERNAL,
                                    avatarUrl: data.avatar_url || "",
                                    joinedAt: new Date().toISOString(),
                                    token: token.access_token,
                                };
                                setUser(userData);
                            }
                        } catch (loginErr) {
                            console.warn("Auto login after registration failed:", loginErr);
                        }
                    }

                    return { success: true, userId };
                } else {
                    const msg = response.message || "Đăng ký thất bại";
                    setError(msg);
                    return { success: false, error: msg };
                }
            } catch (err) {
                let errorMsg = "Lỗi đăng ký";
                if (err instanceof ApiError) {
                    if (err.code === 409 || err.status === 409) {
                        errorMsg = "Tên đăng nhập đã tồn tại, vui lòng chọn tên khác";
                    } else {
                        errorMsg = err.message || "Lỗi đăng ký";
                    }
                } else if (err instanceof Error) {
                    errorMsg = err.message;
                }
                setError(errorMsg);
                return { success: false, error: errorMsg };
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
