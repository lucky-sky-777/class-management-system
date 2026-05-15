import React, { useState } from "react";
import { Modal } from "@shared/components/ui/Modal";
import { 
    Tag, 
    Type, 
    CircleDollarSign, 
    AlignLeft, 
    ArrowUpCircle, 
    ArrowDownCircle 
} from "lucide-react";
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
        <Modal 
            isOpen={isOpen} 
            onClose={onClose} 
            title={
                <div className="flex items-center gap-2">
                    <CircleDollarSign className="w-5 h-5 text-warm-400" />
                    <span>Thêm Giao Dịch Mới</span>
                </div>
            }
        >
            <form onSubmit={handleSubmit} className="space-y-5">
                <div className="grid grid-cols-2 gap-3 p-1 bg-surface-2 rounded-xl">
                    <button
                        type="button"
                        onClick={() => setType("INCOME")}
                        className={`flex items-center justify-center gap-2 py-2 rounded-lg text-sm font-semibold transition-all ${
                            type === "INCOME" 
                                ? "bg-white text-ink-green-text shadow-sm" 
                                : "text-ink-3 hover:text-ink-2"
                        }`}
                    >
                        <ArrowDownCircle className="w-4 h-4" />
                        Khoản Thu
                    </button>
                    <button
                        type="button"
                        onClick={() => setType("EXPENSE")}
                        className={`flex items-center justify-center gap-2 py-2 rounded-lg text-sm font-semibold transition-all ${
                            type === "EXPENSE" 
                                ? "bg-white text-warm-text shadow-sm" 
                                : "text-ink-3 hover:text-ink-2"
                        }`}
                    >
                        <ArrowUpCircle className="w-4 h-4" />
                        Khoản Chi
                    </button>
                </div>

                <div className="space-y-4">
                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <Tag className="w-3 h-3" />
                            Tiêu đề giao dịch *
                        </label>
                        <div className="input-field">
                            <input 
                                type="text" 
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                placeholder="Ví dụ: Thu quỹ tháng 5, Mua phấn viết..."
                                required
                                disabled={isSubmitting}
                            />
                        </div>
                    </div>

                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <CircleDollarSign className="w-3 h-3" />
                            Số tiền (VNĐ) *
                        </label>
                        <div className="input-field">
                            <input 
                                type="number" 
                                value={amount}
                                onChange={(e) => setAmount(e.target.value ? Number(e.target.value) : "")}
                                placeholder="Nhập số tiền..."
                                min="1000"
                                required
                                disabled={isSubmitting}
                            />
                        </div>
                        <p className="text-[10px] text-ink-3 mt-1 ml-1 italic">Tối thiểu 1.000đ</p>
                    </div>

                    <div>
                        <label className="input-label mb-1.5 flex items-center gap-1.5">
                            <AlignLeft className="w-3 h-3" />
                            Mô tả chi tiết
                        </label>
                        <div className="input-field items-start py-2">
                            <textarea 
                                className="min-h-[80px] resize-none"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                placeholder="Ghi chú thêm về khoản thu/chi này (không bắt buộc)..."
                                disabled={isSubmitting}
                            />
                        </div>
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
                        className={`btn flex-1 ${type === "INCOME" ? "btn-primary" : "bg-warm-400 hover:bg-warm-600 text-white border-warm-400"}`}
                        disabled={isSubmitting || !title || !amount}
                    >
                        {isSubmitting ? "Đang xử lý..." : "Lưu Giao Dịch"}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
