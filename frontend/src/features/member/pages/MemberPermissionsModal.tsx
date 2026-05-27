// src/features/member/pages/MemberPermissionsModal.tsx
import React, { useState, useEffect } from "react";
import { X, Shield, Users, Check } from "lucide-react";
import { useParams } from "react-router-dom";
import type {
  Member,
  MemberRole,
  ClassRoleOrPermission,
} from "@features/member/types";
import { PermissionCode } from "@shared/domain/enums";
import { memberAPI } from "@features/member/api";

interface MemberPermissionsModalProps {
  isOpen: boolean;
  onClose: () => void;
  member: Member | null;
  onSave: (role: MemberRole, permissions: PermissionCode[]) => Promise<void>;
  isSubmitting: boolean;
}

const PERMISSION_DESCRIPTIONS: Record<string, string> = {
  [PermissionCode.MANAGE_ACTIVITY]:
    "Tạo và phê duyệt đăng ký hoạt động, quản lý điểm rèn luyện.",
  [PermissionCode.MANAGE_GROUP]:
    "Tạo, chỉnh sửa tổ và quản lý sơ đồ chỗ ngồi của lớp.",
  [PermissionCode.MANAGE_FUND]:
    "Quản lý quỹ lớp, tạo yêu cầu đóng góp và phê duyệt hóa đơn.",
  [PermissionCode.MANAGE_ABSENCE_REQUEST]:
    "Phê duyệt hoặc từ chối các đơn xin nghỉ học của thành viên.",
  [PermissionCode.MANAGE_POINT]:
    "Ghi nhận điểm thi đua cho các cá nhân và tổ chức trong lớp.",
};

