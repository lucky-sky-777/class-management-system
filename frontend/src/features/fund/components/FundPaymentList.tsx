import React, { useEffect, useMemo, useState } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { CheckCircle, XCircle, Clock, ExternalLink, ShieldCheck } from "lucide-react";
import type { ID } from "@shared/utils/common";
import { useFundPayment } from "../hooks/useFund";
import type { FundPaymentStatus } from "../types";
import { useAuth } from "@features/auth";

interface FundPaymentListProps {
    isOpen: boolean;
    onClose: () => void;
    fundId: ID;
    classId: ID;
    fundTitle: string;
    fundDescription: string;
    isAdminOrOwner: boolean;
}

export const FundPaymentList: React.FC<FundPaymentListProps> = ({
    isOpen, onClose, fundId, classId, fundTitle, fundDescription, isAdminOrOwner
}) => {
    const { user } = useAuth();
    const { payments, fetchPayments, isLoading, approvePayment, rejectPayment, cancelPayment } = useFundPayment(classId);
    
    // Trạng thái xử lý cục bộ cho từng giao dịch
    const [processingId, setProcessingId] = useState<ID | null>(null);
    const [processingAction, setProcessingAction] = useState<"approve" | "reject" | "cancel" | null>(null);

    const handleApprove = async (paymentId: ID) => {
        setProcessingId(paymentId);
        setProcessingAction("approve");
        await approvePayment(fundId, paymentId);
        setProcessingId(null);
        setProcessingAction(null);
    };

    const handleReject = async (paymentId: ID) => {
        setProcessingId(paymentId);
        setProcessingAction("reject");
        await rejectPayment(fundId, paymentId);
        setProcessingId(null);
        setProcessingAction(null);
    };

    const handleCancel = async (paymentId: ID) => {
        setProcessingId(paymentId);
        setProcessingAction("cancel");
        await cancelPayment(fundId, paymentId);
        setProcessingId(null);
        setProcessingAction(null);
    };

    useEffect(() => {
        if (isOpen && fundId) {
            fetchPayments(fundId);
        }
    }, [isOpen, fundId, fetchPayments]);

    const fundPayments = useMemo(() => {
        const rawPayments = payments[fundId] || [];
        const sorted = rawPayments.slice().sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime());
        
        if (isAdminOrOwner) {
            return sorted;
        } else {
            return sorted.filter(p => p.creator_user_id === user?.id);
        }
    }, [payments, fundId, isAdminOrOwner, user?.id]);

    const getStatusBadge = (status: FundPaymentStatus) => {
        switch (status) {
            case "APPROVED":
                return <span className="px-2 py-1 bg-ink-green-fill text-ink-green-text rounded-full text-[10px] font-bold flex items-center gap-1"><CheckCircle className="w-3 h-3"/> ĐÃ DUYỆT</span>;
            case "REJECTED":
                return <span className="px-2 py-1 bg-ink-red-fill text-ink-red-text rounded-full text-[10px] font-bold flex items-center gap-1"><XCircle className="w-3 h-3"/> TỪ CHỐI</span>;
            case "CANCELLED":
                return <span className="px-2 py-1 bg-surface-2 text-ink-3 rounded-full text-[10px] font-bold">ĐÃ HỦY</span>;
            case "PENDING":
            default:
                return <span className="px-2 py-1 bg-warm-fill text-warm-text rounded-full text-[10px] font-bold flex items-center gap-1"><Clock className="w-3 h-3"/> CHỜ DUYỆT</span>;
        }
    };

    return (
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title={
                <div className="flex items-center gap-2">
                    <ShieldCheck className="w-5 h-5 text-warm-400" />
                    <span>Danh sách nộp minh chứng</span>
                </div>
            }
        >
            <div className="space-y-4">
                <div className="p-3 bg-surface-2 rounded-xl mb-4">
                    <p className="text-sm font-medium text-ink-2">Khoản thu:</p>
                    <p className="text-base font-bold text-ink-1">{fundTitle}</p>
                </div>
                <div className="p-3 bg-surface-2 rounded-xl mb-4">
                    <p className="text-sm font-medium text-ink-2">Mô tả:</p>
                    <p className="text-base font-bold text-ink-1">{fundDescription}</p>
                </div>

                {isLoading && fundPayments.length === 0 ? (
                    <div className="flex justify-center p-8">
                        <div className="w-8 h-8 border-4 border-warm-400/20 border-t-warm-400 rounded-full animate-spin"></div>
                    </div>
                ) : fundPayments.length === 0 ? (
                    <div className="text-center p-8 text-ink-3 bg-surface-2/30 rounded-xl border border-dashed border-rule">
                        <p>Chưa có thành viên nào nộp minh chứng</p>
                    </div>
                ) : (
                    <div className="space-y-3 max-h-[60vh] overflow-y-auto pr-2">
                        {fundPayments.map(payment => (
                            <div key={payment.id} className="border border-rule rounded-xl p-4 bg-white shadow-sm hover:shadow-md transition-all">
                                <div className="flex justify-between items-start mb-3">
                                    <div className="flex items-center gap-3">
                                        <div className="relative">
                                            {payment.creator_avatar_url ? (
                                                <img
                                                    src={payment.creator_avatar_url}
                                                    alt={payment.creator_display_name}
                                                    className="w-10 h-10 rounded-full object-cover"
                                                    onError={(e) => {
                                                        (e.target as HTMLImageElement).src = '';
                                                    }}
                                                />
                                            ) : (
                                                <div className="w-10 h-10 rounded-full bg-surface-3 flex items-center justify-center text-xs font-bold text-ink-2">
                                                    {payment.creator_display_name?.charAt(0).toUpperCase() || "U"}
                                                </div>
                                            )}
                                        </div>
                                        <div>
                                            <p className="font-semibold text-sm text-ink-1">{payment.creator_display_name}</p>
                                            <p className="text-[10px] text-ink-3">
                                                {new Date(payment.created_at).toLocaleString('vi-VN')}
                                            </p>
                                            {payment.actor_display_name && (
                                                <p className="text-[10px] text-ink-3 mt-1">Xử lý bởi {payment.actor_display_name}</p>
                                            )}
                                        </div>
                                    </div>
                                    {getStatusBadge(payment.status)}
                                </div>
                                
                                <div className="space-y-2 mt-2 mb-3 rounded-xl border border-rule bg-surface-2/50 p-3">
                                    <div className="flex items-center justify-between gap-3">
                                        <div>
                                            <p className="text-sm font-semibold text-ink-1">Minh chứng</p>
                                        </div>
                                        <a href={payment.proof_url} target="_blank" rel="noopener noreferrer" className="inline-flex items-center gap-1 rounded-full border border-rule px-3 py-1 text-xs text-ink-2 hover:border-warm-400 hover:text-warm-400 transition-colors">
                                            <ExternalLink className="w-3.5 h-3.5" />
                                            Mở ảnh
                                        </a>
                                    </div>
                                </div>

                                {payment.status === "PENDING" && (
                                    <div className="flex justify-end gap-2 pt-2 mt-2 border-t border-rule/50 animate-fade-in">
                                        {isAdminOrOwner ? (
                                            <>
                                                <button
                                                    onClick={() => handleReject(payment.id)}
                                                    className="px-3 py-1.5 rounded-lg text-xs font-semibold border border-ink-red-border text-ink-red-text hover:bg-ink-red-fill transition-colors flex items-center gap-1.5"
                                                    disabled={processingId !== null}
                                                >
                                                    {processingId === payment.id && processingAction === "reject" ? (
                                                        <>
                                                            <div className="w-3.5 h-3.5 border-2 border-ink-red-text/30 border-t-ink-red-text rounded-full animate-spin" />
                                                            <span>Đang từ chối...</span>
                                                        </>
                                                    ) : (
                                                        "Từ chối"
                                                    )}
                                                </button>
                                                <button
                                                    onClick={() => handleApprove(payment.id)}
                                                    className="px-3 py-1.5 rounded-lg text-xs font-semibold bg-ink-green-fill border border-ink-green-border text-ink-green-text hover:bg-opacity-80 transition-colors flex items-center gap-1.5"
                                                    disabled={processingId !== null}
                                                >
                                                    {processingId === payment.id && processingAction === "approve" ? (
                                                        <>
                                                            <div className="w-3.5 h-3.5 border-2 border-ink-green-text/30 border-t-ink-green-text rounded-full animate-spin" />
                                                            <span>Đang duyệt...</span>
                                                        </>
                                                    ) : (
                                                        "Phê duyệt"
                                                    )}
                                                </button>
                                            </>
                                        ) : (
                                            <button
                                                onClick={() => handleCancel(payment.id)}
                                                className="px-3 py-1.5 rounded-lg text-xs font-semibold border border-rule text-ink-2 hover:bg-surface-2 transition-colors flex items-center gap-1.5"
                                                disabled={processingId !== null}
                                            >
                                                {processingId === payment.id && processingAction === "cancel" ? (
                                                    <>
                                                        <div className="w-3.5 h-3.5 border-2 border-ink-2/30 border-t-ink-2 rounded-full animate-spin" />
                                                        <span>Đang hủy...</span>
                                                    </>
                                                ) : (
                                                    "Hủy nộp minh chứng"
                                                )}
                                            </button>
                                        )}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </Modal>
    );
};
