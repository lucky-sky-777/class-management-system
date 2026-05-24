import React, { useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import {
  Wallet,
  TrendingUp,
  TrendingDown,
  Plus,
  Search,
  Trash2,
  Calendar,
  ArrowUpRight,
  ArrowDownLeft,
  Inbox,
  Building2,
  Upload,
  ShieldCheck,
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
    summary,
    funds,
    isLoading,
    error,
    createFund,
    deleteFund,
    bankConfig,
    updateBankConfig,
  } = useFund(numericClassId);

  const { user } = useAuth();
  const { members, myRole } = useMembers(classId || "", user?.id);

  const isAdminOrOwner = myRole === "OWNER" || myRole === "CLASS_ADMIN";

  const [isFormOpen, setIsFormOpen] = useState(false);
  const [isQrOpen, setIsQrOpen] = useState(false);
  const [isBankConfigOpen, setIsBankConfigOpen] = useState(false);
  const [submitPaymentFundId, setSubmitPaymentFundId] = useState<ID | null>(null);
  const [viewPaymentsFundId, setViewPaymentsFundId] = useState<ID | null>(null);
  const [selectedFundTitle, setSelectedFundTitle] = useState("");
  const [selectedFundDescription, setselectedFundDescription] = useState("");

  const [selectedFundAmount, setSelectedFundAmount] = useState(0);
  const [selectedFundQrUrl, setSelectedFundQrUrl] = useState<string | undefined>(undefined);
  const [searchTerm, setSearchTerm] = useState("");
  const [activeTab, setActiveTab] = useState<"INCOME" | "EXPENSE">("INCOME");

  const getUserName = (userId: ID) => {
    const member = members.find((m) => String(m.userId) === String(userId));
    return member?.displayName || member?.username || `Thành viên #${userId}`;
  };

  const handleDelete = async (fundId: ID) => {
    if (window.confirm("Bạn có chắc chắn muốn xóa giao dịch này?")) {
      await deleteFund(fundId);
    }
  };

  const filteredFunds = useMemo(() => {
    return funds.filter((fund) => {
      const matchesSearch =
        fund.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        (fund.description &&
          fund.description.toLowerCase().includes(searchTerm.toLowerCase()));
      return matchesSearch && fund.type === activeTab;
    });
  }, [funds, searchTerm, activeTab]);

  if (isLoading && !summary && funds.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
        <p className="text-ink-2 text-sm italic font-medium">
          Đang tải thông tin quỹ...
        </p>
      </div>
    );
  }

  if (error && !summary) {
    return (
      <div className="p-10 text-center max-w-md mx-auto animate-fade-in">
        <div className="bg-[var(--red-fill)] p-6 rounded-2xl border border-[var(--red-border)] shadow-sm">
          <p className="text-[var(--red-text)] font-bold">⚠️ {error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6 max-w-5xl mx-auto pb-12 px-4 md:px-6">
      {/* Header Section */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 border-b border-[var(--rule)] pb-4">
        <div>
          <h1 className="text-2xl md:text-3xl font-serif font-black text-ink-1 tracking-tight">
            Quỹ Lớp
          </h1>
          <p className="text-sm text-ink-2 mt-1">
            Quản lý thu chi và minh bạch tài chính trong lớp học
          </p>
        </div>

        {/* Thanh điều hướng dạng Tab lồng nhau (Pill Style) */}
        <div className="bg-[var(--bg-surface-2)] p-1 rounded-xl flex w-full md:w-auto shadow-inner border border-[var(--rule)]">
          <button
            onClick={() => setActiveTab("INCOME")}
            className={`flex-1 md:flex-none px-5 py-2 text-xs font-bold uppercase tracking-wider rounded-lg transition-all active:scale-95 ${
              activeTab === "INCOME"
                ? "bg-surface text-[var(--green-text)] shadow-sm font-black border border-[var(--rule)]"
                : "text-ink-2 hover:text-ink-1"
            }`}
          >
            Khoản Thu
          </button>
          <button
            onClick={() => setActiveTab("EXPENSE")}
            className={`flex-1 md:flex-none px-5 py-2 text-xs font-bold uppercase tracking-wider rounded-lg transition-all active:scale-95 ${
              activeTab === "EXPENSE"
                ? "bg-surface text-[var(--warm-text)] shadow-sm font-black border border-[var(--rule)]"
                : "text-ink-2 hover:text-ink-1"
            }`}
          >
            Khoản Chi
          </button>
        </div>
      </div>

      {/* Layout Bento Box Thống kê */}
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {/* 1. SỐ DƯ HIỆN TẠI (Chiếm 2 cột) */}
        <div className="md:col-span-2 p-5 bg-[var(--warm-fill)] border border-[var(--warm-border)] rounded-2xl shadow-sm flex flex-col justify-between relative overflow-hidden group">
          <div className="absolute right-0 bottom-0 translate-x-4 translate-y-4 opacity-5 pointer-events-none group-hover:scale-110 transition-transform duration-300">
            <Wallet size={120} />
          </div>
          <div className="flex items-center gap-2">
            <div className="p-2 bg-surface text-[var(--warm-400)] rounded-xl border border-[var(--rule)] shadow-xs">
              <Wallet className="w-4 h-4" />
            </div>
            <span className="text-xs font-bold text-[var(--warm-text)] uppercase tracking-wider">
              Số dư hiện tại
            </span>
          </div>
          <div className="text-3xl font-serif font-bold [font-variant-numeric:lining-nums] text-[var(--warm-text)] tracking-tight mt-6 flex items-baseline gap-1">
            <span>
              {summary?.balance ? summary.balance.toLocaleString("vi-VN") : "0"}
            </span>
            <span className="text-lg font-sans font-medium">đ</span>
          </div>
        </div>

        {/* 2. TỔNG THU (Chiếm 1 cột) */}
        <div className="p-5 bg-[var(--green-fill)] border border-[var(--green-border)] rounded-2xl shadow-sm flex flex-col justify-between">
          <div className="flex items-center gap-2">
            <div className="p-2 bg-surface text-[var(--green-text)] rounded-xl border border-[var(--rule)] shadow-xs">
              <TrendingUp className="w-4 h-4" />
            </div>
            <span className="text-xs font-bold text-[var(--green-text)] uppercase tracking-wider">
              Tổng thu
            </span>
          </div>
          <div className="text-2xl font-serif font-bold [font-variant-numeric:lining-nums] text-[var(--green-text)] tracking-tight mt-6 flex items-baseline gap-1">
            <span>
              {summary?.total_income
                ? summary.total_income.toLocaleString("vi-VN")
                : "0"}
            </span>
            <span className="text-base font-sans font-medium">đ</span>
          </div>
        </div>

        {/* 3. TỔNG CHI (Chiếm 1 cột) */}
        <div className="p-5 bg-[var(--bg-surface-2)] border border-[var(--rule-md)] rounded-2xl shadow-sm flex flex-col justify-between">
          <div className="flex items-center gap-2">
            <div className="p-2 bg-surface text-[var(--ink-2)] rounded-xl border border-[var(--rule)] shadow-xs">
              <TrendingDown className="w-4 h-4" />
            </div>
            <span className="text-xs font-bold text-[var(--ink-2)] uppercase tracking-wider">
              Tổng chi
            </span>
          </div>
          <div className="text-2xl font-serif font-bold [font-variant-numeric:lining-nums] text-[var(--ink-1)] tracking-tight mt-6 flex items-baseline gap-1">
            <span>
              {summary?.total_expense
                ? summary.total_expense.toLocaleString("vi-VN")
                : "0"}
            </span>
            <span className="text-base font-sans font-medium">đ</span>
          </div>
        </div>
      </div>

      {/* Danh sách giao dịch */}
      <div className="space-y-4">
        {/* Actions & Filter Bar */}
        <div className="flex flex-col sm:flex-row justify-between items-stretch sm:items-center gap-3 bg-[var(--bg-surface)] p-3 border border-[var(--rule)] rounded-2xl shadow-xs">
          <div className="relative flex-1">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-ink-3" />
            <input
              type="text"
              placeholder={
                activeTab === "INCOME"
                  ? "Tìm kiếm khoản thu..."
                  : "Tìm kiếm khoản chi..."
              }
              className="w-full bg-[var(--bg-surface-2)] border border-transparent focus:border-[var(--warm-400)] focus:bg-surface text-sm rounded-xl py-2 pl-10 pr-4 outline-none transition-all text-ink-1 font-medium"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>

          {isAdminOrOwner && (
            <div className="flex gap-2 shrink-0">
              {activeTab === "INCOME" && (
                <>
                  <button
                    onClick={() => setIsBankConfigOpen(true)}
                    className="flex items-center justify-center p-2.5 sm:px-4 sm:py-2 text-xs font-bold text-ink-2 bg-surface border border-[var(--rule-md)] hover:bg-[var(--bg-surface-2)] rounded-xl transition-all active:scale-95 shadow-2xs"
                    title="Cài đặt tài khoản ngân hàng"
                  >
                    <Building2 className="w-4 h-4 sm:mr-2" />
                    <span className="hidden sm:inline">Cài đặt Bank</span>
                  </button>
                </>
              )}
              <button
                onClick={() => setIsFormOpen(true)}
                className={`flex items-center justify-center p-2.5 sm:px-5 sm:py-2 text-xs font-black text-white rounded-xl transition-all active:scale-95 shadow-sm ${
                  activeTab === "INCOME"
                    ? "bg-[var(--green-text)] hover:opacity-90 shadow-[var(--green-border)]"
                    : "bg-[var(--warm-600)] hover:opacity-90 shadow-[var(--warm-border)]"
                }`}
              >
                <Plus className="w-4 h-4 sm:mr-1.5" />
                <span>
                  {activeTab === "INCOME" ? "TẠO KHOẢN THU" : "TẠO KHOẢN CHI"}
                </span>
              </button>
            </div>
          )}
        </div>

        {/* Bảng dữ liệu chính */}
        <div className="bg-surface border border-[var(--rule)] rounded-2xl overflow-hidden shadow-xs">
          {filteredFunds.length === 0 ? (
            <div className="py-16 flex flex-col items-center justify-center text-ink-3">
              <div className="w-12 h-12 bg-[var(--bg-surface-2)] rounded-full flex items-center justify-center mb-3 border border-[var(--rule)]">
                <Inbox className="w-5 h-5 opacity-40 text-ink-2" />
              </div>
              <p className="text-sm font-bold text-ink-2">
                Không tìm thấy giao dịch nào
              </p>
              <p className="text-xs opacity-60 mt-0.5">
                Hãy thử thay đổi từ khóa tìm kiếm
              </p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-[var(--bg-surface-2)]/60 text-ink-3 text-[10px] uppercase tracking-wider font-extrabold border-b border-[var(--rule)]">
                    <th className="px-6 py-3.5">Giao dịch / Nội dung</th>
                    <th className="px-6 py-3.5 text-right">Số tiền</th>
                    <th className="px-6 py-3.5 hidden md:table-cell">
                      Người tạo
                    </th>
                    <th className="px-6 py-3.5 text-right"></th>
                  </tr>
                </thead>
                <tbody>
                  {filteredFunds.map((fund) => (
                    <tr
                      key={fund.id}
                      className="group hover:bg-[var(--bg-surface-2)]/40 transition-all border-b border-[var(--rule)] last:border-0"
                    >
                      <td className="px-6 py-4">
                        <div className="flex items-center gap-3.5">
                          <div
                            className={`hidden sm:flex w-9 h-9 rounded-xl items-center justify-center flex-shrink-0 border ${
                              fund.type === "INCOME"
                                ? "bg-[var(--green-fill)] text-[var(--green-text)] border-[var(--green-border)]"
                                : "bg-[var(--warm-fill)] text-[var(--warm-text)] border-[var(--warm-border)]"
                            }`}
                          >
                            {fund.type === "INCOME" ? (
                              <ArrowDownLeft className="w-4 h-4" />
                            ) : (
                              <ArrowUpRight className="w-4 h-4" />
                            )}
                          </div>
                          <div className="min-w-0">
                            <div className="font-bold text-ink-1 text-sm truncate group-hover:text-[var(--warm-600)] transition-colors">
                              {fund.title}
                            </div>
                            <div className="flex flex-wrap items-center gap-x-3 gap-y-0.5 mt-0.5">
                              <div className="flex items-center gap-1 text-[11px] text-ink-3 font-medium">
                                <Calendar className="w-3 h-3" />
                                {new Date(fund.created_at).toLocaleDateString(
                                  "vi-VN",
                                  {
                                    day: "2-digit",
                                    month: "2-digit",
                                    year: "numeric",
                                    hour: "2-digit",
                                    minute: "2-digit",
                                  },
                                )}
                              </div>
                              {fund.description && (
                                <div
                                  className="text-[11px] text-ink-3 italic truncate max-w-[150px] sm:max-w-[250px]"
                                  title={fund.description}
                                >
                                  — {fund.description}
                                </div>
                              )}
                            </div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div
                          className={`text-base font-serif font-bold [font-variant-numeric:lining-nums] ${
                            fund.type === "INCOME"
                              ? "text-[var(--green-text)]"
                              : "text-ink-1"
                          }`}
                        >
                          {fund.type === "INCOME" ? "+" : "-"}
                          {fund.amount.toLocaleString("vi-VN")}
                          <span className="text-xs ml-0.5 font-sans font-bold">
                            đ
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 hidden md:table-cell">
                        <div className="flex items-center gap-2 text-xs text-ink-2 font-semibold">
                          <div className="w-6 h-6 rounded-full bg-[var(--bg-surface-3)] border border-[var(--rule)] flex items-center justify-center text-[10px] font-black text-ink-1 shadow-2xs">
                            {getUserName(fund.creator_user_id)
                              .charAt(0)
                              .toUpperCase()}
                          </div>
                          <span className="truncate max-w-[120px]">
                            {getUserName(fund.creator_user_id)}
                          </span>
                        </div>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex items-center justify-end gap-1 opacity-100 md:opacity-0 md:group-hover:opacity-100 transition-all duration-150">
                          {fund.type === "INCOME" &&
                            (isAdminOrOwner ? (
                              <button
                                onClick={() => {
                                  setViewPaymentsFundId(fund.id);
                                  setSelectedFundTitle(fund.title);
                                  setselectedFundDescription(fund.description || "")
                                }}
                                className="p-1.5 text-ink-3 hover:text-[var(--warm-600)] hover:bg-[var(--warm-fill)] border border-transparent hover:border-[var(--warm-border)] rounded-lg transition-all"
                                title="Xem và duyệt minh chứng"
                              >
                                <ShieldCheck className="w-4 h-4" />
                              </button>
                            ) : (
                              <button
                                onClick={() => {
                                  setSubmitPaymentFundId(fund.id);
                                  setSelectedFundTitle(fund.title);
                                  setselectedFundDescription(fund.description || "")
                                  setSelectedFundAmount(fund.amount);
                                  setSelectedFundQrUrl(fund.qr_code_url);
                                }}
                                className="p-1.5 text-ink-3 hover:text-[var(--green-text)] hover:bg-[var(--green-fill)] border border-transparent hover:border-[var(--green-border)] rounded-lg transition-all"
                                title="Nộp minh chứng thanh toán"
                              >
                                <Upload className="w-4 h-4" />
                              </button>
                            ))}

                          {isAdminOrOwner && (
                            <button
                              onClick={() => handleDelete(fund.id)}
                              className="p-1.5 text-ink-3 hover:text-[var(--red-text)] hover:bg-[var(--red-fill)] border border-transparent hover:border-[var(--red-border)] rounded-lg transition-all"
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
          <div className="flex justify-between items-center px-4 py-2 bg-[var(--bg-surface-2)]/40 border border-[var(--rule)] rounded-xl">
            <div className="text-[11px] font-bold text-ink-3 uppercase tracking-wider">
              Hiển thị <strong>{filteredFunds.length}</strong> giao dịch
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

      <BankQrModal
        isOpen={isQrOpen}
        onClose={() => setIsQrOpen(false)}
        bankConfig={bankConfig}
      />

      {isAdminOrOwner && (
        <BankConfigModal
          isOpen={isBankConfigOpen}
          onClose={() => setIsBankConfigOpen(false)}
          initialConfig={bankConfig}
          onSave={updateBankConfig}
        />
      )}

      {submitPaymentFundId && (
        <SubmitPaymentModal
          isOpen={true}
          onClose={() => setSubmitPaymentFundId(null)}
          fundId={submitPaymentFundId}
          classId={numericClassId}
          fundTitle={selectedFundTitle}
          fundDescription={selectedFundDescription}
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
          fundDescription={selectedFundDescription}
        />
      )}
    </div>
  );
};
