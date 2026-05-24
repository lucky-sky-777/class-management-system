import { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { storage } from "@shared/storages";
import { AUTH_STORAGE_KEY } from "@features/auth/types/keyStorage";
import { useFetchCurrentUser } from "@features/auth/hooks/useFetchCurrentUser";

export const OAuth2RedirectHandler = () => {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();
    const { refetch } = useFetchCurrentUser();
    const [errorMsg, setErrorMsg] = useState<string | null>(null);

    const accessToken = searchParams.get("access-token");
    const refreshToken = searchParams.get("refresh-token");
    const backendError = searchParams.get("error-message");

    useEffect(() => {
        const processAuthentication = async () => {
            if (backendError) {
                setErrorMsg(decodeURIComponent(backendError));
                return;
            }

            if (accessToken && refreshToken) {
                try {
                    // Lưu JWT tokens vào LocalStorage
                    storage.set(AUTH_STORAGE_KEY.TOKEN, accessToken);
                    storage.set(AUTH_STORAGE_KEY.REFRESH, refreshToken);

                    // Tải thông tin tài khoản người dùng hiện tại lên Global Store
                    await refetch();

                    // Điều hướng thành công về trang chủ
                    navigate("/", { replace: true });
                } catch (err: any) {
                    console.error("Lỗi khi tải thông tin người dùng đăng nhập qua OAuth2:", err);
                    setErrorMsg("Không thể xác thực thông tin tài khoản sau khi đăng nhập.");
                }
            } else {
                // Trường hợp thiếu tham số xác thực thiết yếu
                setErrorMsg("Thông tin xác thực từ máy chủ không hợp lệ hoặc bị thiếu.");
            }
        };

        processAuthentication();
    }, [accessToken, refreshToken, backendError, refetch, navigate]);

    return (
        <div className="min-h-screen bg-paper flex flex-col items-center justify-center p-6">
            <div className="card w-full max-w-[450px] shadow-sm animate-fade-up p-8 text-center flex flex-col items-center">
                <div className="w-12 h-12 rounded-lg bg-sidebar-accent flex items-center justify-center font-serif font-semibold text-white text-xl mb-6 shadow-sm">
                    E
                </div>

                {!errorMsg ? (
                    <div className="space-y-4 flex flex-col items-center">
                        {/* Loading Spinner mượt mà phù hợp tone Warm Accent */}
                        <div className="w-10 h-10 border-2 border-warm-200 border-t-warm-400 rounded-full animate-spin mb-2"></div>
                        <h2 className="text-2xl font-serif font-semibold text-ink-1 tracking-tight">
                            Đang kết nối tài khoản...
                        </h2>
                        <p className="text-sm text-ink-2 font-sans">
                            Hệ thống đang thiết lập phiên đăng nhập an toàn của bạn. Vui lòng đợi trong giây lát.
                        </p>
                    </div>
                ) : (
                    <div className="space-y-4">
                        <div className="w-12 h-12 rounded-full bg-red-fill border border-red-border flex items-center justify-center text-red-text text-xl mx-auto mb-2 font-sans">
                            ✕
                        </div>
                        <h2 className="text-2xl font-serif font-semibold text-ink-red-text tracking-tight">
                            Đăng nhập không thành công
                        </h2>
                        <p className="text-sm text-ink-2 font-sans bg-surface-2 p-3.5 rounded border border-rule">
                            {errorMsg}
                        </p>
                        <div className="pt-4">
                            <button
                                onClick={() => navigate("/login", { replace: true })}
                                className="btn btn-primary w-full py-2.5 font-semibold transition-base"
                            >
                                Quay lại trang đăng nhập
                            </button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
};
