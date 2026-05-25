import React, { useState, useEffect } from "react";
import { X, Shield, Users, Check } from "lucide-react";
import { useParams } from "react-router-dom";
import type { Member, MemberRole, ClassRoleOrPermission } from "@features/member/types";
import { PermissionCode } from "@shared/domain/enums";
import { memberAPI } from "@features/member/api"; 

interface MemberPermissionsModalProps {
    isOpen: boolean;
    onClose: () => void;
    member: Member | null;
    onSave: (role: MemberRole, permissions: PermissionCode[]) => Promise<void>;
    isSubmitting: boolean;
}

// 1. CHUYỂN ALL_PERMISSIONS THÀNH MỘT "TỪ ĐIỂN" (DICTIONARY)
// Dùng để tra cứu description dựa vào PermissionCode
const PERMISSION_DESCRIPTIONS: Record<string, string> = {
    [PermissionCode.MANAGE_ACTIVITY]: "Tạo và phê duyệt đăng ký hoạt động, quản lý điểm rèn luyện.",
    [PermissionCode.MANAGE_GROUP]: "Tạo, chỉnh sửa tổ và quản lý sơ đồ chỗ ngồi của lớp.",
    [PermissionCode.MANAGE_FUND]: "Quản lý quỹ lớp, tạo yêu cầu đóng góp và phê duyệt hóa đơn.",
    [PermissionCode.MANAGE_ABSENCE_REQUEST]: "Phê duyệt hoặc từ chối các đơn xin nghỉ học của thành viên.",
    [PermissionCode.MANAGE_POINT]: "Ghi nhận điểm thi đua cho các cá nhân và tổ chức trong lớp.",
};

export const MemberPermissionsModal = ({
    isOpen,
    onClose,
    member,
    onSave,
    isSubmitting,
}: MemberPermissionsModalProps) => {
    const { classId } = useParams<{ classId: string }>();
    
    // State lưu danh sách quyền ĐỘNG TỪ BACKEND
    const [availablePermissions, setAvailablePermissions] = useState<ClassRoleOrPermission[]>([]);
    const [isLoadingPermissions, setIsLoadingPermissions] = useState(false);

    const [selectedRole, setSelectedRole] = useState<MemberRole>("CLASS_MEMBER");
    const [selectedPermissions, setSelectedPermissions] = useState<PermissionCode[]>([]);

    // GỌI API LẤY DANH SÁCH QUYỀN
    useEffect(() => {
        const fetchPermissions = async () => {
            if (isOpen && classId && availablePermissions.length === 0) {
                setIsLoadingPermissions(true);
                try {
                    const response = await memberAPI.getClassRoleAndPermissionList(classId);
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

    // KHỞI TẠO STATE KHI CHỌN MEMBER
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
            // Check full quyền có sẵn từ Backend
            setSelectedPermissions(availablePermissions.map(p => p.code as PermissionCode));
        } else {
            setSelectedPermissions([]);
        }
    };

    const handlePermissionToggle = (code: string) => {
        if (selectedRole === "CLASS_ADMIN") return; 
        const permCode = code as PermissionCode;
        setSelectedPermissions(prev =>
            prev.includes(permCode) ? prev.filter(c => c !== permCode) : [...prev, permCode]
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
                            {isLoadingPermissions ? (
                                <div className="py-4 text-center text-sm text-[var(--ink-3)]">
                                    Đang tải danh sách quyền...
                                </div>
                            ) : availablePermissions.length === 0 ? (
                                <div className="py-4 text-center text-sm text-[var(--ink-3)]">
                                    Không có quyền nào được định nghĩa.
                                </div>
                            ) : (
                                /* MAP DỮ LIỆU TỪ BACKEND */
                                availablePermissions.map(permission => {
                                    const isChecked = selectedPermissions.includes(permission.code as PermissionCode);
                                    const isDisabled = selectedRole === "CLASS_ADMIN";
                                    
                                    // TÌM DESCRIPTION TỪ TỪ ĐIỂN
                                    const description = PERMISSION_DESCRIPTIONS[permission.code] || "Quyền hệ thống được cấp phát.";

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