export const MemberPermissionsModal = ({
  isOpen,
  onClose,
  member,
  onSave,
  isSubmitting,
}: MemberPermissionsModalProps) => {
  const { classId } = useParams<{ classId: string }>();

  const [availablePermissions, setAvailablePermissions] = useState<
    ClassRoleOrPermission[]
  >([]);
  const [isLoadingPermissions, setIsLoadingPermissions] = useState(false);

  const [selectedRole, setSelectedRole] = useState<MemberRole>("CLASS_MEMBER");
  const [selectedPermissions, setSelectedPermissions] = useState<
    PermissionCode[]
  >([]);

  useEffect(() => {
    const fetchPermissions = async () => {
      if (isOpen && classId && availablePermissions.length === 0) {
        setIsLoadingPermissions(true);
        try {
          const response =
            await memberAPI.getClassRoleAndPermissionList(classId);
          setAvailablePermissions(response.permissions || []);
        } catch (error) {
          console.error("Lỗi lấy danh mục quyền:", error);
        } finally {
          setIsLoadingPermissions(false);
        }
      }
    };
    fetchPermissions();
  }, [isOpen, classId, availablePermissions.length]);

  useEffect(() => {
    if (member) {
      const currentRole = member.role === "OWNER" ? "CLASS_ADMIN" : member.role;
      setSelectedRole(currentRole);
      setSelectedPermissions(
        currentRole === "CLASS_ADMIN" ? [] : member.permissions || [],
      );
    }
  }, [member, isOpen]);

  if (!isOpen || !member) return null;

  const handleRoleChange = (role: MemberRole) => {
    if (role === selectedRole) return;

    setSelectedRole(role);
    // Khi đổi role (lên Ban cán sự hoặc xuống Thành viên), đều xóa trắng mảng quyền lẻ
    // Nếu xuống Thành viên: User sẽ tự tick lại.
    // Nếu lên Ban cán sự: Admin ngầm hiểu là full quyền, không cần lưu mảng.
    setSelectedPermissions([]);
  };

  const handlePermissionToggle = (code: string) => {
    // Đang là Ban cán sự thì khóa các nút quyền (không cho thao tác lẻ)
    if (selectedRole === "CLASS_ADMIN") return;

    const permCode = code as PermissionCode;
    setSelectedPermissions((prev) => {
      const newPerms = prev.includes(permCode)
        ? prev.filter((c) => c !== permCode)
        : [...prev, permCode];

      // Auto-Promote: Tick đủ quyền tự động nhảy lên Ban cán sự
      if (
        newPerms.length > 0 &&
        newPerms.length === availablePermissions.length
      ) {
        setSelectedRole("CLASS_ADMIN");
        return [];
      }
      return newPerms;
    });
  };

  const handleSubmit = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    await onSave(selectedRole, selectedPermissions);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-[var(--bg-overlay)] backdrop-blur-xs p-4">
      <div className="bg-[var(--bg-surface)] w-full max-w-lg rounded-xl shadow-[var(--shadow-lg)] border border-[var(--rule)] overflow-hidden flex flex-col max-h-[90vh]">
        {/* Header */}
        <div className="px-6 py-5 border-b border-[var(--rule)] flex items-center justify-between">
          <div>
            <h3 className="font-sans font-black text-xl tracking-tight text-[var(--ink-1)]">
              Phân quyền thành viên
            </h3>
            <p className="font-sans text-xs text-[var(--ink-2)] mt-1">
              Thiết lập vai trò và quyền hạn cho{" "}
              <span className="font-bold text-[var(--ink-1)]">
                {member.displayName}
              </span>
            </p>
          </div>
          <button
            onClick={onClose}
            className="text-[var(--ink-3)] hover:text-[var(--ink-1)] transition-colors p-1.5 rounded-full hover:bg-[var(--bg-surface-2)]"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Nội dung form duy nhất */}
        <form
          onSubmit={handleSubmit}
          className="flex-1 overflow-y-auto p-6 space-y-6"
        >
          <div className="space-y-3">
            <label className="font-sans text-[0.65rem] font-bold tracking-widest uppercase text-[var(--ink-3)]">
              Vai trò trong lớp
            </label>
            <div className="grid grid-cols-2 gap-3">
              <button
                type="button"
                onClick={() => handleRoleChange("CLASS_MEMBER")}
                className={`flex items-start gap-3 p-4 rounded-xl border text-left transition-all ${
                  selectedRole === "CLASS_MEMBER"
                    ? "border-[var(--primary)] bg-[var(--primary-fill)] text-[var(--primary-text)] shadow-sm"
                    : "border-[var(--rule)] hover:bg-[var(--bg-surface-2)] text-[var(--ink-1)]"
                }`}
              >
                <Users className="w-5 h-5 shrink-0 mt-0.5" />
                <div>
                  <div className="font-sans font-bold text-sm">
                    Quyền giới hạn
                  </div>
                  <div
                    className={`font-sans text-[10px] mt-0.5 ${selectedRole === "CLASS_MEMBER" ? "opacity-90 font-medium" : "text-[var(--ink-2)]"}`}
                  >
                    Chỉ thao tác trong quyền được cho phép.
                  </div>
                </div>
              </button>

              <button
                type="button"
                onClick={() => handleRoleChange("CLASS_ADMIN")}
                className={`flex items-start gap-3 p-4 rounded-xl border text-left transition-all ${
                  selectedRole === "CLASS_ADMIN"
                    ? "border-[var(--primary)] bg-[var(--primary-fill)] text-[var(--primary-text)] shadow-sm"
                    : "border-[var(--rule)] hover:bg-[var(--bg-surface-2)] text-[var(--ink-1)]"
                }`}
              >
                <Shield className="w-5 h-5 shrink-0 mt-0.5" />
                <div>
                  <div className="font-sans font-bold text-sm">
                    Quyền quản trị
                  </div>
                  <div
                    className={`font-sans text-[10px] mt-0.5 ${selectedRole === "CLASS_ADMIN" ? "opacity-90 font-medium" : "text-[var(--ink-2)]"}`}
                  >
                    Quản trị viên có toàn quyền quản lý lớp.
                  </div>
                </div>
              </button>
            </div>
          </div>

          <div className="space-y-3">
            <div className="flex items-center justify-between">
              <label className="font-sans text-[0.65rem] font-bold tracking-widest uppercase text-[var(--ink-3)]">
                Cấp quyền chi tiết
              </label>
              {selectedRole === "CLASS_ADMIN" && (
                <span className="font-sans text-[9px] text-[var(--primary-text)] bg-[var(--primary-fill)] border border-[var(--primary-border)] px-2.5 py-0.5 rounded-full font-bold uppercase tracking-wider">
                  Đã có toàn quyền
                </span>
              )}
            </div>

            <div className="space-y-2 max-h-[300px] overflow-y-auto pr-1 custom-scrollbar">
              {isLoadingPermissions ? (
                <div className="py-6 flex justify-center">
                  <div className="animate-spin rounded-full h-6 w-6 border-2 border-[var(--rule)] border-t-[var(--primary)]"></div>
                </div>
              ) : availablePermissions.length === 0 ? (
                <div className="py-4 text-center text-sm font-sans text-[var(--ink-3)] bg-[var(--bg-surface-2)] rounded-lg">
                  Không có dữ liệu quyền.
                </div>
              ) : (
                availablePermissions.map((permission) => {
                  const isChecked =
                    selectedRole === "CLASS_ADMIN" ||
                    selectedPermissions.includes(
                      permission.code as PermissionCode,
                    );
                  const isDisabled = selectedRole === "CLASS_ADMIN";
                  const description =
                    PERMISSION_DESCRIPTIONS[permission.code] ||
                    "Quyền hệ thống.";

                  return (
                    <div
                      key={permission.code}
                      onClick={() => handlePermissionToggle(permission.code)}
                      className={`group flex items-start gap-3 p-3.5 rounded-xl border transition-all ${
                        isDisabled
                          ? "opacity-60 bg-[var(--bg-surface-2)] border-[var(--rule)] cursor-not-allowed"
                          : isChecked
                            ? "border-[var(--primary-border)] bg-[var(--primary-fill)] cursor-pointer"
                            : "border-[var(--rule)] hover:bg-[var(--bg-surface-2)] cursor-pointer"
                      }`}
                    >
                      <div className="flex items-center h-5 mt-0.5">
                        <div
                          className={`w-4.5 h-4.5 rounded md:w-5 md:h-5 flex items-center justify-center border transition-all ${
                            isChecked
                              ? "border-[var(--primary)] bg-[var(--primary)] text-white shadow-sm"
                              : "border-[var(--ink-3)] bg-[var(--bg-surface)] group-hover:border-[var(--primary)]"
                          }`}
                        >
                          {isChecked && (
                            <Check className="w-3.5 h-3.5 md:w-4 md:h-4 stroke-[3px]" />
                          )}
                        </div>
                      </div>
                      <div className="flex-1">
                        <div
                          className={`font-sans font-bold text-xs md:text-sm ${isChecked ? "text-[var(--primary-text)]" : "text-[var(--ink-1)]"}`}
                        >
                          {permission.label}
                        </div>
                        <div className="font-sans text-[10px] md:text-xs text-[var(--ink-2)] mt-0.5 leading-relaxed">
                          {description}
                        </div>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          </div>
        </form>

        <div className="px-6 py-4 border-t border-[var(--rule)] bg-[var(--bg-surface-2)] flex items-center justify-end gap-3 shrink-0">
          <button
            type="button"
            onClick={onClose}
            disabled={isSubmitting}
            className="font-sans py-2.5 px-4 text-xs font-bold rounded-lg text-[var(--ink-2)] hover:text-[var(--ink-1)] hover:bg-[var(--rule-md)] transition-colors"
          >
            Hủy
          </button>
          <button
            onClick={handleSubmit}
            disabled={isSubmitting}
            className="font-sans py-2.5 px-6 text-xs font-bold rounded-lg text-white bg-[var(--primary)] hover:bg-[var(--primary-hover)] disabled:opacity-50 flex items-center gap-2 shadow-sm transition-all active:scale-95"
          >
            {isSubmitting ? (
              <>
                <span className="w-3.5 h-3.5 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                Đang lưu...
              </>
            ) : (
              "Lưu thay đổi"
            )}
          </button>
        </div>
      </div>
    </div>
  );
};
