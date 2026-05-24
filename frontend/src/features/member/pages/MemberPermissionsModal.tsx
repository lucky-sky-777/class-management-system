// src/features/member/pages/MemberPermissionsModal.tsx
import React, { useState, useEffect } from "react";
import { X, Shield, Users, Check } from "lucide-react";
import type { Member, MemberRole } from "@features/member/types";
import { PermissionCode } from "@shared/domain/enums";

interface MemberPermissionsModalProps {
    isOpen: boolean;
    onClose: () => void;
    member: Member | null;
    onSave: (role: MemberRole, permissions: PermissionCode[]) => Promise<void>;
    isSubmitting: boolean;
}

const ALL_PERMISSIONS = [
    {
        code: PermissionCode.MANAGE_ACTIVITY,
        label: "Quản lý hoạt động",
        description: "Tạo và phê duyệt đăng ký hoạt động, quản lý điểm rèn luyện.",
    },
    {
        code: PermissionCode.MANAGE_GROUP,
        label: "Quản lý tổ",
        description: "Tạo, chỉnh sửa tổ và quản lý sơ đồ chỗ ngồi của lớp.",
    },
    {
        code: PermissionCode.MANAGE_FUND,
        label: "Quản lý thu chi",
        description: "Quản lý quỹ lớp, tạo yêu cầu đóng góp và phê duyệt hóa đơn.",
    },
    {
        code: PermissionCode.MANAGE_ABSENCE_REQUEST,
        label: "Quản lý đơn xin nghỉ",
        description: "Phê duyệt hoặc từ chối các đơn xin nghỉ học của thành viên.",
    },
    {
        code: PermissionCode.MANAGE_POINT,
        label: "Quản lý điểm thi đua",
        description: "Ghi nhận điểm thi đua cho các cá nhân và tổ chức trong lớp.",
    },
];

