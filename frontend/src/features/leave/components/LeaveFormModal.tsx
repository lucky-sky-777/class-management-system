import React, { useState, useEffect } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { 
    Calendar, 
    FileText, 
    Link as LinkIcon, 
    AlertCircle,
    CheckCircle2
} from "lucide-react";

interface LeaveFormModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: { reason: string; from: string; to: string; proof_url?: string }) => Promise<{ success: boolean; message?: string }>;
    isSubmitting: boolean;
}

export const LeaveFormModal: React.FC<LeaveFormModalProps> = ({ isOpen, onClose, onSubmit, isSubmitting }) => {
    const [reason, setReason] = useState("");
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");
    const [proofUrl, setProofUrl] = useState("");
    const [submitError, setSubmitError] = useState<string | null>(null);

    useEffect(() => {
        if (!isOpen) {
            setSubmitError(null);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    const handleFormSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitError(null);
        const result = await onSubmit({ 
            reason, 
            from: fromDate, 
            to: toDate, 
            proof_url: proofUrl || undefined 
        });
        if (result.success) {
            setReason("");
            setFromDate("");
            setToDate("");
            setProofUrl("");
            onClose();
        } else {
            setSubmitError(result.message || "Đã có lỗi xảy ra");
        }
    };

    return (
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title={
                <div className="flex items-center gap-2">
                    <FileText className="w-5 h-5 text-warm-400" />
                    <span>Tạo Đơn Xin Nghỉ Phép</span>
                </div>
            }
        >
            <form onSubmit={handleFormSubmit} className="space-y-6">
                {submitError && (
                    <div className="bg-red-50 border border-red-200 rounded-xl p-4 flex gap-3 animate-fade-in">
                        <AlertCircle className="w-5 h-5 text-red-600 shrink-0" />
                        <p className="text-sm text-red-800 leading-relaxed font-medium">
                            {submitError}
                        </p>
                    </div>
                )}
                <div className="bg-amber-50 border border-amber-200 rounded-xl p-4 flex gap-3">
                    <AlertCircle className="w-5 h-5 text-amber-600 shrink-0" />
                    <p className="text-xs text-amber-800 leading-relaxed">
                        Đơn xin nghỉ phép sau khi được phê duyệt sẽ tự động cập nhật vào hệ thống điểm danh của lớp. Vui lòng cung cấp lý do chính xác.
                    </p>
                </div>

                <div className="space-y-4">
                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <FileText className="w-3 h-3" />
                            Lý do nghỉ phép *
                        </label>
                        <div className="input-field items-start py-2">
                            <textarea
                                required
                                value={reason}
                                onChange={(e) => setReason(e.target.value)}
                                placeholder="Nhập lý do chi tiết (ví dụ: bị sốt, việc gia đình hiếu hỉ...)"
                                className="w-full resize-none min-h-[100px] leading-relaxed"
                                disabled={isSubmitting}
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="input-label mb-1.5 flex items-center gap-1.5">
                                <Calendar className="w-3 h-3" />
                                Từ ngày *
                            </label>
                            <div className="input-field">
                                <input
                                    type="date"
                                    required
                                    value={fromDate}
                                    onChange={(e) => setFromDate(e.target.value)}
                                    disabled={isSubmitting}
                                    min={new Date().toISOString().split('T')[0]}
                                />
                            </div>
                        </div>
                        <div>
                            <label className="input-label mb-1.5 flex items-center gap-1.5">
                                <Calendar className="w-3 h-3" />
                                Đến ngày *
                            </label>
                            <div className="input-field">
                                <input
                                    type="date"
                                    required
                                    value={toDate}
                                    onChange={(e) => setToDate(e.target.value)}
                                    disabled={isSubmitting}
                                    min={fromDate || new Date().toISOString().split('T')[0]}
                                />
                            </div>
                        </div>
                    </div>

                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <LinkIcon className="w-3 h-3" />
                            Link minh chứng (nếu có)
                        </label>
                        <div className="input-field">
                            <input
                                type="url"
                                value={proofUrl}
                                onChange={(e) => setProofUrl(e.target.value)}
                                placeholder="https://drive.google.com/share-link..."
                                disabled={isSubmitting}
                            />
                        </div>
                        <p className="text-[10px] text-ink-3 mt-1.5 italic px-1">
                            * Ví dụ: Link ảnh giấy khám bệnh, giấy mời, hoặc minh chứng liên quan.
                        </p>
                    </div>
                </div>

                <div className="flex gap-3 pt-2">
                    <button
                        type="button"
                        onClick={onClose}
                        className="btn btn-secondary flex-1"
                        disabled={isSubmitting}
                    >
                        Hủy bỏ
                    </button>
                    <button
                        type="submit"
                        disabled={isSubmitting || !reason || !fromDate || !toDate}
                        className="btn btn-primary flex-1 shadow-lg shadow-warm-400/20"
                    >
                        {isSubmitting ? (
                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                <span>Đang gửi...</span>
                            </div>
                        ) : (
                            <div className="flex items-center gap-2">
                                <CheckCircle2 className="w-4 h-4" />
                                <span>Gửi đơn xin nghỉ</span>
                            </div>
                        )}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
