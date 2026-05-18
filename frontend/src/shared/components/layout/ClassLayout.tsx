import { NavLink, Outlet, useParams } from "react-router-dom";

export const ClassLayout = () => {
  const { classId } = useParams<{ classId: string }>();

  const menuItems = [
    { path: "diagram", label: "Sơ đồ lớp" },
    { path: "fund", label: "Quỹ lớp" },
    { path: "emulation", label: "Thi đua" },
    { path: "absent", label: "Nghỉ phép" },
    { path: "activity", label: "Hoạt động" },
    { path: "members", label: "Thành viên" },
  ];

  return (
    <div className="flex flex-col h-full bg-[var(--bg-surface)] font-sans transition-colors duration-300">
      {/* THANH MENU CON */}
      <div className="flex items-stretch border-b border-[var(--rule)] px-4 bg-[var(--bg-surface)] sticky top-0 z-20 flex-nowrap overflow-x-auto no-scrollbar shadow-[var(--shadow-sm)] min-h-[56px] transition-colors duration-300">
        {menuItems.map((item) => (
          <NavLink
            key={item.path}
            to={
              item.path === ""
                ? `/class/${classId}`
                : `/class/${classId}/${item.path}`
            }
            end={item.path === ""}
            className={({ isActive }) =>
              `px-5 flex items-center justify-center text-[13px] md:text-sm font-bold transition-all whitespace-nowrap shrink-0 relative ${
                isActive
                  ? "text-[var(--primary-text)] bg-[var(--primary-fill)]"
                  : "text-[var(--ink-2)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)]"
              }`
            }
          >
            {({ isActive }) => (
              <>
                <span className="relative z-10">{item.label}</span>
                {isActive && (
                  // Gạch chân active dùng màu Primary (Blue)
                  <div className="absolute bottom-0 left-0 right-0 h-[2px] bg-[var(--primary)] z-20" />
                )}
              </>
            )}
          </NavLink>
        ))}
      </div>

      {/* Nội dung trang con */}
      <div className="p-4 md:p-6 overflow-y-auto">
        <div className="max-w-7xl mx-auto">
          <Outlet />
        </div>
      </div>
    </div>
  );
};
