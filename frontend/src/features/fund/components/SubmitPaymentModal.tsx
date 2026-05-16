import React, { useState } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { Upload, Link as LinkIcon, Image as ImageIcon } from "lucide-react";
import type { ID } from "@shared/utils/common";
import { useFundPayment } from "../hooks/useFund";

interface SubmitPaymentModalProps {
    isOpen: boolean;
    onClose: () => void;
    fundId: ID;
    classId: ID;
    fundTitle: string;
    fundAmount: number;
    qrCodeUrl?: string;
}

export const SubmitPaymentModal: React.FC<SubmitPaymentModalProps> = ({
    isOpen, onClose, fundId, classId, fundTitle, fundAmount, qrCodeUrl
}) => {
    const [proofUrl, setProofUrl] = useState("");
    const { submitPayment, isLoading } = useFundPayment(classId);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!proofUrl.trim()) return;

        const success = await submitPayment(fundId, { proof_url: proofUrl });
        if (success) {
            setProofUrl("");
            onClose();
        }
    };

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={
                <div className="flex items-center gap-2">
                    <Upload className="w-5 h-5 text-warm-400" />
                    <span>Nộp Minh Chứng Thanh Toán</span>
                </div>
            }
        >
            <form onSubmit={handleSubmit} className="space-y-4">
                <div className="p-3 bg-surface-2 rounded-xl mb-4">
                    <p className="text-sm font-medium text-ink-2">Khoản thu:</p>
                    <p className="text-base font-bold text-ink-1">{fundTitle}</p>
                    <p className="text-sm font-semibold text-warm-500 mt-1">{fundAmount.toLocaleString('vi-VN')} đ</p>
                </div>

                {qrCodeUrl && (
                    <div className="flex flex-col items-center justify-center p-4 border border-rule rounded-xl bg-white mb-4">
                        <p className="text-xs font-bold text-ink-2 mb-2">QUÉT MÃ ĐỂ THANH TOÁN</p>
                        <img src={qrCodeUrl} alt="Payment QR Code" className="w-48 h-auto rounded-lg shadow-sm" />
                        <p className="text-[10px] text-ink-3 mt-2 text-center max-w-[200px]">
                            Vui lòng kiểm tra lại số tiền trước khi chuyển.
                        </p>
                    </div>
                )}

                <div>
                    <label className="input-label mb-1.5 flex items-center gap-1.5">
                        <LinkIcon className="w-3 h-3" />
                        Đường dẫn ảnh minh chứng (Link Ảnh) *
                    </label>
                    <div className="input-field">
                        <input
                            type="url"
                            value={proofUrl}
                            onChange={(e) => setProofUrl(e.target.value)}
                            placeholder="https://example.com/image.jpg"
                            required
                            disabled={isLoading}
                        />
                    </div>
                    <p className="text-xs text-ink-3 mt-2">
                        Ghi chú: Vui lòng dán link ảnh màn hình chuyển khoản thành công của bạn.
                    </p>
                </div>

                {proofUrl && (
                    <div className="mt-4 border border-rule rounded-xl overflow-hidden p-2 bg-surface-2/50">
                        <p className="text-xs font-semibold text-ink-2 mb-2 flex items-center gap-1">
                            <ImageIcon className="w-3 h-3" /> Xem trước ảnh:
                        </p>
                        <div className="w-full flex justify-center">
                            <img
                                src={proofUrl}
                                alt="Proof Preview"
                                className="max-h-48 object-contain rounded-lg"
                                onError={(e) => {
                                    (e.target as HTMLImageElement).src = 'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="100" height="100"><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" font-family="sans-serif" font-size="12" fill="%23999">Lỗi tải ảnh</text></svg>';
                                }}
                            />
                        </div>
                    </div>
                )}

                <div className="pt-4 flex gap-3">
                    <button type="button" onClick={onClose} className="btn btn-secondary flex-1" disabled={isLoading}>
                        Hủy bỏ
                    </button>
                    <button type="submit" className="btn btn-primary flex-1 shadow-md shadow-warm-400/20" disabled={isLoading || !proofUrl.trim()}>
                        {isLoading ? "Đang gửi..." : "Gửi Minh Chứng"}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
