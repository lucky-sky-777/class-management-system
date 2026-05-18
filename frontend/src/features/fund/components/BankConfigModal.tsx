import React, { useState, useEffect } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { 
    Building2, 
    CreditCard, 
    User, 
    Save
} from "lucide-react";
import { fundAPI } from "../api";
import type { VietQrBankResponseDto, BankConfig } from "../types";

interface BankConfigModalProps {
    isOpen: boolean;
    onClose: () => void;
    initialConfig: BankConfig | null;
    onSave: (config: BankConfig) => void;
}

export const BankConfigModal: React.FC<BankConfigModalProps> = ({ isOpen, onClose, initialConfig, onSave }) => {
    const [banks, setBanks] = useState<VietQrBankResponseDto[]>([]);
    const [selectedBankBin, setSelectedBankBin] = useState("");
    const [accountNumber, setAccountNumber] = useState("");
    const [accountName, setAccountName] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (isOpen) {
            fetchBanks();
            if (initialConfig) {
                setSelectedBankBin(initialConfig.bank_code);
                setAccountNumber(initialConfig.account_number);
                setAccountName(initialConfig.account_name);
            } else {
                setAccountNumber("");
                setAccountName("");
            }
        }
    }, [isOpen, initialConfig]);

    const fetchBanks = async () => {
        setIsLoading(true);
        try {
            const res = await fundAPI.getBanks();
            if (res.success) {
                setBanks(res.data);
                if (res.data.length > 0 && !initialConfig) {
                    setSelectedBankBin(res.data[0].bin);
                }
            }
        } catch (err) {
            console.error("Lỗi tải danh sách ngân hàng", err);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSave = (e: React.FormEvent) => {
        e.preventDefault();
        if (!selectedBankBin || !accountNumber) return;

        onSave({
            bank_code: selectedBankBin,
            account_number: accountNumber,
            account_name: accountName
        });
        onClose();
    };

    return (
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title={
                <div className="flex items-center gap-2">
                    <Building2 className="w-5 h-5 text-warm-400" />
                    <span>Cài đặt Tài khoản Quỹ chung</span>
                </div>
            }
        >
            <form onSubmit={handleSave} className="space-y-4">
                <p className="text-sm text-ink-3 mb-4">
                    Tài khoản này sẽ được sử dụng làm thông tin nhận tiền mặc định cho các khoản thu của lớp.
                </p>

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
                            disabled={isLoading}
                        >
                            {isLoading && banks.length === 0 ? (
                                <option>Đang tải danh sách ngân hàng...</option>
                            ) : (
                                banks.map(b => (
                                    <option key={b.id} value={b.bin}>{b.shortName || b.short_name} - {b.name}</option>
                                ))
                            )}
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

                <div className="pt-4 flex gap-3">
                    <button type="button" onClick={onClose} className="btn btn-secondary flex-1">
                        Hủy bỏ
                    </button>
                    <button type="submit" className="btn btn-primary flex-1 shadow-md shadow-warm-400/20" disabled={isLoading || !selectedBankBin || !accountNumber}>
                        <Save className="w-4 h-4 mr-2" />
                        Lưu cấu hình
                    </button>
                </div>
            </form>
        </Modal>
    );
};
