// src/features/classDiagram/pages/Seat.tsx
import type { StudentSeat } from "@features/classDiagram/types";

interface SeatProps {
  student: StudentSeat | null;
  onClick: (e: React.MouseEvent<HTMLDivElement>) => void;
  isTeacherView: boolean;
  isSelected?: boolean; // Mình vẫn giữ prop này ở đây để file Group.tsx truyền vào không bị báo lỗi đỏ nhé, nhưng mình không thèm dùng nó để đổi màu nữa
}

export const Seat = ({ student, onClick, isTeacherView }: SeatProps) => {
  const getStatusColor = () => {
    if (!student) return "bg-transparent";
    switch (student.status) {
      case "present":
        return "bg-green-500"; 
      case "absent_excused":
        return "bg-yellow-400"; 
      case "absent_unexcused":
        return "bg-red-600";
      case "late":
        return "bg-orange-500";
      default:
        return "bg-[var(--ink-4)]";
    }
  };

  const defaultAvatar = `https://ui-avatars.com/api/?name=${student?.name || "?"}&background=random`;

  return (
    <div
      onClick={onClick}
      className={`relative w-16 h-20 md:w-20 md:h-24 rounded-xl border flex flex-col items-center justify-center p-1 cursor-pointer transition-all hover:scale-105 shadow-[var(--shadow-sm)]
        ${student ? "border-[var(--warm-border)] bg-[var(--bg-surface)]" : "border-dashed border-[var(--rule-lg)] bg-[var(--bg-surface-2)] hover:bg-[var(--bg-surface-3)]"}
        ${isTeacherView ? "rotate-180" : ""}
      `}
    >
      {student ? (
        <>
          <div
            className={`absolute top-1 right-1 w-3 h-3 rounded-full border border-[var(--bg-surface)] shadow-sm z-10 ${getStatusColor()}`}
          />
          <div className="w-8 h-8 md:w-10 md:h-10 rounded-full overflow-hidden border border-[var(--rule-md)] shadow-sm mb-1 bg-[var(--bg-surface-2)] shrink-0">
            <img
              src={student.avatarUrl || defaultAvatar}
              alt={student.name}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="text-[9px] md:text-[10px] font-semibold text-[var(--ink-1)] text-center leading-tight line-clamp-2 w-full px-1">
            {student.name}
          </div>
        </>
      ) : (
        <div className="text-[var(--ink-4)] text-2xl font-light">+</div>
      )}
    </div>
  );
};