export const MemberPermissionsModal = ({
    isOpen,
    onClose,
    member,
    onSave,
    isSubmitting,
}: MemberPermissionsModalProps) => {
    const [selectedRole, setSelectedRole] = useState<MemberRole>("CLASS_MEMBER");
    const [selectedPermissions, setSelectedPermissions] = useState<PermissionCode[]>([]);

    useEffect(() => {
        if (member) {
            setSelectedRole(member.role === "OWNER" ? "CLASS_ADMIN" : member.role);
            setSelectedPermissions(member.permissions || []);
        }
    }, [member, isOpen]);

    if (!isOpen || !member) return null;

    const handleRoleChange = (role: MemberRole) => {
        setSelectedRole(role);
        if (role === "CLASS_ADMIN") {
            // Admin has all permissions
            setSelectedPermissions(ALL_PERMISSIONS.map(p => p.code));
        } else {
            // Member permissions default empty if not specified
            setSelectedPermissions([]);
        }
    };

    const handlePermissionToggle = (code: PermissionCode) => {
        if (selectedRole === "CLASS_ADMIN") return; // Admin has all, cannot modify

        setSelectedPermissions(prev =>
            prev.includes(code) ? prev.filter(c => c !== code) : [...prev, code]
        );
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        await onSave(selectedRole, selectedPermissions);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-xs p-4">
            <div className="bg-white dark:bg-stone-900 w-full max-w-lg rounded-xl shadow-lg border border-[var(--rule)] overflow-hidden flex flex-col max-h-[90vh]">
                {/* Header */}
                <div className="px-6 py-5 border-b border-[var(--rule)] flex items-center justify-between">
                    <div>
                        <h3 className="font-serif font-semibold text-xl tracking-tight text-[#1C1917] dark:text-[#FAFAF8]">
                            Phân quyền thành viên
                        </h3>
                        <p className="font-sans text-xs text-[#57534E] dark:text-[#A8A29E] mt-1">
                            Thiết lập vai trò và quyền hạn cho <span className="font-semibold text-[#1C1917] dark:text-[#FAFAF8]">{member.displayName}</span>
                        </p>
                    </div>
                    <button
                        onClick={onClose}
                        className="text-[#A8A29E] hover:text-[#1C1917] dark:hover:text-[#FAFAF8] transition-colors p-1 rounded-full hover:bg-stone-100 dark:hover:bg-stone-800"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {/* Body */}
                <form onSubmit={handleSubmit} className="flex-1 overflow-y-auto p-6 space-y-6">
                    {/* Role Selector */}
                    <div className="space-y-3">
                        <label className="font-sans text-2xs font-semibold tracking-label uppercase text-[#A8A29E]">
                            Vai trò trong lớp
                        </label>
                        <div className="grid grid-cols-2 gap-3">
                            <button
                                type="button"
                                onClick={() => handleRoleChange("CLASS_MEMBER")}
                                className={`flex items-start gap-3 p-4 rounded-lg border text-left transition-all ${
                                    selectedRole === "CLASS_MEMBER"
                                        ? "border-[#C2714F] bg-[#FBF0EC] dark:bg-[#2e1d16] text-[#A85A38]"
                                        : "border-[var(--rule)] hover:bg-[#F5F4F1] dark:hover:bg-stone-800 text-[#1C1917] dark:text-[#FAFAF8]"
                                }`}
                            >
                                <Users className="w-5 h-5 shrink-0 mt-0.5" />
                                <div>
                                    <div className="font-sans font-semibold text-sm">Thành viên</div>
                                    <div className="font-sans text-2xs opacity-85 mt-0.5">
                                        Chỉ thao tác trong quyền được Admin lớp cho phép.
                                    </div>
                                </div>
                            </button>

                            <button
                                type="button"
                                onClick={() => handleRoleChange("CLASS_ADMIN")}
                                className={`flex items-start gap-3 p-4 rounded-lg border text-left transition-all ${
                                    selectedRole === "CLASS_ADMIN"
                                        ? "border-[#C2714F] bg-[#FBF0EC] dark:bg-[#2e1d16] text-[#A85A38]"
                                        : "border-[var(--rule)] hover:bg-[#F5F4F1] dark:hover:bg-stone-800 text-[#1C1917] dark:text-[#FAFAF8]"
                                }`}
                            >
                                <Shield className="w-5 h-5 shrink-0 mt-0.5" />
                                <div>
                                    <div className="font-sans font-semibold text-sm">Ban cán sự</div>
                                    <div className="font-sans text-2xs opacity-85 mt-0.5">
                                        Admin phụ tá, có toàn quyền cấu hình dữ liệu lớp.
                                    </div>
                                </div>
                            </button>
                        </div>
                    </div>

                    {/* Permissions Section */}
                    <div className="space-y-3">
                        <div className="flex items-center justify-between">
                            <label className="font-sans text-2xs font-semibold tracking-label uppercase text-[#A8A29E]">
                                Quyền quản lý chi tiết
                            </label>
                            {selectedRole === "CLASS_ADMIN" && (
                                <span className="font-sans text-2xs text-[#A85A38] bg-[#FBF0EC] dark:bg-[#2e1d16] px-2 py-0.5 rounded-full font-semibold">
                                    Đã gán toàn quyền
                                </span>
                            )}
                        </div>

                        <div className="space-y-2 max-h-[300px] overflow-y-auto pr-1">
                            {ALL_PERMISSIONS.map(permission => {
                                const isChecked = selectedPermissions.includes(permission.code);
                                const isDisabled = selectedRole === "CLASS_ADMIN";

                                return (
                                    <div
                                        key={permission.code}
                                        onClick={() => handlePermissionToggle(permission.code)}
                                        className={`flex items-start gap-3 p-3.5 rounded-lg border transition-all cursor-pointer ${
                                            isDisabled
                                                ? "opacity-60 bg-stone-50 dark:bg-stone-800/40 border-[var(--rule)]"
                                                : isChecked
                                                ? "border-[#C2714F]/40 bg-[#FBF0EC]/30 dark:bg-[#2e1d16]/10"
                                                : "border-[var(--rule)] hover:bg-[#FAFAF8] dark:hover:bg-stone-800/50"
                                        }`}
                                    >
                                        <div className="flex items-center h-5 mt-0.5">
                                            <div
                                                className={`w-4.5 h-4.5 rounded flex items-center justify-center border transition-all ${
                                                    isChecked
                                                        ? "border-[#C2714F] bg-[#C2714F] text-white"
                                                        : "border-stone-300 dark:border-stone-600 bg-white dark:bg-stone-800"
                                                }`}
                                            >
                                                {isChecked && <Check className="w-3.5 h-3.5 stroke-[3px]" />}
                                            </div>
                                        </div>
                                        <div>
                                            <div className="font-sans font-semibold text-xs text-[#1C1917] dark:text-[#FAFAF8]">
                                                {permission.label}
                                            </div>
                                            <div className="font-sans text-2xs text-[#57534E] dark:text-[#A8A29E] mt-0.5">
                                                {permission.description}
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </div>
                </form>

                {/* Footer */}
                <div className="px-6 py-4 border-t border-[var(--rule)] bg-[#F5F4F1] dark:bg-stone-900/60 flex items-center justify-end gap-3 shrink-0">
                    <button
                        type="button"
                        onClick={onClose}
                        disabled={isSubmitting}
                        className="btn btn-secondary py-2 px-4 text-xs font-semibold rounded hover:bg-stone-200"
                    >
                        Hủy
                    </button>
                    <button
                        onClick={handleSubmit}
                        disabled={isSubmitting}
                        className="btn btn-warm py-2 px-5 text-xs font-semibold rounded text-white bg-[#C2714F] hover:bg-[#A85A38] disabled:opacity-50 flex items-center gap-1.5"
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
