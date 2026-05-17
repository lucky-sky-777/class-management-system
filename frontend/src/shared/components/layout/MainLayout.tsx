import React, { useEffect } from 'react';
import { Header } from '@shared/components/layout/Header'; 
import { Sidebar } from '@shared/components/layout/Sidebar';
import { useUIStore } from '@app/store';

// Định nghĩa Type cho UI Store
interface UIStoreState {
  isSidebarOpen: boolean;
  setSidebarOpen: (isOpen: boolean) => void;
}

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout = ({ children }: MainLayoutProps) => {
  // const isSidebarOpen = useUIStore((state: UIStoreState) => state.isSidebarOpen);
  const setSidebarOpen = useUIStore((state: UIStoreState) => state.setSidebarOpen);

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth < 768) {
        setSidebarOpen(false);
      } else {
        setSidebarOpen(true);
      }
    };

    // Chạy thử 1 lần lúc mới load để check kích thước màn hình
    handleResize();

    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, [setSidebarOpen]);

  return (
    <div className="h-screen flex flex-col bg-[var(--bg-paper)] overflow-hidden transition-colors duration-300">
      <Header />
      
      <div className="flex flex-1 overflow-hidden relative">
        <Sidebar />
        <main className="flex-1 overflow-y-auto p-0 transition-all duration-300 ease-in-out relative">
          {children}
        </main>
      </div>
    </div>
  );
};