// src/features/classDiagram/pages/ClassDiagram.tsx
import { useState, useEffect, useMemo } from "react";
import { useParams } from "react-router-dom";
import { useClassDiagram } from "@features/classDiagram/hooks/useClassDiagram";
import type { AttendanceStatus } from "@features/classDiagram/types";
import { classDiagramAPI } from "@features/classDiagram/api";
import { useAuth } from "@features/auth";
import { ClassRole } from "@shared/domain/enums";
import { homeAPI } from "@features/home/api";
import { Group } from "@features/classDiagram/pages/Group";

export const ClassDiagram = () => {
  const { classId } = useParams();
  const { data, refresh, shuffle } = useClassDiagram(classId!);
  const [mode, setMode] = useState<"view" | "attendance" | "setup">("view");
  const [members, setMembers] = useState<{ id: string; name: string }[]>([]);
  const [selectedStudentId, setSelectedStudentId] = useState<string | null>(
    null,
  );

  const [perspective, setPerspective] = useState<"student" | "teacher">(
    "student",
  );
  const isTeacherView = perspective === "teacher";

  const { user } = useAuth();
  const [canEdit, setCanEdit] = useState(false);

  useEffect(() => {
    const checkPermission = async () => {
      if (!classId || !user?.id) return;
      try {
        const res = await homeAPI.getClassMembers(Number(classId));
        if (res.success) {
          const currentMember = res.data.find(
            (m) => String(m.user_id) === String(user.id),
          );
          if (currentMember && currentMember.role === ClassRole.CLASS_ADMIN) {
            setCanEdit(true);
          } else {
            setCanEdit(false);
          }
        }
      } catch (error) {
        console.error("Lỗi kiểm tra quyền:", error);
      }
    };
    checkPermission();
  }, [classId, user?.id]);

  useEffect(() => {
    let isMounted = true;
    const loadMembers = async () => {
      if (mode === "setup" && classId) {
        try {
          const memberList = await classDiagramAPI.getMembers(classId);
          if (isMounted) setMembers(memberList);
        } catch (error) {
          console.error(error);
        }
      } else {
        setMembers([]);
        setSelectedStudentId(null);
      }
    };
    loadMembers();
    return () => {
      isMounted = false;
    };
  }, [mode, classId]);

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

  if (!data)
    return (
      <div className="p-10 text-center animate-pulse text-[var(--ink-3)] font-medium">
        Đang tải dữ liệu...
      </div>
    );

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

    // --- LOGIC ĐIỂM DANH ---
    if (mode === "attendance" && studentAtSeat?.id) {
      // Logic vòng lặp trạng thái: Có mặt -> Vắng phép -> Vắng không phép -> Có mặt...
      const nextStatus: Record<string, AttendanceStatus> = {
        present: "absent_excused",
        absent_excused: "absent_unexcused",
        absent_unexcused: "present",
      };

      const newStatus = nextStatus[studentAtSeat.status] || "present";

      try {
        // Truyền thêm classId và groupId vào hàm
        await classDiagramAPI.updateAttendance(
          classId!,
          groupId,
          studentAtSeat.id,
          newStatus,
        );

        // Gọi điểm danh xong thì refresh lại sơ đồ để cập nhật màu ghế
        refresh();
      } catch (error) {
        console.error("Lỗi khi điểm danh:", error);
        alert("Điểm danh thất bại, vui lòng thử lại!");
      }
    }

    // --- LOGIC XẾP CHỖ & ĐỔI CHỖ (SWAP) ---
    if (mode === "setup") {
      // 1. CHƯA CÓ AI ĐƯỢC CHỌN (Nhấc lên)
      if (!selectedStudentId) {
        if (studentAtSeat) setSelectedStudentId(studentAtSeat.id);
        return;
      }

      // 2. ĐÃ CÓ NGƯỜI ĐƯỢC CHỌN VÀ BẤM LẠI VÀO CHÍNH HỌ (Bỏ xuống)
      if (selectedStudentId === studentAtSeat?.id) {
        setSelectedStudentId(null);
        return;
      }

      // 3. ĐÃ CÓ NGƯỜI ĐƯỢC CHỌN VÀ BẤM VÀO VỊ TRÍ KHÁC (Hoán đổi hoặc Di chuyển)
      try {
        let sourceGroup: number | null = null;
        let sourceDesk: number | null = null;
        let sourcePos: number | null = null;

        // Tìm Tọa độ Nguồn
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

        // Gọi API với Tọa độ Nguồn và Tọa độ Đích
        await classDiagramAPI.assignSeat(
          selectedStudentId,
          groupId,
          deskId,
          positionId,
          classId!,
          sourceGroup,
          sourceDesk,
          sourcePos,
        );
        setSelectedStudentId(null);
        refresh();
      } catch (error : any) {
        console.error("Lỗi khi gọi API xếp chỗ:", error);
        const backendMessage = error.response?.data?.message || "Xếp chỗ thất bại do lỗi không xác định!";
        alert(`❌ Lỗi Xếp Chỗ:\n${backendMessage}`);
      }
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
          {canEdit && (
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
          )}

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
              <div className="flex justify-between items-center">
                <span className="text-[10px] md:text-xs font-bold text-[var(--warm-600)] uppercase tracking-wider">
                  Danh sách xếp chỗ
                </span>
                <button
                  onClick={() => shuffle()}
                  className="flex items-center gap-1.5 px-3 py-1.5 bg-white border border-[var(--warm-border)] text-[var(--warm-600)] rounded-lg text-[10px] md:text-xs font-bold shadow-sm hover:bg-[var(--bg-surface-2)] transition-all"
                >
                  🎲 Xếp tự động
                </button>
              </div>

              <div className="flex flex-wrap gap-2 max-h-24 overflow-y-auto pr-2 custom-scrollbar">
                {members.map((m) => {
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
            <span className="text-sm">{isTeacherView ? "👨‍🏫" : "🎓"}</span>
            <span>Góc nhìn: {isTeacherView ? "Giáo viên" : "Học sinh"}</span>
          </button>
        </div>
      </div>

      {/* 3. CONTAINER SƠ ĐỒ LỚP HỌC */}
      <div
        className="w-full bg-[var(--bg-surface-2)] rounded-3xl border border-[var(--rule-lg)] shadow-inner overflow-hidden flex flex-col"
        onClick={(e) => e.stopPropagation()}
      >
        {/* THANH CUỘN NGANG TỰ DO */}
        <div className="w-full overflow-x-auto custom-scrollbar p-6 md:p-12">
          {/* LƯU Ý CỰC QUAN TRỌNG: 
       - Dùng `min-w-max` để các tổ không bao giờ bị co lại.
       - Dùng `mx-auto` để nếu ít tổ thì nó vẫn nằm giữa đẹp đẽ.
    */}
          <div
            className={`flex ${isTeacherView ? "flex-col-reverse" : "flex-col"} gap-12 transition-all duration-500 min-w-max mx-auto`}
          >
            {/* BẢNG ĐEN & BÀN GIÁO VIÊN */}
            <div
              className={`flex items-end justify-between w-full px-10 mb-8 ${
                isTeacherView ? "flex-row-reverse" : "flex-row"
              }`}
            >
              <div className="w-64">
                <div className="bg-yellow-400 px-8 py-4 rounded-2xl font-black text-slate-800 shadow-lg border-b-4 border-yellow-600 text-xs uppercase tracking-wider text-center">
                  Bàn Giáo Viên
                </div>
              </div>
              <div className="flex-1 flex justify-center">
                <div className="w-full max-w-md h-4 bg-slate-800 rounded-full shadow-2xl border border-slate-600 relative flex items-center justify-center">
                  <span
                    className={`absolute -top-8 text-[11px] text-slate-500 font-black uppercase tracking-[0.8em] ${isTeacherView ? "rotate-180" : ""}`}
                  >
                    BẢNG ĐEN
                  </span>
                </div>
              </div>
              <div className="w-64"></div> {/* Giữ cân bằng layout */}
            </div>

            {/* LƯỚI CHỖ NGỒI - NƠI CHỨA 6-8 TỔ */}
            <div
              style={{
                transition: "transform 0.6s cubic-bezier(0.4, 0, 0.2, 1)",
              }}
              className={`flex flex-nowrap justify-center gap-12 md:gap-24 pb-10 ${
                mode !== "view" ? "cursor-crosshair" : ""
              } ${isTeacherView ? "rotate-180" : "rotate-0"}`}
            >
              {groupColumns.map((colGroups, colIndex) => (
                <div
                  key={`col-${colIndex}`}
                  className="flex flex-col gap-12 md:gap-20 relative"
                >
                  {/* Đường kẻ phân cách giữa các dãy bàn (Cột) */}
                  {colIndex < groupColumns.length - 1 && (
                    <div className="absolute -right-6 md:-right-12 top-0 bottom-0 w-px border-r-2 border-dashed border-[var(--rule-md)] opacity-30" />
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
