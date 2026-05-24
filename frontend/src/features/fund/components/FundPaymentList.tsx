import React, { useEffect, useMemo } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { CheckCircle, XCircle, Clock, ExternalLink, ShieldCheck } from "lucide-react";
import type { ID } from "@shared/utils/common";
import { useFundPayment } from "../hooks/useFund";
import type { FundPaymentStatus } from "../types";

interface FundPaymentListProps {
    isOpen: boolean;
    onClose: () => void;
    fundId: ID;
    classId: ID;
    fundTitle: string;
    fundDescription: string;
}

export const FundPaymentList: React.FC<FundPaymentListProps> = ({
    isOpen, onClose, fundId, classId, fundTitle, fundDescription
}) => {
    const { payments, fetchPayments, isLoading } = useFundPayment(classId);
    
    useEffect(() => {
        if (isOpen && fundId) {
            fetchPayments(fundId);
        }
    }, [isOpen, fundId, fetchPayments]);

    const fundPayments = useMemo(
        () => (payments[fundId] || []).slice().sort((a, b) => new Date(b.created_at).getTime() - new Date(a.created_at).getTime()),
        [payments, fundId]
    );

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
                                            <p className="text-xs text-ink-3">Nhấn vào ảnh để xem đầy đủ</p>
                                        </div>
                                        <a href={payment.proof_url} target="_blank" rel="noopener noreferrer" className="inline-flex items-center gap-1 rounded-full border border-rule px-3 py-1 text-xs text-ink-2 hover:border-warm-400 hover:text-warm-400 transition-colors">
                                            <ExternalLink className="w-3.5 h-3.5" />
                                            Mở ảnh
                                        </a>
                                    </div>

                                    <div className="relative group overflow-hidden rounded-2xl border border-rule bg-white shadow-sm">
                                        <img
                                            src={payment.proof_url}
                                            alt={`Minh chứng của ${payment.creator_display_name}`}
                                            loading="lazy"
                                            className="w-full min-h-[180px] max-h-[260px] object-contain bg-surface-3"
                                            onError={(e) => {
                                                (e.target as HTMLImageElement).src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="240" height="180"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="14" fill="%23999">Ảnh không tải được</text></svg>';
                                            }}
                                        />
                                        <div className="absolute inset-0 bg-gradient-to-t from-ink-900/30 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </Modal>
    );
};
