import { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import { useMembers } from "@features/member/hooks/useMembers";
import { useAuth } from "@features/auth";
import { memberAPI } from "@features/member/api";
import { apiClient } from "@services/api-client";
import type { MemberRole, Member } from "@features/member/types";
import { ShieldPlus, ShieldMinus, CircleX } from "lucide-react";

// --- COMPONENT CON: HIỂN THỊ TỪNG THÀNH VIÊN ---
const MemberItem = ({ member, myRole, onUpdateRole, onKick }: any) => {
  const name = member.displayName || "Thành viên";
  const firstChar = name.trim().charAt(0).toUpperCase();

  return (
    <div className="group flex items-center justify-between py-3 px-2 hover:bg-slate-50 rounded-lg transition-all border-b border-slate-50 last:border-0">
      <div className="flex items-center gap-4">
        <div className="w-10 h-10 rounded-full bg-gradient-to-br from-indigo-500 to-purple-500 flex items-center justify-center text-white font-bold shadow-sm">
          {firstChar}
        </div>
        <div>
          <p className="text-sm font-semibold text-slate-700">{name}</p>
          <p className="text-xs text-slate-400">
            {member.username} Tham gia vào: {member.joinedAt}
          </p>
        </div>
      </div>

      <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
        {myRole === "OWNER" && member.role !== "OWNER" && (
          <>
            <button
              onClick={() => onUpdateRole(member.userId, member.role)}
              className="p-2 hover:bg-indigo-100 rounded-full text-indigo-600 transition-colors"
              title={member.role === "CLASS_ADMIN" ? "Hạ cấp" : "Thăng cấp"}
            >
              {member.role === "CLASS_ADMIN" ? <ShieldMinus/> : <ShieldPlus/>}
            </button>
            <button
              onClick={() => onKick(member.userId)}
              className="p-2 hover:bg-red-100 rounded-full text-red-500 transition-colors"
              title="Xóa khỏi nhóm"
            >
              <CircleX color="#ff0000" />
            </button>
          </>
        )}
        {myRole === "CLASS_ADMIN" && member.role === "CLASS_MEMBER" && (
          <button
            onClick={() => onKick(member.userId)}
            className="p-2 hover:bg-red-100 rounded-full text-red-500 transition-colors"
            title="Xóa khỏi nhóm"
          >
            🚫
          </button>
        )}
      </div>
    </div>
  );
};

// --- COMPONENT CON: PHÂN NHÓM ---
const GroupSection = ({
  title,
  data,
  colorClass,
  myRole,
  onUpdateRole,
  onKick,
}: any) => (
  <div className="mb-10">
    <div
      className={`flex justify-between items-center border-b-2 ${colorClass} pb-2 mb-4`}
    >
      <h3
        className={`text-lg font-bold uppercase tracking-wider ${colorClass.replace("border", "text")}`}
      >
        {title}
      </h3>
      <span className="text-xs font-bold text-slate-400">
        {data.length} người
      </span>
    </div>
    <div className="space-y-1">
      {data.length > 0 ? (
        data.map((m: Member) => (
          <MemberItem
            key={m.userId}
            member={m}
            myRole={myRole}
            onUpdateRole={onUpdateRole}
            onKick={onKick}
          />
        ))
      ) : (
        <p className="text-sm text-slate-400 italic py-2 px-2">Trống</p>
      )}
    </div>
  </div>
);

// --- COMPONENT CHÍNH ---
export const MemberPage = () => {
  const { classId } = useParams();
  const { user } = useAuth();
  const [classInfo, setClassInfo] = useState<any>(null);

  // 1. SỬA LỖI 405: Lấy thông tin lớp từ danh sách lớp (vì endpoint đơn lẻ bị lỗi)
  useEffect(() => {
    const fetchClassInfo = async () => {
      try {
        // Gọi API lấy danh sách lớp đã tham gia (Backend đã có sẵn)
        const res: any = await apiClient.get("/classes");
        const classes = res.data?.data || res.data || [];

        // Tìm lớp hiện tại trong danh sách để lấy owner_user_id
        const foundClass = classes.find(
          (c: any) => String(c.id) === String(classId),
        );

        if (foundClass) {
          setClassInfo(foundClass);
        } else {
          console.warn(
            "Không tìm thấy lớp học này trong danh sách đã tham gia.",
          );
        }
      } catch (err) {
        console.error("Lỗi fetch thông tin lớp:", err);
      }
    };
    if (classId) fetchClassInfo();
  }, [classId]);

  // 2. Truyền owner_user_id vào hook để "nhận diện" OWNER
  const { members, isLoading, myRole, refresh } = useMembers(
    classId!,
    user?.id,
    classInfo?.owner_user_id, // ID này sẽ dùng để map role "OWNER" ở Hook
  );

  const handleUpdateRole = async (userId: number, currentRole: MemberRole) => {
    try {
      const newRole: MemberRole =
        currentRole === "CLASS_ADMIN" ? "CLASS_MEMBER" : "CLASS_ADMIN";
      await memberAPI.updateRole(classId!, userId, newRole);
      refresh();
    } catch (error) {
      alert("Lỗi khi cập nhật quyền hạn");
    }
  };

  const handleKick = async (userId: number) => {
    if (window.confirm("Xóa thành viên này khỏi lớp học?")) {
      try {
        await memberAPI.kickMember(classId!, userId);
        refresh();
      } catch (error) {
        alert("Lỗi khi xóa thành viên");
      }
    }
  };

  // Chỉ hiển thị khi đã tải xong cả thông tin lớp và danh sách thành viên
  if (isLoading || !classInfo) {
    return (
      <div className="p-10 text-center text-slate-400 animate-pulse font-medium">
        Đang chuẩn bị dữ liệu...
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto py-8 px-4 bg-white min-h-screen">
      <header className="mb-12">
        <h2 className="text-2xl font-black text-slate-800 uppercase tracking-tight">
          Thành viên lớp học
        </h2>
        <p className="text-slate-500 text-sm">
          Lớp: {classInfo.name} ({classInfo.code})
        </p>
      </header>

      {/* Nhóm 1: Chủ sở hữu (Lọc những ai được Hook đánh nhãn OWNER) */}
      <GroupSection
        title="Chủ sở hữu"
        data={members.filter((m) => m.role === "OWNER")}
        colorClass="border-rose-500"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
      />

      {/* Nhóm 2: Quản trị viên */}
      <GroupSection
        title="Quản trị viên"
        data={members.filter((m) => m.role === "CLASS_ADMIN")}
        colorClass="border-indigo-500"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
      />

      {/* Nhóm 3: Thành viên thường */}
      <GroupSection
        title="Thành viên"
        data={members.filter((m) => m.role === "CLASS_MEMBER")}
        colorClass="border-emerald-500"
        myRole={myRole}
        onUpdateRole={handleUpdateRole}
        onKick={handleKick}
      />
    </div>
  );
};
