import { createBrowserRouter } from "react-router-dom";
import { LoginPage, RegisterPage, OAuth2RedirectHandler } from "@features/auth";
import { GoogleCallbackPage } from "@features/auth/pages/GoogleCallbackPage";
import App from "@/App";
import { HomePage } from "@features/home/pages/HomePage";
import { ClassDiagram } from "@features/classDiagram/pages/ClassDiagram";
import { LeavePage } from "@features/leave/pages/LeavePage";
import { NotFoundPage } from "@features/error";
import { ClassLayout } from "@shared/components/layout/ClassLayout";
import { Emulation } from "@features/emulation/pages/Emulation";
import { FundPage } from "@features/fund";
import { ProtectedRoute } from "./ProtectedRoute";
import { ActivityPage } from "@features/activity";
import { MemberPage } from "@features/member/pages/MemberPage";
import { EmptyLayout } from "@shared/components/layout/EmptyLayout";

/**
 * Global application router configuration using React Router
 */
export const router = createBrowserRouter([
    {
        path: "/login",
        element: (
            <EmptyLayout>
                <LoginPage />
            </EmptyLayout>
        ),
        handle: {
            title: "Đăng nhập"
        }
    },
    {
        path: "/register",
        element: (
            <EmptyLayout>
                <RegisterPage />
            </EmptyLayout>
        ),
        handle: {
            title: "Đăng ký"
        }
    },
    {
        path: "/google-callback",
        element: (
            <EmptyLayout>
                <GoogleCallbackPage />
            </EmptyLayout>
        ),
        handle: {
            title: "Đăng nhập bằng Google"
        }
    },
    {
        path: "/oauth2-signin-redirect",
        element: (
            <EmptyLayout>
                <OAuth2RedirectHandler />
            </EmptyLayout>
        ),
        handle: {
            title: "Đang chuyển hướng"
        }
    },
    {
        path: "/",
        element: (
            <ProtectedRoute>
                <App />
            </ProtectedRoute>
        ),
        handle: {
            title: "Trang chủ"
        },
        children: [
            {
                index: true,
                element: <HomePage />,
                handle: {
                    title: "Trang chủ"
                }
            },
            {
                path: "class/:classId",
                element: <ClassLayout />,
                children: [
                    {
                        index: true,
                        path: "diagram",
                        element: <ClassDiagram />,
                        handle: {
                            title: "Sơ đồ lớp"
                        }
                    },
                    {
                        path: "absent",
                        element: <LeavePage />,
                        handle: {
                            title: "Nghỉ phép"
                        }
                    },
                    {
                        path: "emulation",
                        element: <Emulation />,
                        handle: {
                            title: "Thi đua"
                        }
                    },
                    {
                        path: "fund",
                        element: <FundPage />,
                        handle: {
                            title: "Quỹ lớp"
                        }
                    },
                    {
                        path: "activity",
                        element: <ActivityPage />,
                        handle: {
                            title: "Hoạt động"
                        }
                    },
                    {
                        path: "members",
                        element: <MemberPage />,
                        handle: {
                            title: "Thành viên"
                        }
                    },
                ],
            },
        ],
    },
    {
        path: "*",
        element: (
            <EmptyLayout>
                <NotFoundPage />
            </EmptyLayout>
        ),
        handle: {
            title: "Không tìm thấy trang"
        }
    },
]);
