// src/shared/components/layout/Header.tsx
import React, { useState, useRef, useEffect } from "react";
import { Menu, Plus, User, Hash, LogIn, LogOut, Sun, Moon } from "lucide-react";
import { useNavigate, useParams, useLocation } from "react-router-dom";
import { useHome } from "@features/home/hooks/useHome";
import { useAuth } from "@features/auth";
import { ChevronRight } from "lucide-react";
import { useUIStore } from "@app/store";
import { CreateClassModal } from "@features/home/pages/CreateClass";
import { JoinClassModal } from "@features/home/pages/JoinClass";
import { Avatar } from "@shared/components/ui/Avatar";

interface UIStoreState {
  toggleSidebar: () => void;
}

export const Header = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { classId } = useParams<{ classId: string }>();
  const [classCode, setClassCode] = useState("");
  const { user, isAuthenticated, logout } = useAuth();

  const toggleSidebar = useUIStore(
    (state: UIStoreState) => state.toggleSidebar,
  );
  // đã được Hook useHome lo liệu tự động từ bên trong!
  const { classes } = useHome();

  const [showUserMenu, setShowUserMenu] = useState(false);
  const [isCreateModalOpen, setIsCreateModalOpen] = useState(false);
  const [isJoinModalOpen, setIsJoinModalOpen] = useState(false);
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem("theme") || "light";
  });

  const userMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        userMenuRef.current &&
        !userMenuRef.current.contains(event.target as Node)
      ) {
        setShowUserMenu(false);
      }
    };
    if (showUserMenu) {
      document.addEventListener("mousedown", handleClickOutside);
    }
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [showUserMenu]);

  const currentClass = classes.find((item) => String(item.id) === classId);
  const isClassPage = location.pathname.includes("/class/") && classId;

  const handleLoginClick = () => {
    navigate("/login");
  };

  const handleLogout = async () => {
    setShowUserMenu(false); // Thu menu dropdown lại
    await logout();
    navigate("/login");
  };

  const handleJoinClass = (e: React.FormEvent) => {
    e.preventDefault();
    if (!classCode.trim()) return;
    console.log("Đang tìm lớp với mã:", classCode);
  };

  React.useEffect(() => {
    if (theme === "dark") {
      document.documentElement.classList.add("dark");
      localStorage.setItem("theme", "dark");
    } else {
      document.documentElement.classList.remove("dark");
      localStorage.setItem("theme", "light");
    }
  }, [theme]);

  const toggleTheme = () => {
    setTheme((prev) => (prev === "light" ? "dark" : "light"));
  };

  return (
    <header className="flex items-center justify-between h-16 px-4 bg-[var(--bg-surface)] border-b border-[var(--rule)] shrink-0 relative z-40 shadow-[var(--shadow-sm)]">
      <div className="flex items-center gap-2 sm:gap-4 flex-1">
        <button
          onClick={toggleSidebar}
          className="p-2 hover:bg-[var(--bg-surface-2)] rounded-full transition-colors"
        >
          <Menu size={22} className="text-[var(--ink-2)]" />
        </button>

        <nav className="flex items-center gap-2 overflow-hidden">
          <div
            className="flex items-center gap-2 cursor-pointer shrink-0"
            onClick={() => {
              if (location.pathname === "/") {
                window.dispatchEvent(new Event("refreshHomeClasses"));
              } else {
                navigate("/");
              }
            }}
          >
            <div className="w-8 h-8 bg-[var(--primary)] rounded-[var(--r-md)] flex items-center justify-center text-white font-bold shadow-[var(--shadow-sm)]">
              C
            </div>
            <h1 className="text-sm md:text-base font-bold text-[var(--ink-1)] hidden sm:block tracking-tight">
              Class Management
            </h1>
          </div>

          {isClassPage && (
            <div className="flex items-center gap-1.5 sm:gap-2.5 shrink-0">
              <ChevronRight className="text-[var(--ink-3)]" />
              <div className="flex items-center">
                <span
                  className="text-sm md:text-base font-black text-[var(--ink-1)] tracking-tight truncate 
                  max-w-[50px] xs:max-w-[120px] sm:max-w-[200px] md:max-w-[300px] lg:max-w-[400px]"
                >
                  {currentClass ? currentClass.name : "Đang tải..."}
                </span>
              </div>
            </div>
          )}
        </nav>
      </div>

      <div className="flex-[2] max-w-md mx-4 hidden sm:block">
        <form onSubmit={handleJoinClass} className="relative group">
          <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
            <Hash
              size={16}
              className="text-[var(--ink-3)] group-focus-within:text-[var(--primary)] transition-colors"
            />
          </div>
          <input
            type="text"
            placeholder="Tìm nhanh bằng mã..."
            value={classCode}
            onChange={(e) => setClassCode(e.target.value)}
            className="input-field w-full py-2 pl-10 pr-4 rounded-[var(--r-md)] outline-none"
          />
        </form>
      </div>

      <div className="flex items-center justify-end gap-2 sm:gap-3 flex-1">
        <button
          onClick={toggleTheme}
          title={
            theme === "light"
              ? "Chuyển sang giao diện tối"
              : "Chuyển sang giao diện sáng"
          }
          className="p-2 text-[var(--ink-2)] hover:bg-[var(--bg-surface-2)] rounded-full transition-all active:scale-95"
        >
          {theme === "light" ? <Moon size={20} /> : <Sun size={20} />}
        </button>

        {isAuthenticated && (
          <>
            <button
              onClick={() => setIsJoinModalOpen(true)}
              title="Tham gia lớp"
              className="flex items-center justify-center gap-1.5 p-2 md:px-3 md:py-1.5 text-[var(--primary)] hover:bg-[var(--primary-fill)] rounded-[var(--r-md)] font-bold transition-all text-sm border border-transparent hover:border-[var(--primary-border)] active:scale-95"
            >
              <LogIn
                size={20}
                strokeWidth={2.5}
                className="md:w-[18px] md:h-[18px] md:stroke-2"
              />
              <span className="hidden md:block">Tham gia</span>
            </button>

            <button
              onClick={() => setIsCreateModalOpen(true)}
              title="Tạo lớp mới"
              className="flex items-center justify-center gap-1.5 p-2 md:px-3 md:py-1.5 bg-[var(--warm-400)] text-white hover:bg-[var(--warm-600)] shadow-[var(--shadow-xs)] rounded-[var(--r-md)] font-bold text-sm transition-all active:scale-95"
            >
              <Plus
                size={20}
                strokeWidth={2.5}
                className="md:w-[18px] md:h-[18px] md:stroke-2"
              />
              <span className="hidden md:block">Tạo lớp</span>
            </button>
          </>
        )}

        <div className="relative" ref={userMenuRef}>
          <div
            onClick={
              isAuthenticated
                ? () => setShowUserMenu(!showUserMenu)
                : handleLoginClick
            }
            className={`ml-1 w-9 h-9 ${
              isAuthenticated
                ? "bg-[var(--primary-fill)] border-[var(--primary-border)]"
                : "bg-[var(--gold-fill)] border-[var(--gold-border)]"
            } rounded-full flex items-center justify-center overflow-hidden border cursor-pointer shrink-0 hover:ring-2 hover:ring-[var(--primary-border)] transition-all`}
          >
            {isAuthenticated && user ? (
              <Avatar name={user.displayName ?? user.username} />
            ) : (
              <User size={20} className="text-[var(--ink-3)]" />
            )}
          </div>

          {showUserMenu && isAuthenticated && (
            <div className="absolute right-0 mt-2 w-48 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-lg)] shadow-[var(--shadow-lg)] py-1 z-50 animate-scale-in">
              <div className="px-4 py-2 border-b border-[var(--rule)]">
                <p className="text-sm font-bold text-[var(--ink-1)] truncate">
                  {user?.displayName}
                </p>
                <p className="text-xs text-[var(--ink-2)] truncate">
                  @{user?.username}
                </p>
              </div>
              <button
                onClick={handleLogout}
                className="w-full text-left px-4 py-2.5 text-sm text-[var(--red-text)] hover:bg-[var(--red-fill)] flex items-center gap-2 transition-colors font-medium"
              >
                <LogOut size={16} />
                Đăng xuất
              </button>
            </div>
          )}
        </div>
      </div>

      <CreateClassModal
        isOpen={isCreateModalOpen}
        onClose={() => setIsCreateModalOpen(false)}
      />
      <JoinClassModal
        isOpen={isJoinModalOpen}
        onClose={() => setIsJoinModalOpen(false)}
        onSuccess={() => console.log("Tham gia lớp thành công...")}
      />
    </header>
  );
};