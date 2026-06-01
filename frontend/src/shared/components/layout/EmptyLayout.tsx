import React from "react";
import { usePageTitle } from "@shared/hooks/usePageTitle";

interface EmptyLayoutProps {
    children: React.ReactNode;
}

export const EmptyLayout = ({ children }: EmptyLayoutProps) => {
    usePageTitle();

    return (
        <main>
            {children}
        </main>
    );
};