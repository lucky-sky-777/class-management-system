import React, { useEffect, useState } from "react";
import type { Activity } from "@features/activity/types";
import { Calendar, MapPin, Star, Award, Pencil, Trash2, LogOut, ArrowUpRight } from "lucide-react";
import { useAuth } from "@features/auth";
import { useRegistrations } from "@features/activity/hooks/useRegistrations";
import { ActivityRegistrationStatus } from "@shared/domain/enums";

interface ActivityCardProps {
    activity: Activity;
    myRole: string;
    onEdit: (activity: Activity) => void;
    onDelete: (id: number) => void;
    onViewRegistrations: (activity: Activity) => void;
}

const formatDate = (iso: string | null) => {
    if (!iso) return "—";
    return new Date(iso).toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
    });
};

export const ActivityCard: React.FC<ActivityCardProps> = ({
    activity,
    myRole,
    onEdit,
    onDelete,
    onViewRegistrations,
}) => {
    const { user } = useAuth();
    const isAdminOrOwner = myRole === "OWNER" || myRole === "CLASS_ADMIN";
    
    // Quản lý đăng ký của chính học sinh này
    const { registrations, fetchRegistrations, register, cancel } = useRegistrations(activity.classId);
    const [isActionLoading, setIsActionLoading] = useState(false);

    useEffect(() => {
        if (activity.id != null && !isAdminOrOwner) {
            fetchRegistrations(activity.id);
        }
    }, [activity.id, fetchRegistrations, isAdminOrOwner]);

    // Tìm đăng ký của user hiện tại
    const myReg = registrations.find(
        (r) => r.registeredUser.id === user?.id && r.status !== ActivityRegistrationStatus.CANCELLED
    );

    const handleRegister = async () => {
        if (isActionLoading) return;
        setIsActionLoading(true);
        try {
            await register(activity.id);
        } finally {
            setIsActionLoading(false);
        }
    };

    const handleCancel = async () => {
        if (!myReg || isActionLoading) return;
        if (window.confirm("Bạn có chắc chắn muốn hủy đăng ký tham gia hoạt động này?")) {
            setIsActionLoading(true);
            try {
                await cancel(myReg.id);
            } finally {
                setIsActionLoading(false);
            }
        }
    };

    const renderRegistrationStatus = () => {
        if (isAdminOrOwner) return null;

        if (myReg) {
            switch (myReg.status) {
                case ActivityRegistrationStatus.APPROVED:
                    return (
                        <span className="pill pill-green font-semibold">
                            <span className="pill-dot bg-[var(--green-text)]" />
                            Đã duyệt tham gia
                        </span>
                    );
                case ActivityRegistrationStatus.REJECTED:
                    return (
                        <span className="pill pill-red font-semibold">
                            <span className="pill-dot bg-[var(--red-text)]" />
                            Từ chối tham gia
                        </span>
                    );
                case ActivityRegistrationStatus.PENDING:
                default:
                    return (
                        <div className="flex items-center gap-2">
                            <span className="pill pill-amber font-semibold">
                                <span className="pill-dot bg-[var(--amber-text)]" />
                                Chờ phê duyệt
                            </span>
                            <button
                                onClick={handleCancel}
                                disabled={isActionLoading}
                                className="text-2xs text-[#991B1B] hover:underline flex items-center gap-0.5 ml-1 disabled:opacity-50"
                            >
                                <LogOut className="w-3 h-3" />
                                Hủy đăng ký
                            </button>
                        </div>
                    );
            }
        }

        // Chưa đăng ký
        if (activity.isMandatory) {
            return (
                <span className="pill pill-red font-semibold">
                    <span className="pill-dot bg-[var(--red-text)]" />
                    Bắt buộc tham gia
                </span>
            );
        }

        return (
            <button
                onClick={handleRegister}
                disabled={isActionLoading}
                className="btn btn-warm btn-sm py-1 px-3 text-xs font-semibold rounded text-white bg-[#C2714F] hover:bg-[#A85A38] disabled:opacity-50 flex items-center gap-1"
            >
                {isActionLoading ? (
                    <span className="w-3 h-3 border-2 border-white/30 border-t-white rounded-full animate-spin"></span>
                ) : (
                    <ArrowUpRight className="w-3.5 h-3.5" />
                )}
                Đăng ký tham gia
            </button>
        );
    };

    return (
        <div className="card group transition-all duration-300 hover:shadow-md hover:border-warm-border">
            <div className="p-5 flex flex-col gap-4">
                {/* Header */}
                <div className="flex items-start justify-between gap-3">
                    <div className="flex items-start gap-3 flex-1 min-w-0">
                        <div className="w-10 h-10 rounded-lg bg-warm-fill flex items-center justify-center shrink-0 mt-0.5">
                            <Award className="w-5 h-5 text-warm-text" />
                        </div>
                        <div className="min-w-0">
                            <h3 className="font-serif font-semibold text-lg text-ink-1 leading-tight truncate">
                                {activity.name}
                            </h3>
                            {activity.description && (
                                <p className="text-sm text-ink-2 mt-1 line-clamp-2 leading-relaxed">
                                    {activity.description}
                                </p>
                            )}
                        </div>
                    </div>
                    {/* Admin actions */}
                    {isAdminOrOwner && (
                        <div className="flex gap-1 shrink-0 opacity-0 group-hover:opacity-100 transition-opacity">
                            <button
                                onClick={() => onEdit(activity)}
                                className="p-2 rounded-lg text-ink-3 hover:text-ink-blue-text hover:bg-ink-blue-fill transition-colors"
                                title="Chỉnh sửa"
                            >
                                <Pencil className="w-4 h-4" />
                            </button>
                            <button
                                onClick={() => onDelete(activity.id as number)}
                                className="p-2 rounded-lg text-ink-3 hover:text-ink-red-text hover:bg-ink-red-fill transition-colors"
                                title="Xóa hoạt động"
                            >
                                <Trash2 className="w-4 h-4" />
                            </button>
                        </div>
                    )}
                </div>

                {/* Meta info */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-2 text-sm text-ink-2">
                    <div className="flex items-center gap-2">
                        <Calendar className="w-4 h-4 text-ink-3 shrink-0" />
                        <span>
                            {formatDate(activity.startAt)}
                            {activity.endAt && (
                                <>
                                    <span className="text-ink-4 mx-1.5">→</span>
                                    {formatDate(activity.endAt)}
                                </>
                            )}
                        </span>
                    </div>
                    {activity.location && (
                        <div className="flex items-center gap-2">
                            <MapPin className="w-4 h-4 text-ink-3 shrink-0" />
                            <span className="truncate">{activity.location}</span>
                        </div>
                    )}
                </div>

                {/* Footer */}
                <div className="flex items-center justify-between pt-2 border-t border-rule">
                    <div className="flex items-center gap-3">
                        {activity.point != null && (
                            <div className="flex items-center gap-1.5">
                                <Star className="w-3.5 h-3.5 text-amber-text" />
                                <span className="text-sm font-semibold text-ink-1 font-mono">
                                    +{activity.point} điểm
                                </span>
                            </div>
                        )}
                        {activity.isMandatory ? (
                            <span className="pill pill-red">
                                <span className="pill-dot" />
                                Bắt buộc
                            </span>
                        ) : (
                            <span className="pill pill-blue">
                                <span className="pill-dot" />
                                Tự nguyện
                            </span>
                        )}
                    </div>

                    <div className="flex items-center gap-2">
                        {isAdminOrOwner ? (
                            <button
                                onClick={() => onViewRegistrations(activity)}
                                className="btn btn-ghost btn-sm font-bold text-ink-2 hover:text-ink-1"
                            >
                                Xem đăng ký →
                            </button>
                        ) : (
                            renderRegistrationStatus()
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};
