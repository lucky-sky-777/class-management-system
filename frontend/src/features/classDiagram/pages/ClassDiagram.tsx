// src/features/classDiagram/pages/ClassDiagram.tsx
import { useState, useMemo } from "react";
import { useParams, Link } from "react-router-dom";
import { GraduationCap, Presentation, Shuffle } from "lucide-react";
import { useClassDiagram } from "@features/classDiagram/hooks/useClassDiagram";
import { Group } from "@features/classDiagram/pages/Group";

export const ClassDiagram = () => {
  const { classId } = useParams();
  const [mode, setMode] = useState<"view" | "attendance" | "setup">("view");
  const [selectedStudentId, setSelectedStudentId] = useState<string | null>(
    null,
  );
  const [perspective, setPerspective] = useState<"student" | "teacher">(
    "student",
  );
  const isTeacherView = perspective === "teacher";

  const {
    data,
    unseatedMembers,
    isLoading,
    shuffle,
    assignSeat,
    markAttendance,
  } = useClassDiagram(classId!, mode);

  // THUẬT TOÁN CHIA CỘT TỔ CHỖ NGỒI
  const currentGroups = data?.groups;
  const groupColumns = useMemo(() => {
    if (!currentGroups) return [];
    const sortedGroups = [...currentGroups].sort(
      (a, b) => a.groupId - b.groupId,
    );
    const columns: (typeof sortedGroups)[] = [];
    for (let i = 0; i < sortedGroups.length; i += 2) {
      columns.push(sortedGroups.slice(i, i + 2));
    }
    return columns;
  }, [currentGroups]);

  if (isLoading || !data) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">
            Đang tải sơ đồ lớp học...
          </p>
        </div>
      </div>
    );
  }
  const handleSeatClick = async (
    groupId: number,
    deskId: number,
    positionId: number,
  ) => {
    const targetGroup = data.groups.find((g) => g.groupId === groupId);
    const targetDesk = targetGroup?.desks.find((d) => d.deskId === deskId);
    const targetPos = targetDesk?.positions.find(
      (p) => p.positionId === positionId,
    );
    const studentAtSeat = targetPos?.student;

    if (mode === "attendance" && studentAtSeat?.id) {
      await markAttendance(groupId, studentAtSeat.id, studentAtSeat.status);
    }

    if (mode === "setup") {
      if (!selectedStudentId) {
        if (studentAtSeat) setSelectedStudentId(studentAtSeat.id);
        return;
      }
      if (selectedStudentId === studentAtSeat?.id) {
        setSelectedStudentId(null);
        return;
      }

      let sourceGroup: number | null = null;
      let sourceDesk: number | null = null;
      let sourcePos: number | null = null;

      data.groups.forEach((g) =>
        g.desks.forEach((d) =>
          d.positions.forEach((p) => {
            if (p.student?.id === selectedStudentId) {
              sourceGroup = p.student.groupId;
              sourceDesk = p.student.deskId;
              sourcePos = p.student.positionId;
            }
          }),
        ),
      );

      const isSuccess = await assignSeat(
        selectedStudentId,
        groupId,
        deskId,
        positionId,
        sourceGroup,
        sourceDesk,
        sourcePos,
      );

      if (isSuccess) setSelectedStudentId(null);
    }
  };

  return (
    <div
      className="space-y-6 select-none max-w-7xl mx-auto p-2 md:p-4 bg-[var(--bg-paper)] min-h-screen overflow-hidden"
      onClick={() => setSelectedStudentId(null)}
    >
      {/* 1. THANH CÔNG CỤ */}
      <div
        className="flex flex-col gap-4 bg-[var(--bg-surface)] p-3 rounded-xl border border-[var(--rule-md)] shadow-[var(--shadow-sm)] relative z-10"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 w-full">
          {
            <div className="flex bg-[var(--bg-surface-2)] p-1 rounded-lg w-full sm:w-auto border border-[var(--rule)]">
              {(["view", "attendance", "setup"] as const).map((m) => (
                <button
                  key={m}
                  onClick={() => setMode(m)}
                  className={`flex-1 sm:flex-none px-3 py-2 rounded-md text-[11px] md:text-xs font-bold transition-all flex items-center justify-center gap-1 ${
                    mode === m
                      ? "bg-[var(--bg-surface)] text-[var(--warm-600)] shadow-[var(--shadow-xs)]"
                      : "text-[var(--ink-2)] hover:text-[var(--ink-1)]"
                  }`}
                >
                  <span className="hidden xs:inline">
                    {m === "view"
                      ? "Xem"
                      : m === "attendance"
                        ? "Điểm danh"
                        : "Xếp chỗ"}
                  </span>
                  <span className="xs:hidden">
                    {m === "view"
                      ? "Xem"
                      : m === "attendance"
                        ? "Điểm danh"
                        : "Xếp"}
                  </span>
                </button>
              ))}
            </div>
          }

          <div className="flex items-center gap-2 overflow-x-auto w-full sm:w-auto pb-1 sm:pb-0 no-scrollbar justify-start sm:justify-end">
            <Badge theme="neutral" label="Sĩ số" val={data.totalStudents} />
            <Badge theme="success" label="Có mặt" val={data.presentCount} />
            <Badge theme="warning" label="Vắng phép" val={data.excusedCount} />
            <Badge
              theme="danger"
              label="Không phép"
              val={data.unexcusedCount}
            />
          </div>
        </div>
      </div>

      {/* 2. KHU VỰC ĐIỀU KHIỂN RIÊNG */}
      <div className="flex flex-col-reverse md:flex-row justify-between items-end gap-4 w-full relative z-10">
        <div className="w-full md:w-2/3" onClick={(e) => e.stopPropagation()}>
          {mode === "setup" && (
            <div className="bg-[var(--warm-fill)] p-3 rounded-xl border border-[var(--warm-border)] animate-in fade-in zoom-in-95 duration-300 flex flex-col gap-3">
              {/* Header: Tiêu đề & Nút Xếp tự động */}
              <div className="flex justify-between items-start sm:items-center gap-2 mb-3">
                {/* Cụm Tiêu đề + Ghi chú nằm chung 1 dòng */}
                <div className="flex-1 leading-snug">
                  <span className="text-[10px] md:text-xs font-bold text-[var(--warm-600)] uppercase tracking-wider">
                    Danh sách xếp chỗ
                  </span>
                  <span className="text-[9px] md:text-[10px] text-[var(--ink-3)] font-medium ml-1.5 normal-case tracking-normal inline-block">
                    (Cần phân tổ trước khi xếp chỗ.{" "}
                    <Link
                      to={`/class/${classId}/emulation`}
                      className="font-bold text-[var(--warm-600)] underline hover:text-[var(--warm-800)] transition-colors"
                    >
                      Tại đây!
                    </Link>
                    )
                  </span>
                </div>

                <button
                  onClick={() => shuffle()}
                  className="group flex items-center shrink-0 gap-1.5 px-3 py-1.5 bg-white border border-[var(--warm-border)] text-[var(--warm-600)] rounded-lg text-[10px] md:text-xs font-bold shadow-[var(--shadow-sm)] hover:bg-[var(--bg-surface-2)] transition-all active:scale-95"
                >
                  <Shuffle
                    size={14}
                    strokeWidth={2.5}
                    className="transition-transform duration-300 group-hover:rotate-45"
                  />

                  <span className="tracking-wide">Xếp tự động</span>
                </button>
              </div>

              <div className="flex flex-wrap gap-2 max-h-24 overflow-y-auto pr-2 custom-scrollbar">
                {unseatedMembers.map((m) => {
                  let isAssigned = false;
                  data.groups.forEach((g) =>
                    g.desks.forEach((d) =>
                      d.positions.forEach((p) => {
                        if (p.student?.id === m.id) isAssigned = true;
                      }),
                    ),
                  );

                  return (
                    <button
                      key={m.id}
                      onClick={() => setSelectedStudentId(m.id)}
                      className={`px-3 py-1.5 rounded-md text-[10px] md:text-xs font-bold border transition-all ${
                        selectedStudentId === m.id
                          ? "bg-[var(--warm-600)] text-white border-[var(--warm-600)] shadow-[var(--shadow-sm)]"
                          : isAssigned
                            ? "bg-[var(--bg-surface-3)] text-[var(--ink-3)] border-[var(--rule)]"
                            : "bg-[var(--bg-surface)] text-[var(--ink-1)] border-[var(--rule-md)] hover:bg-[var(--bg-surface-2)]"
                      }`}
                    >
                      {m.name} {isAssigned && "✓"}
                    </button>
                  );
                })}
              </div>
            </div>
          )}
        </div>

        <div
          className="flex justify-end w-full md:w-auto"
          onClick={(e) => e.stopPropagation()}
        >
          <button
            onClick={() =>
              setPerspective(isTeacherView ? "student" : "teacher")
            }
            className="group flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-[11px] md:text-xs font-bold bg-[var(--bg-surface)] text-[var(--warm-600)] border-2 border-[var(--warm-border)] hover:bg-[var(--warm-fill)] transition-all shadow-[var(--shadow-sm)] w-full sm:w-auto"
          >
            <div className="text-[var(--warm-600)] transition-transform duration-300 group-hover:scale-110">
              {isTeacherView ? (
                <Presentation size={15} strokeWidth={2.5} />
              ) : (
                <GraduationCap size={16} strokeWidth={2.5} />
              )}
            </div>

            <span className="uppercase tracking-wider">
              Góc nhìn: {isTeacherView ? "Giáo viên" : "Học sinh"}
            </span>
          </button>
        </div>
      </div>

      {/* 3. CONTAINER SƠ ĐỒ LỚP HỌC */}
      <div
        className="w-full bg-[var(--bg-surface-2)] rounded-3xl border border-[var(--rule-lg)] shadow-inner overflow-hidden flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        <div className="w-full overflow-x-auto custom-scrollbar p-6 md:p-12">
          {/* Main Layout wrapper: Xoay trên/dưới theo góc nhìn */}
          <div
            className={`flex ${isTeacherView ? "flex-col-reverse" : "flex-col"} gap-10 md:gap-16 transition-all duration-500 min-w-max mx-auto`}
          >
            <div
              className={`flex w-full items-center justify-center gap-8 md:gap-16 ${isTeacherView ? "flex-row-reverse" : "flex-row"}`}
            >
              {/* KHỐI 1: BÀN GIÁO VIÊN */}
              {/* Mặc định nằm TRÁI (Học sinh nhìn lên), khi lật sẽ nhảy sang PHẢI */}
              <div className="bg-[var(--bg-surface)] px-8 md:px-12 py-3.5 md:py-4 rounded-xl shadow-[var(--shadow-md)] border-2 border-[var(--warm-border)] flex items-center justify-center relative shrink-0 min-w-[160px]">
                <div className="absolute top-1 w-1/3 h-1 bg-[var(--rule)] rounded-full"></div>
                <span className="text-[11px] md:text-xs font-black text-[var(--warm-600)] uppercase tracking-[0.15em] mt-1">
                  Bàn Giáo Viên
                </span>
              </div>

              {/* KHỐI 2: BẢNG ĐEN */}
              {/* Mặc định nằm PHẢI (Học sinh nhìn lên), khi lật sẽ nhảy sang TRÁI */}
              <div className="flex flex-col items-center gap-2 shrink-0">
                <span className="text-[10px] md:text-[11px] font-black text-[var(--ink-3)] uppercase tracking-[0.4em]">
                  Bảng Đen
                </span>
                <div className="w-72 md:w-96 h-4 md:h-5 bg-[#1a1c23] rounded-md shadow-[0_5px_15px_rgba(0,0,0,0.12)] border-x-2 border-t-2 border-b-[4px] border-[#4a3f35] relative">
                  <div className="absolute bottom-0 w-full h-[1.5px] bg-white/20"></div>
                </div>
              </div>
            </div>

            {/* LƯỚI CHỖ NGỒI (Đã chia cột đối xứng) */}
            <div
              style={{
                transition: "transform 0.6s cubic-bezier(0.4, 0, 0.2, 1)",
              }}
              className={`flex flex-nowrap justify-center gap-10 md:gap-12 pb-10 ${mode !== "view" ? "cursor-crosshair" : ""} ${isTeacherView ? "rotate-180" : "rotate-0"}`}
            >
              {groupColumns.map((colGroups, colIndex) => (
                <div
                  key={`col-${colIndex}`}
                  className="flex flex-col gap-10 md:gap-12 relative w-fit"
                >
                  {/* Đường kẻ đứt nét chia luồng đi ở giữa */}
                  {colIndex === 0 && (
                    <div className="absolute -right-5 md:-right-6 top-0 bottom-0 w-px border-r-2 border-dashed border-[var(--rule-md)] opacity-40" />
                  )}

                  {colGroups.map((groupData) => (
                    <Group
                      key={groupData.groupId}
                      groupData={groupData}
                      isTeacherView={isTeacherView}
                      onSeatClick={handleSeatClick}
                      selectedStudentId={selectedStudentId}
                    />
                  ))}
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

const Badge = ({
  theme,
  label,
  val,
}: {
  theme: "neutral" | "success" | "warning" | "danger";
  label: string;
  val: number;
}) => {
  const themeClasses = {
    neutral:
      "bg-[var(--bg-surface-3)] text-[var(--ink-2)] border-[var(--rule-md)]",
    success:
      "bg-[var(--green-fill)] text-[var(--green-text)] border-[var(--green-border)]",
    warning:
      "bg-[var(--amber-fill)] text-[var(--amber-text)] border-[var(--amber-border)]",
    danger:
      "bg-[var(--red-fill)] text-[var(--red-text)] border-[var(--red-border)]",
  };

  return (
    <div
      className={`px-2 py-1.5 rounded-lg text-[10px] md:text-[11px] font-bold shadow-sm border flex items-center gap-1.5 whitespace-nowrap ${themeClasses[theme]}`}
    >
      <span className="opacity-70">{label}:</span>
      <span className="font-black text-[var(--ink-1)]">{val}</span>
    </div>
  );
};
