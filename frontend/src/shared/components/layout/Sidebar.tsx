import { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import {
  Home,
  MonitorPlay,
  Users,
  ChevronDown,
  ChevronUp,
  Lock,
  Globe,
} from "lucide-react";
import { useHome } from "@features/home/hooks/useHome";
import { useUIStore } from "@app/store";

export const Sidebar = () => {
  const [isRegisteredOpen, setIsRegisteredOpen] = useState(true);
  const { classes, isLoading, refresh } = useHome();
  
  const isSidebarOpen = useUIStore((state: any) => state.isSidebarOpen);
  const setSidebarOpen = useUIStore((state: any) => state.setSidebarOpen || state.toggleSidebar);

  useEffect(() => {
    refresh();

    const handleRefresh = () => refresh();
    window.addEventListener("refreshHomeClasses", handleRefresh);

    return () => {
      window.removeEventListener("refreshHomeClasses", handleRefresh);
    };
  }, []);

  const myClasses = classes || [];

  return (
    <>
      {isSidebarOpen && (
        <div 
          className="fixed inset-0 bg-black/60 backdrop-blur-sm z-30 md:hidden transition-opacity"
          onClick={() => setSidebarOpen?.(false)}
        />
      )}


      <aside
        className={`bg-surface border-r border-rule flex flex-col fixed md:sticky top-[64px] z-40 h-[calc(100vh-64px)] w-64 transition-transform duration-300 ease-in-out ${
          isSidebarOpen ? "translate-x-0" : "-translate-x-full md:-ml-64"
        }`}
      >
        <nav className="flex-1 py-2 overflow-y-auto p-2.5">
          <NavLink to="/" end>
            {({ isActive }: { isActive: boolean }) => (
              <div 
                className={`flex items-center gap-3 px-3 py-2.5 rounded-xl transition-all font-medium ${
                  isActive 
                    ? "bg-blue-fill text-blue-text" 
                    : "text-ink-2 hover:bg-surface-2 hover:text-ink-1"
                }`}
              >
                <Home size={20} className="shrink-0" />
                <span>Trang chủ</span>
                {isActive && (
                  <span className="ml-auto w-1.5 h-1.5 rounded-full bg-blue-text"></span>
                )}
              </div>
            )}
          </NavLink>

          <div className="mt-4">
            <div
              className="flex items-center justify-between px-3 py-2 cursor-pointer text-ink-2 hover:text-ink-1 hover:bg-surface-2 rounded-xl transition-colors"
              onClick={() => setIsRegisteredOpen(!isRegisteredOpen)}
            >
              <div className="flex items-center gap-3">
                <MonitorPlay size={20} className="shrink-0" />
                <span className="text-2xs font-bold tracking-wider uppercase">
                  Đã đăng ký
                </span>
              </div>
              {isRegisteredOpen ? (
                <ChevronUp size={14} />
              ) : (
                <ChevronDown size={14} />
              )}
            </div>

            {isRegisteredOpen && (
              <ul className="mt-1 flex flex-col gap-0.5">
                {isLoading ? (
                  <li className="px-10 py-3">
                    <div className="skeleton h-4 w-full opacity-20 rounded-md"></div>
                  </li>
                ) : myClasses.length > 0 ? (
                  myClasses.map((item) => (
                    <li key={item.id}>
                      <NavLink to={`/class/${item.id}/diagram`}>
                        {({ isActive }: { isActive: boolean }) => (
                          <div
                            className={`group flex items-center justify-between px-3 py-2.5 rounded-xl transition-all ${
                              isActive 
                                ? "bg-blue-fill text-blue-text" 
                                : "text-ink-2 hover:bg-surface-2 hover:text-ink-1"
                            }`}
                          >
                            <div className="flex items-center gap-3 truncate">
                              <Users
                                size={18}
                                className="shrink-0"
                              />
                              <span className="text-sm font-medium truncate leading-tight">
                                {item.name}
                              </span>
                            </div>

                            <div
                              title={
                                item.privacy === "PUBLIC"
                                  ? "Cộng đồng"
                                  : "Nhóm kín"
                              }
                            >
                              {item.privacy === "PUBLIC" ? (
                                <Globe
                                  size={12}
                                  className={isActive ? "text-blue-text opacity-80" : "text-green-text opacity-60"}
                                />
                              ) : (
                                <Lock
                                  size={12}
                                  className={isActive ? "text-blue-text opacity-80" : "text-amber-text opacity-80"}
                                />
                              )}
                            </div>
                          </div>
                        )}
                      </NavLink>
                    </li>
                  ))
                ) : (
                  <li className="px-10 py-4 text-xs text-ink-3 opacity-80 italic">
                    Chưa tham gia lớp nào
                  </li>
                )}
              </ul>
            )}
          </div>
        </nav>
      </aside>
    </>
  );
};