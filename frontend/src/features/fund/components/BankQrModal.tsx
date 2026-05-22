import React, { useEffect, useState } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { QrCode, Building2, CreditCard, User, Download, ExternalLink } from "lucide-react";
import { fundAPI } from "../api";
import type { BankConfig } from "../types";

interface BankQrModalProps {
    isOpen: boolean;
    onClose: () => void;
    bankConfig: BankConfig | null;
}

export const BankQrModal: React.FC<BankQrModalProps> = ({ isOpen, onClose, bankConfig }) => {
    const [qrUrl, setQrUrl] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!isOpen) return;
        if (!bankConfig) {
            setError("Chưa có cấu hình tài khoản ngân hàng lớp.");
            return;
        }

        const fetchQr = async () => {
            setIsLoading(true);
            setError(null);

            try {
                const res = await fundAPI.getQrCodeUrl({
                    bank_code: bankConfig.bank_code,
                    account_number: bankConfig.account_number,
                    account_name: bankConfig.account_name,
                    amount: 0,
                    notes: "Thanh toán quỹ lớp"
                });

                if (res.success) {
                    setQrUrl(res.data.qr_code_url);
                } else {
                    setError(res.message || "Không thể tải mã QR");
                }
            } catch (err) {
                console.error("Failed to load bank QR code:", err);
                setError("Lỗi khi tải mã QR");
            } finally {
                setIsLoading(false);
            }
        };

        fetchQr();
    }, [isOpen, bankConfig]);

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={
                <div className="flex items-center gap-2">
                    <QrCode className="w-5 h-5 text-warm-400" />
                    <span>Mã QR thanh toán chung của lớp</span>
                </div>
            }
        >
            <div className="space-y-4">
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="bg-surface-2 rounded-2xl p-4 border border-rule">
                        <div className="text-xs text-ink-3 uppercase tracking-[0.2em] mb-3">Ngân hàng</div>
                        <div className="text-sm font-semibold text-ink-1 flex items-center gap-2">
                            <Building2 className="w-4 h-4" />
                            {bankConfig?.bank_code || "-"}
                        </div>
                        <div className="text-xs text-ink-3 mt-4">Số tài khoản</div>
                        <div className="text-sm font-semibold text-ink-1 flex items-center gap-2">
                            <CreditCard className="w-4 h-4" />
                            {bankConfig?.account_number || "-"}
                        </div>
                        <div className="text-xs text-ink-3 mt-4">Tên chủ tài khoản</div>
                        <div className="text-sm font-semibold text-ink-1 flex items-center gap-2">
                            <User className="w-4 h-4" />
                            {bankConfig?.account_name || "-"}
                        </div>
                    </div>

                    <div className="flex flex-col items-center justify-center p-4 rounded-2xl border border-dashed border-rule bg-white min-h-[220px]">
                        {isLoading ? (
                            <div className="w-28 h-28 flex items-center justify-center rounded-2xl bg-surface-2">
                                <div className="w-8 h-8 border-2 border-warm-400/20 border-t-warm-400 rounded-full animate-spin"></div>
                            </div>
                        ) : error ? (
                            <div className="text-sm text-red-600">{error}</div>
                        ) : qrUrl ? (
                            <div className="text-center space-y-3">
                                <div className="relative group p-4 bg-white rounded-3xl shadow-sm transition-transform hover:scale-[1.02]">
                                    <img src={qrUrl} alt="Bank QR Code" className="w-48 h-48 mx-auto rounded-xl" />
                                    <div className="absolute inset-0 bg-ink-1/30 opacity-0 group-hover:opacity-100 transition-opacity rounded-3xl flex items-center justify-center">
                                        <button
                                            type="button"
                                            onClick={() => window.open(qrUrl, "_blank")}
                                            className="p-2 bg-white rounded-full shadow"
                                            title="Mở ảnh QR"
                                        >
                                            <ExternalLink className="w-4 h-4" />
                                        </button>
                                    </div>
                                </div>
                                <div className="text-xs text-ink-3">Quét mã này để thanh toán trực tiếp cho quỹ lớp.</div>
                            </div>
                        ) : (
                            <div className="text-sm text-ink-3">Chưa có mã QR.</div>
                        )}
                    </div>
                </div>

                {qrUrl && (
                    <div className="flex flex-wrap gap-3 justify-center">
                        <button
                            type="button"
                            onClick={() => {
                                const link = document.createElement("a");
                                link.href = qrUrl;
                                link.download = `QR_Quy_Lop_${bankConfig?.account_number || ""}.png`;
                                link.click();
                            }}
                            className="btn btn-secondary min-w-[180px]"
                        >
                            <Download className="w-4 h-4 mr-2" />
                            Tải mã QR về máy
                        </button>
                    </div>
                )}
            </div>
        </Modal>
    );
};
