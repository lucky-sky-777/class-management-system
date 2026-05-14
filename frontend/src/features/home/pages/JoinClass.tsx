import React, { useState } from 'react';
import type { ClassItems } from '@features/home/types';
import { useHome } from '@features/home/hooks/useHome';
import { Modal } from '@shared/components/ui/Modal';

interface JoinClassModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSuccess: () => void;
}

export const JoinClassModal = ({ isOpen, onClose, onSuccess }: JoinClassModalProps) => {
    const { joinClassMutation } = useHome();
    const [step, setStep] = useState<'INPUT' | 'PENDING' | 'SUCCESS'>('INPUT');
    const [code, setCode] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    const [foundClass, setFoundClass] = useState<ClassItems | { name: string } | null>(null);

    if (!isOpen) return null;

    const handleClose = () => {
        onClose();
        setTimeout(() => {
            setStep('INPUT');
            setCode("");
            setError("");
            setFoundClass(null);
        }, 300);
    }

    const handleJoin = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!code.trim()) return;

        setLoading(true);
        setError("");

        try {
            const result = await joinClassMutation(code);
            const classData = (result as any)?.data || result;
            console.log("DỮ LIỆU TỪ BACKEND TRẢ VỀ LÀ:", classData); 
            console.log("TRẠNG THÁI JOIN LÀ:", classData?.userJoinStatus);
            
            setFoundClass(classData || { name: `Mã: ${code}` });

            if (classData?.type === 'REQUESTED') {
                setStep('PENDING');
            } else {
                setStep('SUCCESS');
                setTimeout(() => { 
                    onSuccess(); 
                    handleClose(); 
                }, 1500);
            }

        } catch (err: any) {
            setError(err.message || "Lỗi: Không thể tham gia lớp lúc này");
        } finally {
            setLoading(false);
        }
    };

    return (
        <Modal isOpen={isOpen} onClose={handleClose} title="Tham gia lớp học">
            <div className="w-full max-w-[340px] mx-auto animate-in fade-in zoom-in duration-200">
                
                {/* BƯỚC 1: NHẬP MÃ */}
                {step === 'INPUT' && (
                    <>
                        <p className="text-sm text-ink-2 mb-4 text-center">Nhập mã lớp học để tham gia ngay.</p>
                        <form onSubmit={handleJoin} className="space-y-3">
                            <div>
                                <input 
                                    type="text"
                                    value={code}
                                    onChange={(e) => {
                                        setCode(e.target.value.toUpperCase());
                                        setError("");
                                    }}
                                    placeholder="MÃ LỚP (VD: MATH101)"
                                    className="w-full border border-rule bg-surface-2 rounded-xl py-2.5 px-4 focus:ring-2 focus:ring-blue-border focus:border-blue-text outline-none font-bold text-base text-center tracking-widest uppercase placeholder:normal-case placeholder:text-ink-4 placeholder:font-medium transition-all"
                                    autoFocus
                                />
                                {error && (
                                    <p className="text-red-text text-[11px] font-medium mt-1.5 text-center">
                                        {error}
                                    </p>
                                )}
                            </div>
                            
                            <div className="flex gap-2 pt-2">
                                <button 
                                    type="button" 
                                    onClick={handleClose} 
                                    className="flex-1 py-2 bg-surface-2 hover:bg-surface-3 text-ink-2 text-sm font-semibold rounded-lg transition-colors"
                                >
                                    Hủy
                                </button>
                                <button 
                                    type="submit" 
                                    disabled={loading || !code.trim()} 
                                    className="flex-[2] py-2 bg-warm-600 hover:bg-warm-400 text-white text-sm font-semibold rounded-lg shadow-sm disabled:opacity-50 transition-colors"
                                >
                                    {loading ? "ĐANG KIỂM TRA..." : "THAM GIA"}
                                </button>
                            </div>
                        </form>
                    </>
                )}

                {/* BƯỚC 2: CHỜ DUYỆT */}
                {step === 'PENDING' && (
                    <div className="text-center py-2">
                        <div className="w-12 h-12 bg-amber-fill text-amber-text rounded-full flex items-center justify-center mx-auto mb-3 text-xl shadow-inner">
                            ⏳
                        </div>
                        <h2 className="text-lg font-bold text-ink-1 mb-1">Đã gửi yêu cầu</h2>
                        <p className="text-sm text-ink-2 mb-5 leading-relaxed">
                            Bạn đã yêu cầu tham gia lớp <b className="text-ink-1">{foundClass?.name}</b>. Vui lòng chờ giáo viên duyệt.
                        </p>
                        <button 
                            onClick={handleClose} 
                            className="w-full py-2 bg-surface-2 hover:bg-surface-3 text-ink-1 text-sm font-semibold rounded-lg transition-colors"
                        >
                            Đóng
                        </button>
                    </div>
                )}

                {/* BƯỚC 3: THÀNH CÔNG */}
                {step === 'SUCCESS' && (
                    <div className="text-center py-2">
                        <div className="w-12 h-12 bg-green-fill text-green-text rounded-full flex items-center justify-center mx-auto mb-3 text-xl shadow-inner">
                            ✅
                        </div>
                        <h2 className="text-lg font-bold text-ink-1 mb-1">Thành công!</h2>
                        <p className="text-sm text-ink-2">
                            Chào mừng đến với lớp <b className="text-ink-1">{foundClass?.name}</b>.
                        </p>
                    </div>
                )}
            </div>
        </Modal>
    );
};