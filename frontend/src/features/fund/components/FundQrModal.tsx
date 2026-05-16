import React, { useState, useEffect } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { 
    QrCode, 
    Building2, 
    CreditCard, 
    User, 
    CircleDollarSign, 
    MessageSquare,
    Smartphone,
    Download,
    ExternalLink
} from "lucide-react";
import { fundAPI } from "../api";
import type { VietQrBankResponseDto, BankConfig } from "../types";

interface FundQrModalProps {
    isOpen: boolean;
    onClose: () => void;
    initialConfig?: BankConfig | null;
}

export const FundQrModal: React.FC<FundQrModalProps> = ({ isOpen, onClose, initialConfig }) => {
    const [banks, setBanks] = useState<VietQrBankResponseDto[]>([]);
    const [selectedBankBin, setSelectedBankBin] = useState("");
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

    useEffect(() => {
        if (initialConfig && isOpen) {
            setSelectedBankBin(initialConfig.bank_code);
            setAccountNumber(initialConfig.account_number);
            setAccountName(initialConfig.account_name);
        }
    }, [initialConfig, isOpen, banks]);

    const fetchBanks = async () => {
        try {
            const res = await fundAPI.getBanks();
            if (res.success) {
                setBanks(res.data);
                if (res.data.length > 0 && (!initialConfig || !initialConfig.bank_code)) {
                    setSelectedBankBin(res.data[0].bin);
                }
            }
        } catch (err) {
            console.error("Lỗi tải danh sách ngân hàng", err);
        }
    };

    const handleGenerate = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedBankBin || !accountNumber) return;

        setIsLoading(true);
        setError(null);
        try {
            const res = await fundAPI.getQrCodeUrl({
                bank_code: selectedBankBin,
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

    return (
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title={
                <div className="flex items-center gap-2">
                    <QrCode className="w-5 h-5 text-warm-400" />
                    <span>Tạo Mã QR Nhận Tiền</span>
                </div>
            }
        >
            <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <form onSubmit={handleGenerate} className="space-y-4">
                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <Building2 className="w-3 h-3" />
                            Ngân hàng thụ hưởng *
                        </label>
                        <div className="input-field">
                            <select 
                                value={selectedBankBin}
                                onChange={(e) => setSelectedBankBin(e.target.value)}
                                className="w-full bg-transparent outline-none py-2 text-sm"
                                required
                            >
                                {banks.map(b => (
                                    <option key={b.id} value={b.bin}>{b.shortName || b.short_name} - {b.name}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <CreditCard className="w-3 h-3" />
                            Số tài khoản *
                        </label>
                        <div className="input-field">
                            <input 
                                type="text" 
                                value={accountNumber}
                                onChange={(e) => setAccountNumber(e.target.value)}
                                placeholder="Nhập số tài khoản..."
                                required
                            />
                        </div>
                    </div>

                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <User className="w-3 h-3" />
                            Tên chủ tài khoản
                        </label>
                        <div className="input-field">
                            <input 
                                type="text" 
                                value={accountName}
                                onChange={(e) => setAccountName(e.target.value.toUpperCase())}
                                placeholder="VÍ DỤ: NGUYEN VAN A"
                            />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="input-label mb-1.5 flex items-center gap-1.5">
                                <CircleDollarSign className="w-3 h-3" />
                                Số tiền
                            </label>
                            <div className="input-field">
                                <input 
                                    type="number" 
                                    value={amount}
                                    onChange={(e) => setAmount(e.target.value ? Number(e.target.value) : "")}
                                    placeholder="0"
                                />
                            </div>
                        </div>
                        <div>
                            <label className="input-label mb-1.5 flex items-center gap-1.5">
                                <MessageSquare className="w-3 h-3" />
                                Lời nhắn
                            </label>
                            <div className="input-field">
                                <input 
                                    type="text" 
                                    value={notes}
                                    onChange={(e) => setNotes(e.target.value)}
                                    placeholder="Nội dung..."
                                />
                            </div>
                        </div>
                    </div>

                    {error && (
                        <div className="p-3 bg-red-50 text-red-600 rounded-lg text-xs font-medium border border-red-100 animate-fade-in">
                            {error}
                        </div>
                    )}

                    <button 
                        type="submit" 
                        className="btn btn-primary w-full py-3 shadow-md shadow-warm-400/20"
                        disabled={isLoading || !selectedBankBin || !accountNumber}
                    >
                        {isLoading ? (
                            <div className="flex items-center gap-2">
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                                <span>Đang tạo mã...</span>
                            </div>
                        ) : (
                            <div className="flex items-center gap-2">
                                <QrCode className="w-4 h-4" />
                                <span>Tạo Mã QR Ngay</span>
                            </div>
                        )}
                    </button>
                </form>

                <div className="flex flex-col items-center justify-center p-6 border-2 border-dashed border-rule rounded-2xl bg-surface-2/50 relative overflow-hidden min-h-[350px]">
                    {qrUrl ? (
                        <div className="text-center animate-in zoom-in duration-500 flex flex-col items-center">
                            <div className="relative group p-4 bg-white rounded-2xl shadow-xl border border-rule transition-transform hover:scale-[1.02]">
                                <img src={qrUrl} alt="QR Code" className="w-full max-w-[220px] h-auto mx-auto rounded-lg" />
                                <div className="absolute inset-0 bg-ink-1/40 opacity-0 group-hover:opacity-100 transition-opacity rounded-2xl flex items-center justify-center gap-3 backdrop-blur-[2px]">
                                    <button 
                                        onClick={() => window.open(qrUrl, '_blank')}
                                        className="p-2 bg-white rounded-full text-ink-1 hover:text-warm-400 shadow-lg transition-all"
                                        title="Mở ảnh lớn"
                                    >
                                        <ExternalLink className="w-5 h-5" />
                                    </button>
                                </div>
                            </div>
                            
                            <p className="text-sm font-semibold text-ink-1 mt-6 flex items-center gap-2">
                                <Smartphone className="w-4 h-4 text-warm-400" />
                                Quét để thanh toán
                            </p>
                            <p className="text-xs text-ink-3 mt-1 max-w-[200px]">
                                Đưa mã này cho thành viên để quét nhanh trên các app ngân hàng
                            </p>
                            
                            <button 
                                onClick={() => {
                                    const link = document.createElement('a');
                                    link.href = qrUrl;
                                    link.download = `QR_Quy_Lop_${accountNumber}.png`;
                                    link.click();
                                }}
                                className="mt-4 flex items-center gap-1.5 text-xs font-bold text-warm-400 hover:text-warm-600 transition-colors py-2 px-4 rounded-full bg-warm-fill hover:bg-warm-border"
                            >
                                <Download className="w-3.5 h-3.5" />
                                Tải mã QR về máy
                            </button>
                        </div>
                    ) : (
                        <div className="text-center p-8">
                            <div className="w-20 h-20 bg-white rounded-3xl flex items-center justify-center mx-auto mb-6 shadow-sm border border-rule">
                                <Smartphone className="w-10 h-10 text-ink-4" />
                            </div>
                            <h4 className="text-ink-2 font-bold mb-2">Xem trước mã QR</h4>
                            <p className="text-xs text-ink-3 max-w-[180px] mx-auto leading-relaxed">
                                Nhập đầy đủ thông tin bên trái để tạo mã QR thanh toán chuyên nghiệp
                            </p>
                            
                            {/* Decorative dots for placeholder */}
                            <div className="absolute bottom-4 left-4 right-4 flex justify-between opacity-10">
                                <div className="w-12 h-12 border-t-2 border-l-2 border-ink-1 rounded-tl-lg"></div>
                                <div className="w-12 h-12 border-t-2 border-r-2 border-ink-1 rounded-tr-lg"></div>
                            </div>
                            <div className="absolute top-4 left-4 right-4 flex justify-between opacity-10">
                                <div className="w-12 h-12 border-b-2 border-l-2 border-ink-1 rounded-bl-lg"></div>
                                <div className="w-12 h-12 border-b-2 border-r-2 border-ink-1 rounded-br-lg"></div>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </Modal>
    );
};
