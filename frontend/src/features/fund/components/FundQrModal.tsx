import React, { useState, useEffect } from "react";
import { fundAPI } from "../api";
import type { VietQrBankResponseDto } from "../types";

interface FundQrModalProps {
    isOpen: boolean;
    onClose: () => void;
}

export const FundQrModal: React.FC<FundQrModalProps> = ({ isOpen, onClose }) => {
    const [banks, setBanks] = useState<VietQrBankResponseDto[]>([]);
    const [selectedBankCode, setSelectedBankCode] = useState("");
    const [accountNumber, setAccountNumber] = useState("");
    const [accountName, setAccountName] = useState("");
    const [amount, setAmount] = useState<number | "">("");
    const [notes, setNotes] = useState("");
    
    const [qrUrl, setQrUrl] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (isOpen && banks.length === 0) {
            fetchBanks();
        }
    }, [isOpen]);

    const fetchBanks = async () => {
        try {
            const res = await fundAPI.getBanks();
            if (res.success) {
                setBanks(res.data);
                if (res.data.length > 0) {
                    setSelectedBankCode(res.data[0].code);
                }
            }
        } catch (err) {
            console.error("Lỗi tải danh sách ngân hàng", err);
        }
    };

    const handleGenerate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedBankCode || !accountNumber) return;

        setIsLoading(true);
        setError(null);
        try {
            const res = await fundAPI.getQrCodeUrl({
                bank_code: selectedBankCode,
                account_number: accountNumber,
                account_name: accountName,
                amount: amount ? Number(amount) : 0,
                notes: notes
            });
            if (res.success) {
                setQrUrl(res.data.qr_code_url);
            } else {
                setError(res.message);
            }
        } catch (err) {
            setError("Lỗi khi tạo mã QR");
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink-1/50 animate-fade-in backdrop-blur-sm">
            <div className="bg-paper rounded-2xl p-6 w-full max-w-lg shadow-xl max-h-[90vh] overflow-y-auto animate-slide-up">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="font-serif text-2xl text-ink-1 font-semibold">
                        Tạo Mã QR Nhận Tiền
                    </h3>
                    <button onClick={onClose} className="text-ink-3 hover:text-ink-1">✕</button>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <form onSubmit={handleGenerate} className="space-y-4">
                        <div>
                            <label className="block text-sm font-medium text-ink-2 mb-1">Ngân hàng *</label>
                            <select 
                                value={selectedBankCode}
                                onChange={(e) => setSelectedBankCode(e.target.value)}
                                className="input-field"
                                required
                            >
                                {banks.map(b => (
                                    <option key={b.id} value={b.code}>{b.shortName} - {b.name}</option>
                                ))}
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-ink-2 mb-1">Số tài khoản *</label>
                            <input 
                                type="text" 
                                className="input-field"
                                value={accountNumber}
                                onChange={(e) => setAccountNumber(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-ink-2 mb-1">Tên chủ tài khoản</label>
                            <input 
                                type="text" 
                                className="input-field"
                                value={accountName}
                                onChange={(e) => setAccountName(e.target.value)}
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-ink-2 mb-1">Số tiền (VNĐ)</label>
                            <input 
                                type="number" 
                                className="input-field"
                                value={amount}
                                onChange={(e) => setAmount(Number(e.target.value))}
                                placeholder="Để trống nếu cho phép tự nhập"
                            />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-ink-2 mb-1">Lời nhắn</label>
                            <input 
                                type="text" 
                                className="input-field"
                                value={notes}
                                onChange={(e) => setNotes(e.target.value)}
                                placeholder="Nội dung CK..."
                            />
                        </div>

                        {error && <p className="text-sm text-red-500">{error}</p>}

                        <button 
                            type="submit" 
                            className="btn btn-primary w-full"
                            disabled={isLoading || !selectedBankCode || !accountNumber}
                        >
                            {isLoading ? "Đang tạo..." : "Tạo Mã QR"}
                        </button>
                    </form>

                    <div className="flex flex-col items-center justify-center p-4 border border-rule rounded-xl bg-surface-1">
                        {qrUrl ? (
                            <div className="text-center">
                                <img src={qrUrl} alt="QR Code" className="w-full max-w-[200px] h-auto mx-auto border rounded-lg bg-white p-2" />
                                <p className="text-xs text-ink-3 mt-3">Đưa mã này cho thành viên để quét thanh toán</p>
                            </div>
                        ) : (
                            <div className="text-ink-3 text-sm text-center">
                                <span className="block text-3xl mb-2 opacity-50">📱</span>
                                Nhập thông tin và bấm Tạo Mã QR
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};
