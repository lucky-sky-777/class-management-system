import { createBrowserRouter } from "react-router-dom";
import { LoginPage, RegisterPage, OAuth2RedirectHandler } from "@features/auth";
import { GoogleCallbackPage } from "@features/auth/pages/GoogleCallbackPage";
import App from "@/App";
import { HomePage } from "@features/home/pages/HomePage";
import { ClassDiagram } from "@features/classDiagram/pages/ClassDiagram";
import { LeavePage } from "@features/leave/pages/LeavePage";
import { NotFoundPage } from "@features/error";
import { ClassLayout } from "@shared/components/layout/ClassLayout";
import { Emulation } from "@features/emulation/pages/Emulation"
import { FundPage } from "@features/fund"
import { ProtectedRoute } from "./ProtectedRoute";
import { ActivityPage } from "@features/activity";
import { MemberPage } from "@features/member/pages/MemberPage";

/**
 * Global application router configuration using React Router
 */
export const router = createBrowserRouter([
    {
        path: "/login",
        element: <LoginPage />,
    },
    {
        path: "/register",
        element: <RegisterPage />,
    },
    {
        path: "/google-callback",
        element: <GoogleCallbackPage />,
    },
    {
        path: "/oauth2-signin-redirect",
        element: <OAuth2RedirectHandler />,
    },
    {
        path: "/",
        element: (
            <ProtectedRoute>
                <App />
            </ProtectedRoute>
        ),
        children: [
            {
                index: true,
                element: <HomePage />,
            },
            {
                path: "class/:classId",
                element: <ClassLayout />,
                children: [
                    { index: true, path: "diagram", element: <ClassDiagram /> },
                    { path: "absent", element: <LeavePage /> },
                    { path: "emulation", element: <Emulation /> },
                    { path: "fund", element: <FundPage /> },
                    { path: "activity", element: <ActivityPage /> },
                    { path: "members", element: <MemberPage /> },
                ],
            },
        ],
    },
    {
        path: "*",
        element: <NotFoundPage />,
    },
]);
