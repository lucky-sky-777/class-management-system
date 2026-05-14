import React, { useState } from "react";
import type { CreateFundRequestDto } from "../types";

interface FundFormModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (data: CreateFundRequestDto) => Promise<boolean>;
}

export const FundFormModal: React.FC<FundFormModalProps> = ({ isOpen, onClose, onSubmit }) => {
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [amount, setAmount] = useState<number | "">("");
    const [type, setType] = useState<"INCOME" | "EXPENSE">("INCOME");
    const [isSubmitting, setIsSubmitting] = useState(false);

    if (!isOpen) return null;

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!title || !amount) return;

        setIsSubmitting(true);
        const success = await onSubmit({
            title,
            description,
            amount: Number(amount),
            type
        });
        setIsSubmitting(false);

        if (success) {
            setTitle("");
            setDescription("");
            setAmount("");
            setType("INCOME");
            onClose();
        }
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink-1/50 animate-fade-in backdrop-blur-sm">
            <div className="bg-paper rounded-2xl p-6 w-full max-w-md shadow-xl animate-slide-up">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="font-serif text-2xl text-ink-1 font-semibold">
                        Thêm Giao Dịch
                    </h3>
                    <button 
                        onClick={onClose}
                        className="text-ink-3 hover:text-ink-1 transition-colors"
                        disabled={isSubmitting}
                    >
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-ink-2 mb-1">Loại giao dịch</label>
                        <select 
                            value={type}
                            onChange={(e) => setType(e.target.value as "INCOME" | "EXPENSE")}
                            className="input-field"
                            disabled={isSubmitting}
                        >
                            <option value="INCOME">Thu (Income)</option>
                            <option value="EXPENSE">Chi (Expense)</option>
                        </select>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-ink-2 mb-1">Tiêu đề *</label>
                        <input 
                            type="text" 
                            className="input-field"
                            value={title}
                            onChange={(e) => setTitle(e.target.value)}
                            placeholder="Ví dụ: Thu quỹ tháng 5"
                            required
                            disabled={isSubmitting}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-ink-2 mb-1">Số tiền (VNĐ) *</label>
                        <input 
                            type="number" 
                            className="input-field"
                            value={amount}
                            onChange={(e) => setAmount(Number(e.target.value))}
                            placeholder="Nhập số tiền..."
                            min="1000"
                            required
                            disabled={isSubmitting}
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-ink-2 mb-1">Mô tả (Tùy chọn)</label>
                        <textarea 
                            className="input-field min-h-[80px]"
                            value={description}
                            onChange={(e) => setDescription(e.target.value)}
                            placeholder="Ghi chú thêm về khoản thu/chi này..."
                            disabled={isSubmitting}
                        />
                    </div>

                    <div className="flex gap-3 pt-4 border-t border-rule mt-6">
                        <button 
                            type="button" 
                            onClick={onClose}
                            className="btn btn-secondary flex-1"
                            disabled={isSubmitting}
                        >
                            Hủy
                        </button>
                        <button 
                            type="submit" 
                            className={`btn ${type === "INCOME" ? "btn-primary" : "btn-warm"} flex-1`}
                            disabled={isSubmitting || !title || !amount}
                        >
                            {isSubmitting ? "Đang xử lý..." : "Lưu giao dịch"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};
