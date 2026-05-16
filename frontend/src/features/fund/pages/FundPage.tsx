import React, { useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { 
    Wallet, 
    TrendingUp, 
    TrendingDown, 
    Plus, 
    QrCode, 
    Search, 
    Trash2,
    Calendar,
    ArrowUpRight,
    ArrowDownLeft,
    Inbox,
    Building2,
    Upload,
    ShieldCheck
} from "lucide-react";
import { useFund } from "../hooks/useFund";
import { FundFormModal } from "../components/FundFormModal";
import { BankConfigModal } from "../components/BankConfigModal";
import { BankQrModal } from "../components/BankQrModal";
import { SubmitPaymentModal } from "../components/SubmitPaymentModal";
import { FundPaymentList } from "../components/FundPaymentList";
import { useAuth } from "@features/auth";
import { useMembers } from "@features/member/hooks/useMembers"; 
import type { ID } from "@shared/utils/common";

export const FundPage: React.FC = () => {
    const { classId } = useParams<{ classId: string }>();
    const numericClassId = Number(classId);
    
    const { 
        summary, funds, isLoading, error, 
        createFund, deleteFund,
        bankConfig, updateBankConfig
    } = useFund(numericClassId);
    
    const { user } = useAuth();
    const { members, myRole } = useMembers(classId || "", user?.id);

    const isAdminOrOwner = myRole === "OWNER" || myRole === "CLASS_ADMIN";

    const [isFormOpen, setIsFormOpen] = useState(false);
    const [isBankConfigOpen, setIsBankConfigOpen] = useState(false);
    const [isBankQrOpen, setIsBankQrOpen] = useState(false);
    const [submitPaymentFundId, setSubmitPaymentFundId] = useState<ID | null>(null);
    const [viewPaymentsFundId, setViewPaymentsFundId] = useState<ID | null>(null);
    const [selectedFundTitle, setSelectedFundTitle] = useState("");
    const [selectedFundAmount, setSelectedFundAmount] = useState(0);
    const [selectedFundQrUrl, setSelectedFundQrUrl] = useState<string | undefined>(undefined);
    const [searchTerm, setSearchTerm] = useState("");
    const [activeTab, setActiveTab] = useState<"INCOME" | "EXPENSE">("INCOME");

    const getUserName = (userId: ID) => {
        const member = members.find(m => String(m.userId) === String(userId));
        return member?.displayName || member?.username || `Thành viên #${userId}`;
    };

    const handleDelete = async (fundId: ID) => {
        if (window.confirm("Bạn có chắc chắn muốn xóa giao dịch này?")) {
            await deleteFund(fundId);
        }
    };

    const filteredFunds = useMemo(() => {
        return funds.filter(fund => {
            const matchesSearch = fund.title.toLowerCase().includes(searchTerm.toLowerCase()) || 
                                (fund.description && fund.description.toLowerCase().includes(searchTerm.toLowerCase()));
            return matchesSearch && fund.type === activeTab;
        });
    }, [funds, searchTerm, activeTab]);

    if (isLoading && !summary && funds.length === 0) {
        return (
            <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
                <div className="w-12 h-12 border-4 border-warm-400/20 border-t-warm-400 rounded-full animate-spin"></div>
                <p className="text-ink-3 font-medium">Đang tải thông tin quỹ...</p>
            </div>
        );
    }

    if (error && !summary) {
        return (
            <div className="p-10 text-center max-w-md mx-auto">
                <div className="bg-ink-red-fill p-6 rounded-2xl border border-ink-red-border">
                    <p className="text-ink-red-text font-medium">{error}</p>
                </div>
            </div>
        );
    }

    return (
        <div className="space-y-8 animate-fade-in max-w-5xl mx-auto pb-10 px-4">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-serif font-semibold text-ink-1">Quỹ Lớp</h1>
                    <p className="text-ink-3 mt-1">Quản lý thu chi và minh bạch tài chính trong lớp học</p>
                </div>
                <div className="flex items-center gap-3 w-full md:w-auto">
                    {/* Optionally we can put general buttons here, but now they are moved to the tabs */}
                </div>
            </div>

            {/* Summary Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
                <div className="stat-card bg-gradient-to-br from-warm-400 to-warm-600 border-none shadow-lg shadow-warm-400/20">
                    <div className="flex justify-between items-start mb-4">
                        <div className="p-2 bg-white/20 rounded-lg backdrop-blur-md">
                            <Wallet className="w-5 h-5 text-white" />
                        </div>
                    </div>
                    <div className="stat-label text-white/70">Số dư hiện tại</div>
                    <div className="text-3xl font-serif font-bold text-white tracking-tight">
                        {summary?.balance ? summary.balance.toLocaleString('vi-VN') : '0'}
                        <span className="text-lg ml-1 font-sans font-medium opacity-80">đ</span>
                    </div>
                </div>

                <div className="stat-card border-ink-green-border bg-ink-green-fill/30">
                    <div className="flex justify-between items-start mb-4">
                        <div className="p-2 bg-ink-green-fill rounded-lg">
                            <TrendingUp className="w-5 h-5 text-ink-green-text" />
                        </div>
                    </div>
                    <div className="stat-label">Tổng thu</div>
                    <div className="text-2xl font-serif font-bold text-ink-green-text tracking-tight">
                        {summary?.total_income ? summary.total_income.toLocaleString('vi-VN') : '0'}
                        <span className="text-base ml-1 font-sans font-medium opacity-70">đ</span>
                    </div>
                </div>

                <div className="stat-card border-rule-md bg-surface-2">
                    <div className="flex justify-between items-start mb-4">
                        <div className="p-2 bg-surface-3 rounded-lg">
                            <TrendingDown className="w-5 h-5 text-ink-2" />
                        </div>
                    </div>
                    <div className="stat-label">Tổng chi</div>
                    <div className="text-2xl font-serif font-bold text-ink-1 tracking-tight">
                        {summary?.total_expense ? summary.total_expense.toLocaleString('vi-VN') : '0'}
                        <span className="text-base ml-1 font-sans font-medium opacity-70">đ</span>
                    </div>
                </div>
            </div>

            {/* Transactions Section with Tabs */}
            <div className="card shadow-sm border-rule overflow-hidden">
                <div className="flex border-b border-rule">
                    <button
                        onClick={() => setActiveTab("INCOME")}
                        className={`flex-1 py-4 text-sm font-bold uppercase tracking-wider transition-colors ${
                            activeTab === "INCOME" 
                            ? "text-ink-green-text border-b-2 border-ink-green-text bg-ink-green-fill/10" 
                            : "text-ink-3 hover:bg-surface-2"
                        }`}
                    >
                        Thu Tiền (Khoản Thu)
                    </button>
                    <button
                        onClick={() => setActiveTab("EXPENSE")}
                        className={`flex-1 py-4 text-sm font-bold uppercase tracking-wider transition-colors ${
                            activeTab === "EXPENSE" 
                            ? "text-warm-text border-b-2 border-warm-text bg-warm-fill/10" 
                            : "text-ink-3 hover:bg-surface-2"
                        }`}
                    >
                        Chi Tiêu (Khoản Chi)
                    </button>
                </div>
                
                {/* Actions & Filter Bar */}
                <div className="p-4 flex flex-col sm:flex-row justify-between gap-4 bg-surface-2/30">
                    <div className="relative flex-1 max-w-md">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-ink-3" />
                        <input 
                            type="text"
                            placeholder={activeTab === "INCOME" ? "Tìm kiếm khoản thu..." : "Tìm kiếm khoản chi..."}
                            className="input-field w-full pl-10 bg-white"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>
                    
                    <div className="flex gap-2">
                        {bankConfig && (
                            <button
                                onClick={() => setIsBankQrOpen(true)}
                                className="btn btn-secondary shadow-sm"
                                title="Xem mã QR thanh toán lớp"
                            >
                                <QrCode className="w-4 h-4 md:mr-2" />
                                <span className="hidden md:inline">Xem QR Quỹ Lớp</span>
                            </button>
                        )}
                        {isAdminOrOwner && activeTab === "INCOME" && (
                            <button 
                                onClick={() => setIsBankConfigOpen(true)} 
                                className="btn btn-secondary shadow-sm"
                                title="Cài đặt tài khoản ngân hàng nhận tiền"
                            >
                                <Building2 className="w-4 h-4 md:mr-2" />
                                <span className="hidden md:inline">Cài đặt Bank</span>
                            </button>
                        )}
                        <button 
                            onClick={() => setIsFormOpen(true)} 
                            className={`btn shadow-md ${activeTab === "INCOME" ? "btn-primary bg-ink-green-text hover:bg-green-600 border-none shadow-ink-green-fill" : "btn-primary shadow-warm-400/20"}`}
                        >
                            <Plus className="w-4 h-4 md:mr-2" />
                            <span className="hidden md:inline">{activeTab === "INCOME" ? "Thêm Khoản Thu" : "Thêm Khoản Chi"}</span>
                        </button>
                    </div>
                </div>

                <div className="card-body">
                    {filteredFunds.length === 0 ? (
                        <div className="py-20 flex flex-col items-center justify-center text-ink-3">
                            <div className="w-16 h-16 bg-surface-2 rounded-full flex items-center justify-center mb-4">
                                <Inbox className="w-8 h-8 opacity-20" />
                            </div>
                            <p className="font-medium">Không tìm thấy giao dịch nào</p>
                            <p className="text-sm opacity-60">Hãy thử thay đổi từ khóa tìm kiếm</p>
                        </div>
                    ) : (
                        <div className="overflow-x-auto">
                            <table className="w-full text-left border-collapse">
                                <thead>
                                    <tr className="bg-surface-2/50 text-ink-3 text-[10px] uppercase tracking-widest font-bold">
                                        <th className="px-6 py-3 border-b border-rule">Giao dịch / Nội dung</th>
                                        <th className="px-6 py-3 border-b border-rule text-right">Số tiền</th>
                                        <th className="px-6 py-3 border-b border-rule hidden md:table-cell">Người tạo</th>
                                        <th className="px-6 py-3 border-b border-rule text-right"></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {filteredFunds.map(fund => (
                                        <tr key={fund.id} className="group hover:bg-surface-2/30 transition-all border-b border-rule last:border-0">
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-4">
                                                    <div className={`hidden sm:flex w-10 h-10 rounded-xl items-center justify-center flex-shrink-0 ${
                                                        fund.type === "INCOME" 
                                                            ? "bg-ink-green-fill text-ink-green-text" 
                                                            : "bg-warm-fill text-warm-text"
                                                    }`}>
                                                        {fund.type === "INCOME" ? <ArrowDownLeft className="w-5 h-5" /> : <ArrowUpRight className="w-5 h-5" />}
                                                    </div>
                                                    <div>
                                                        <div className="font-semibold text-ink-1 group-hover:text-warm-400 transition-colors">
                                                            {fund.title}
                                                        </div>
                                                        <div className="flex flex-wrap items-center gap-x-4 gap-y-1 mt-1">
                                                            <div className="flex items-center gap-1.5 text-xs text-ink-3">
                                                                <Calendar className="w-3.5 h-3.5" />
                                                                {new Date(fund.created_at).toLocaleDateString('vi-VN', {
                                                                    day: '2-digit', month: '2-digit', year: 'numeric',
                                                                    hour: '2-digit', minute: '2-digit'
                                                                })}
                                                            </div>
                                                            {fund.description && (
                                                                <div className="text-xs text-ink-3 italic line-clamp-1 max-w-[200px]" title={fund.description}>
                                                                    — {fund.description}
                                                                </div>
                                                            )}
                                                        </div>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <div className={`text-base font-bold font-serif ${
                                                    fund.type === "INCOME" ? "text-ink-green-text" : "text-ink-1"
                                                }`}>
                                                    {fund.type === "INCOME" ? "+" : "-"}
                                                    {fund.amount.toLocaleString('vi-VN')}
                                                    <span className="text-xs ml-0.5 font-sans font-medium">đ</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 hidden md:table-cell">
                                                <div className="flex items-center gap-2 text-sm text-ink-2">
                                                    <div className="w-6 h-6 rounded-full bg-surface-3 flex items-center justify-center text-[10px] font-bold">
                                                        {getUserName(fund.creator_user_id).charAt(0).toUpperCase()}
                                                    </div>
                                                    <span>{getUserName(fund.creator_user_id)}</span>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-right">
                                                <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-all">
                                                    {fund.type === "INCOME" && (
                                                        isAdminOrOwner ? (
                                                            <button 
                                                                onClick={() => {
                                                                    setViewPaymentsFundId(fund.id);
                                                                    setSelectedFundTitle(fund.title);
                                                                }}
                                                                className="p-2 text-ink-3 hover:text-warm-400 hover:bg-warm-fill rounded-lg transition-all"
                                                                title="Xem và duyệt minh chứng"
                                                            >
                                                                <ShieldCheck className="w-4 h-4" />
                                                            </button>
                                                        ) : (
                                                            <button 
                                                                onClick={() => {
                                                                    setSubmitPaymentFundId(fund.id);
                                                                    setSelectedFundTitle(fund.title);
                                                                    setSelectedFundAmount(fund.amount);
                                                                    setSelectedFundQrUrl(fund.qr_code_url);
                                                                }}
                                                                className="p-2 text-ink-3 hover:text-ink-green-text hover:bg-ink-green-fill rounded-lg transition-all"
                                                                title="Nộp minh chứng thanh toán"
                                                            >
                                                                <Upload className="w-4 h-4" />
                                                            </button>
                                                        )
                                                    )}
                                                    
                                                    {isAdminOrOwner && (
                                                        <button 
                                                            onClick={() => handleDelete(fund.id)}
                                                            className="p-2 text-ink-3 hover:text-ink-red-text hover:bg-ink-red-fill rounded-lg transition-all"
                                                            title="Xóa giao dịch"
                                                        >
                                                            <Trash2 className="w-4 h-4" />
                                                        </button>
                                                    )}
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </div>
                
                {filteredFunds.length > 0 && (
                    <div className="card-footer bg-surface-2/30 justify-between py-3">
                        <div className="text-xs text-ink-3">
                            Hiển thị <strong>{filteredFunds.length}</strong> giao dịch
                        </div>
                        <div className="flex gap-1">
                            {/* Pagination would go here in the future */}
                        </div>
                    </div>
                )}
            </div>

            {/* Modals */}
            <FundFormModal 
                isOpen={isFormOpen}
                onClose={() => setIsFormOpen(false)}
                onSubmit={createFund}
                defaultType={activeTab}
            />

            <BankConfigModal
                isOpen={isBankConfigOpen}
                onClose={() => setIsBankConfigOpen(false)}
                initialConfig={bankConfig}
                onSave={updateBankConfig}
            />

            <BankQrModal
                isOpen={isBankQrOpen}
                onClose={() => setIsBankQrOpen(false)}
                bankConfig={bankConfig}
            />

            {submitPaymentFundId && (
                <SubmitPaymentModal
                    isOpen={true}
                    onClose={() => setSubmitPaymentFundId(null)}
                    fundId={submitPaymentFundId}
                    classId={numericClassId}
                    fundTitle={selectedFundTitle}
                    fundAmount={selectedFundAmount}
                    qrCodeUrl={selectedFundQrUrl}
                />
            )}

            {viewPaymentsFundId && (
                <FundPaymentList
                    isOpen={true}
                    onClose={() => setViewPaymentsFundId(null)}
                    fundId={viewPaymentsFundId}
                    classId={numericClassId}
                    fundTitle={selectedFundTitle}
                />
            )}
        </div>
    );
};
