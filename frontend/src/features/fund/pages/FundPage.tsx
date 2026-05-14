import React, { useState } from "react";
import { useParams } from "react-router-dom";
import { useFund } from "../hooks/useFund";
import { FundFormModal } from "../components/FundFormModal";
import { FundQrModal } from "../components/FundQrModal";
import { useAuth } from "@features/auth";
// TODO: Tối ưu hiệu năng truy vấn (Performance optimization)
// Hiện tại dùng useMembers để lấy myRole và map tên user. Trong tương lai nên tách API nhẹ hơn.
import { useMembers } from "@features/member/hooks/useMembers"; 
import type { ID } from "@shared/utils/common";

export const FundPage: React.FC = () => {
    const { classId } = useParams<{ classId: string }>();
    const numericClassId = Number(classId);
    
    const { 
        summary, funds, isLoading, error, 
        createFund, deleteFund 
    } = useFund(numericClassId);
    
    const { user } = useAuth();
    const { members, myRole } = useMembers(classId || "", user?.id);

    const isAdminOrOwner = myRole === "OWNER" || myRole === "CLASS_ADMIN";

    const [isFormOpen, setIsFormOpen] = useState(false);
    const [isQrOpen, setIsQrOpen] = useState(false);

    const getUserName = (userId: ID) => {
        const member = members.find(m => String(m.userId) === String(userId));
        return member?.displayName || member?.username || `User #${userId}`;
    };

    const handleDelete = async (fundId: ID) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa giao dịch này?")) {
            await deleteFund(fundId);
        }
    };

    if (isLoading && !summary && funds.length === 0) {
        return <div className="p-10 text-center animate-pulse text-ink-3">Đang tải thông tin quỹ...</div>;
    }

    if (error && !summary) {
        return <div className="p-10 text-center text-red-500">{error}</div>;
    }

    return (
        <div className="space-y-8 animate-fade-in max-w-5xl mx-auto">
            {/* Header / Stats */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div className="stat-card">
                    <div className="stat-label">Số dư hiện tại</div>
                    <div className="stat-value text-warm-400">
                        {summary?.balance ? summary.balance.toLocaleString('vi-VN') : '0'}đ
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Tổng thu</div>
                    <div className="stat-value text-green-text text-2xl">
                        {summary?.total_income ? summary.total_income.toLocaleString('vi-VN') : '0'}đ
                    </div>
                </div>
                <div className="stat-card">
                    <div className="stat-label">Tổng chi</div>
                    <div className="stat-value text-ink-3 text-2xl">
                        {summary?.total_expense ? summary.total_expense.toLocaleString('vi-VN') : '0'}đ
                    </div>
                </div>
            </div>

            {/* Main Content */}
            <div className="card">
                <div className="card-header flex flex-col md:flex-row justify-between md:items-center gap-4">
                    <div>
                        <h2 className="card-title">Sổ thu chi</h2>
                        <p className="card-subtitle">Lịch sử các giao dịch quỹ lớp</p>
                    </div>
                    <div className="flex items-center gap-2">
                        {isAdminOrOwner && (
                            <>
                                <button onClick={() => setIsQrOpen(true)} className="btn btn-secondary btn-sm">
                                    Mã QR Quỹ Lớp
                                </button>
                                <button onClick={() => setIsFormOpen(true)} className="btn btn-warm btn-sm">
                                    + Thêm giao dịch
                                </button>
                            </>
                        )}
                    </div>
                </div>
                <div className="card-body p-0">
                    {funds.length === 0 ? (
                        <div className="p-10 text-center text-ink-3">Chưa có giao dịch nào.</div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left border-collapse">
                                <thead>
                                    <tr className="bg-surface-2 text-ink-2 text-sm">
                                        <th className="p-4 font-medium border-b border-rule">Thời gian</th>
                                        <th className="p-4 font-medium border-b border-rule">Giao dịch</th>
                                        <th className="p-4 font-medium border-b border-rule text-right">Số tiền</th>
                                        <th className="p-4 font-medium border-b border-rule">Người tạo</th>
                                        {isAdminOrOwner && (
                                            <th className="p-4 font-medium border-b border-rule w-16"></th>
                                        )}
                                    </tr>
                                </thead>
                                <tbody>
                                    {funds.map(fund => (
                                        <tr key={fund.id} className="hover:bg-surface-1 transition-colors border-b border-rule last:border-0">
                                            <td className="p-4 text-sm text-ink-2">
                                                {new Date(fund.created_at).toLocaleString('vi-VN')}
                                            </td>
                                            <td className="p-4">
                                                <div className="font-medium text-ink-1">{fund.title}</div>
                                                {fund.description && (
                                                    <div className="text-sm text-ink-3 line-clamp-1" title={fund.description}>
                                                        {fund.description}
                                                    </div>
                                                )}
                                            </td>
                                            <td className="p-4 text-right font-medium">
                                                {fund.type === "INCOME" ? (
                                                    <span className="text-green-text">+{fund.amount.toLocaleString('vi-VN')}đ</span>
                                                ) : (
                                                    <span className="text-red-500">-{fund.amount.toLocaleString('vi-VN')}đ</span>
                                                )}
                                            </td>
                                            <td className="p-4 text-sm text-ink-2">
                                                {getUserName(fund.creator_user_id)}
                                            </td>
                                            {isAdminOrOwner && (
                                                <td className="p-4 text-center">
                                                    <button 
                                                        onClick={() => handleDelete(fund.id)}
                                                        className="text-red-400 hover:text-red-600 transition-colors p-1"
                                                        title="Xóa giao dịch"
                                                    >
                                                        🗑️
                                                    </button>
                                                </td>
                                            )}
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
            </div>

            {/* Modals */}
            <FundFormModal 
                isOpen={isFormOpen}
                onClose={() => setIsFormOpen(false)}
                onSubmit={createFund}
            />

            <FundQrModal 
                isOpen={isQrOpen}
                onClose={() => setIsQrOpen(false)}
            />
        </div>
    );
};
