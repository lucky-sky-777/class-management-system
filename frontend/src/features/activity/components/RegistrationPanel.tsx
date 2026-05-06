import React, { useEffect } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { useRegistrations } from "@features/activity/hooks/useRegistrations";
import { ActivityRegistrationStatus } from "@features/activity/types";
import type { Activity } from "@features/activity/types";
import { CheckCircle, XCircle, ExternalLink } from "lucide-react";
import type { ID } from "@shared/utils/common";

interface RegistrationPanelProps {
    activity: Activity | null;
    isOpen: boolean;
    onClose: () => void;
}

const StatusPill: React.FC<{ status: string }> = ({ status }) => {
    switch (status) {
        case ActivityRegistrationStatus.APPROVED:
            return (
                <span className="pill pill-green">
                    <span className="pill-dot" />
                    Đã duyệt
                </span>
            );
        case ActivityRegistrationStatus.REJECTED:
            return (
                <span className="pill pill-red">
                    <span className="pill-dot" />
                    Đã từ chối
                </span>
            );
        default:
            return (
                <span className="pill pill-amber">
                    <span className="pill-dot" />
                    Chờ duyệt
                </span>
            );
    }
};

export const RegistrationPanel: React.FC<RegistrationPanelProps> = ({
    activity,
    isOpen,
    onClose,
}) => {
    const { registrations, isLoading, error, fetchRegistrations, approve, reject } =
        useRegistrations();

    useEffect(() => {
        if (isOpen && activity?.id != null) {
            fetchRegistrations(activity.id as ID);
        }
    }, [isOpen, activity?.id, fetchRegistrations]);

    const pendingCount = registrations.filter(
        (r) => r.status === ActivityRegistrationStatus.PENDING
    ).length;

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={
                <div className="flex items-center gap-2 min-w-0">
                    <span className="truncate">Danh sách đăng ký</span>
                    {pendingCount > 0 && (
                        <span className="shrink-0 text-xs font-mono bg-amber-fill text-amber-text border border-amber-border px-2 py-0.5 rounded-full">
                            {pendingCount} chờ duyệt
                        </span>
                    )}
                </div>
            }
        >
            {/* Activity name sub-title */}
            {activity && (
                <p className="text-xs text-ink-3 font-semibold uppercase tracking-widest mb-4 -mt-1">
                    {activity.name}
                </p>
            )}

            {isLoading && (
                <div className="space-y-3">
                    {[1, 2, 3].map((i) => (
                        <div key={i} className="h-16 skeleton rounded-xl" />
                    ))}
                </div>
            )}

            {error && (
                <div className="p-4 bg-ink-red-fill border border-ink-red-border text-ink-red-text rounded-lg text-sm">
                    {error}
                </div>
            )}

            {!isLoading && !error && registrations.length === 0 && (
                <div className="py-12 text-center space-y-2">
                    <p className="text-4xl">◎</p>
                    <p className="font-semibold text-ink-2">Chưa có ai đăng ký</p>
                    <p className="text-sm text-ink-3">Danh sách đăng ký hiện đang trống.</p>
                </div>
            )}

            {!isLoading && registrations.length > 0 && (
                <div className="divide-y divide-rule">
                    {registrations.map((reg) => (
                        <div
                            key={reg.id}
                            className="py-3.5 flex items-center gap-3"
                        >
                            {/* Avatar */}
                            <div className="w-9 h-9 rounded-full bg-surface-3 flex items-center justify-center text-ink-2 font-bold text-sm shrink-0 border border-rule">
                                {(reg.registeredUser.displayName ?? reg.registeredUser.username ?? "?")
                                    .charAt(0)
                                    .toUpperCase()}
                            </div>

                            {/* Info */}
                            <div className="flex-1 min-w-0">
                                <p className="font-semibold text-sm text-ink-1 truncate">
                                    {reg.registeredUser.displayName ?? reg.registeredUser.username}
                                </p>
                                <p className="text-xs text-ink-3 font-mono">
                                    {new Date(reg.registeredAt).toLocaleDateString("vi-VN", {
                                        day: "2-digit",
                                        month: "2-digit",
                                        hour: "2-digit",
                                        minute: "2-digit",
                                    })}
                                </p>
                            </div>

                            {/* Proof link */}
                            {reg.proofImageUrl && (
                                <a
                                    href={reg.proofImageUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="p-1.5 rounded-lg text-ink-3 hover:text-ink-blue-text hover:bg-ink-blue-fill transition-colors"
                                    title="Xem minh chứng"
                                >
                                    <ExternalLink className="w-3.5 h-3.5" />
                                </a>
                            )}

                            {/* Status or actions */}
                            {reg.status === ActivityRegistrationStatus.PENDING ? (
                                <div className="flex gap-1.5 shrink-0">
                                    <button
                                        onClick={() => approve(reg.id as ID)}
                                        className="p-1.5 rounded-lg text-ink-green-text hover:bg-ink-green-fill border border-ink-green-border transition-colors"
                                        title="Duyệt"
                                    >
                                        <CheckCircle className="w-4 h-4" />
                                    </button>
                                    <button
                                        onClick={() => reject(reg.id as ID)}
                                        className="p-1.5 rounded-lg text-ink-red-text hover:bg-ink-red-fill border border-ink-red-border transition-colors"
                                        title="Từ chối"
                                    >
                                        <XCircle className="w-4 h-4" />
                                    </button>
                                </div>
                            ) : (
                                <StatusPill status={reg.status} />
                            )}
                        </div>
                    ))}
                </div>
            )}

            {/* Summary footer */}
            {!isLoading && registrations.length > 0 && (
                <div className="mt-4 pt-4 border-t border-rule grid grid-cols-3 gap-2 text-center">
                    {[
                        {
                            label: "Đã duyệt",
                            count: registrations.filter((r) => r.status === ActivityRegistrationStatus.APPROVED).length,
                            color: "text-ink-green-text",
                        },
                        {
                            label: "Chờ duyệt",
                            count: pendingCount,
                            color: "text-amber-text",
                        },
                        {
                            label: "Từ chối",
                            count: registrations.filter((r) => r.status === ActivityRegistrationStatus.REJECTED).length,
                            color: "text-ink-red-text",
                        },
                    ].map((s) => (
                        <div key={s.label} className="bg-surface-2 rounded-lg py-2">
                            <p className={`font-mono font-bold text-lg ${s.color}`}>{s.count}</p>
                            <p className="text-xs text-ink-3">{s.label}</p>
                        </div>
                    ))}
                </div>
            )}
        </Modal>
    );
};
