import { createBrowserRouter } from "react-router-dom";
import { LoginPage } from "@features/auth/pages/LoginPage";
import { RegisterPage } from "@features/auth/pages/RegisterPage";
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
          { index: true, element: <ClassDiagram /> },
          { path: "nghiphep", element: <LeavePage /> },
          { path: "thidua", element: <Emulation /> },
          { path: "quy", element: <FundPage /> },
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
