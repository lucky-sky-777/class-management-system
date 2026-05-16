import React, { useState, useMemo } from "react";
import { useParams } from "react-router-dom";
import { 
    Calendar, 
    Clock, 
    XCircle, 
    RotateCcw, 
    ExternalLink, 
    Search, 
    Check,
    X,
    Inbox,
    RefreshCw,
    FileText
} from "lucide-react";
import { useLeave } from "../hooks/useLeave";
import { LeaveFormModal } from "../components/LeaveFormModal";
import { useAuth } from "@features/auth";
import { useMembers } from "@features/member/hooks/useMembers";
import type { LeaveStatus, LeaveRequest } from "../types";
import type { ID } from "@shared/utils/common";
import { Avatar } from "@shared/components/ui/Avatar";

const STATUS_LABELS: Record<LeaveStatus | "ALL", string> = {
    ALL: "Tất cả đơn",
    PENDING: "Chờ duyệt",
    APPROVED: "Đã duyệt",
    REJECTED: "Từ chối",
    CANCELLED: "Đã hủy",
};

// --- Sub-components ---

/**
 * Item hiển thị thông tin một đơn xin nghỉ
 */
const LeaveItem: React.FC<{ 
    leave: LeaveRequest; 
    isAdmin: boolean;
    currentUserId: ID;
    onApprove: (id: ID) => void;
    onReject: (id: ID) => void;
    onCancel: (id: ID) => void;
}> = ({ leave, isAdmin, currentUserId, onApprove, onReject, onCancel }) => {
    
    const getStatusStyles = (status: LeaveStatus) => {
        switch (status) {
            case "PENDING": return "pill-amber";
            case "APPROVED": return "pill-green";
            case "REJECTED": return "pill-red";
            case "CANCELLED": return "bg-surface-3 text-ink-3 border-rule";
            default: return "pill-blue";
        }
    };

    const canManage = isAdmin && leave.status === "PENDING";
    const canCancel = String(leave.userId) === String(currentUserId) && leave.status === "PENDING";
    const displayName = leave.user_display_name || `Thành viên #${leave.userId}`;

    return (
        <div className="hover:border-warm-200 transition-all duration-300  m-1.5">
            <div className="p-5 flex flex-col md:flex-row md:items-center justify-between gap-6">
                <div className="space-y-4 flex-1">
                    <div className="flex items-center gap-3">
                        <span className={`pill ${getStatusStyles(leave.status)}`}>
                            <span className="pill-dot" />
                            {STATUS_LABELS[leave.status]}
                        </span>
                        <div className="flex items-center gap-1.5 text-[10px] font-mono text-ink-3">
                            <Clock className="w-3 h-3" />
                            {leave.created_at}
                            
                        </div>
                    </div>

                    <h4 className="text-lg font-sans font-bold text-ink-1 leading-snug group-hover:text-warm-600 transition-colors">
                        {leave.reason}
                    </h4>

                    <div className="flex flex-wrap items-center gap-x-6 gap-y-3 text-sm text-ink-2">
                        <div className="flex items-center gap-2 px-3 py-1.5 bg-surface-2 rounded-lg">
                            <Calendar className="w-4 h-4 text-ink-3" />
                            <span className="font-medium text-ink-1">
                                {new Date(leave.from).toLocaleDateString('vi-VN')} 
                                <span className="mx-2 text-ink-4">→</span>
                                {new Date(leave.to).toLocaleDateString('vi-VN')}
                            </span>
                        </div>
                        
                        {leave.proof_url && (
                            <a 
                                href={leave.proof_url} 
                                target="_blank" 
                                rel="noopener noreferrer"
                                className="flex items-center gap-1.5 text-blue-500 font-bold hover:underline py-1"
                            >
                                <ExternalLink className="w-4 h-4" />
                                Xem minh chứng
                            </a>
                        )}
                    </div>
                </div>

                <div className="flex flex-row md:flex-col items-center md:items-end justify-between md:justify-center gap-4 border-t md:border-t-0 md:border-l border-rule pt-4 md:pt-0 md:pl-6 min-w-[160px]">
                    <div className="flex flex-col items-start md:items-end">
                        <div className="flex items-center gap-2.5">
                            <span className="text-ink-1 font-sans text-right">{displayName}</span>
                            <Avatar
                                name={displayName}
                                size="md"
                                shape="circle"
                                src={leave.user_avatar_url}
                                className="rounded-full border border-rule object-cover"
                            />
                        </div>
                    </div>

                    <div className="flex items-center gap-2">
                        {canManage && (
                            <>
                                <button 
                                    onClick={() => onReject(leave.id)}
                                    className="p-2 text-ink-red-text hover:bg-ink-red-fill rounded-lg transition-colors border border-transparent hover:border-ink-red-border"
                                    title="Từ chối"
                                >
                                    <X className="w-5 h-5" />
                                </button>
                                <button 
                                    onClick={() => onApprove(leave.id)}
                                    className="p-2 text-ink-green-text hover:bg-ink-green-fill rounded-lg transition-colors border border-transparent hover:border-ink-green-border"
                                    title="Phê duyệt"
                                >
                                    <Check className="w-5 h-5" />
                                </button>
                            </>
                        )}
                        {canCancel && (
                            <button 
                                onClick={() => onCancel(leave.id)}
                                className="btn btn-secondary btn-sm text-ink-3 hover:text-ink-red-text"
                            >
                                <RotateCcw className="w-3.5 h-3.5 mr-1.5" />
                                Hủy đơn
                            </button>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

// --- Main Page ---

export const LeavePage: React.FC = () => {
    const { classId } = useParams<{ classId: string }>();
    const numericClassId = Number(classId);
    
    const { 
        leaves, isLoading, isSubmitting, error, 
        submitLeave, fetchLeaves, approveLeave, rejectLeave, cancelLeave 
    } = useLeave(numericClassId);
    
    const { user } = useAuth();
    const { myRole } = useMembers(classId || "", user?.id);
    const isAdmin = myRole === "OWNER" || myRole === "CLASS_ADMIN";

    const [activeTab, setActiveTab] = useState<LeaveStatus | "ALL">("ALL");
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [searchTerm, setSearchTerm] = useState("");

    const filteredLeaves = useMemo(() => {
        return leaves.filter(l => {
            const matchesTab = activeTab === "ALL" || l.status === activeTab;
            const displayName = l.user_display_name || "";
            const matchesSearch = l.reason.toLowerCase().includes(searchTerm.toLowerCase()) || 
                                 displayName.toLowerCase().includes(searchTerm.toLowerCase());
            return matchesTab && matchesSearch;
        });
    }, [leaves, activeTab, searchTerm]);

    return (
        <div className="space-y-8 animate-fade-in max-w-5xl mx-auto pb-20 px-4">
            {/* Header Section */}
            <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
                <div>
                    <h1 className="text-3xl font-serif font-semibold text-ink-1">Nghỉ phép</h1>
                    <p className="text-ink-3 mt-1">Quản lý và theo dõi các đơn xin nghỉ phép trong lớp</p>
                </div>
                <button
                    onClick={() => setIsModalOpen(true)}
                    className="btn btn-primary shadow-lg shadow-warm-400/20 px-6 py-3"
                >
                    <FileText className="w-4 h-4 mr-2" />
                    <span>Tạo đơn xin nghỉ</span>
                </button>
            </div>

            {/* Filter & Search Bar */}
            <div className="card shadow-sm border-rule">
                <div className="p-4 space-y-4">
                    <div className="flex flex-col sm:flex-col gap-4">
                        {/* nav bar */}
                        <div className="flex overflow-x-auto no-scrollbar gap-1 p-1 bg-surface-2 rounded-xl shrink-0">
                            {(Object.keys(STATUS_LABELS) as Array<LeaveStatus | "ALL">).map((tab) => (
                                <button
                                    key={tab}
                                    onClick={() => setActiveTab(tab)}
                                    className={`px-4 py-1.5 text-xs font-bold rounded-lg transition-all whitespace-nowrap ${
                                        activeTab === tab
                                            ? "bg-white text-warm-600 shadow-sm"
                                            : "text-ink-3 hover:text-ink-1"
                                    }`}
                                >
                                    {STATUS_LABELS[tab]}
                                </button>
                            ))}
                        </div>
                        {/* search bar */}
                        <div className="input-field justify-center align-middle w-full">
                            <Search className=" w-4 h-4 text-ink-3" />
                            <input 
                                type="text"
                                placeholder="Tìm theo lý do hoặc tên học sinh..."
                                className="w-full pl-10"
                                value={searchTerm}
                                onChange={(e) => setSearchTerm(e.target.value)}
                            />
                        </div>
                    </div>
                </div>

                <div className="card-body p-0">
                    {isLoading && leaves.length === 0 ? (
                        <div className="p-20 flex flex-col items-center justify-center">
                            <RefreshCw className="w-10 h-10 text-warm-400 animate-spin mb-4" />
                            <p className="text-ink-3 font-medium">Đang tải danh sách đơn...</p>
                        </div>
                    ) : filteredLeaves.length === 0 ? (
                        <div className="py-24 flex flex-col items-center justify-center text-ink-3">
                            <div className="w-20 h-20 bg-surface-2 rounded-full flex items-center justify-center mb-6">
                                <Inbox className="w-10 h-10 opacity-20" />
                            </div>
                            <h3 className="text-lg font-bold text-ink-1 mb-1">Không tìm thấy đơn nào</h3>
                            <p className="text-sm opacity-60">Hãy thử thay đổi bộ lọc hoặc từ khóa tìm kiếm</p>
                            {searchTerm === "" && activeTab === "ALL" && (
                                <button onClick={() => setIsModalOpen(true)} className="btn btn-warm btn-sm mt-6 font-bold">
                                    Gửi đơn đầu tiên ngay
                                </button>
                            )}
                        </div>
                    ) : (
                        <div className="divide-y divide-rule">
                            {filteredLeaves.map((leave) => (
                                <LeaveItem 
                                    key={leave.id} 
                                    leave={leave} 
                                    isAdmin={isAdmin}
                                    currentUserId={user?.id || 0}
                                    onApprove={approveLeave}
                                    onReject={rejectLeave}
                                    onCancel={cancelLeave}
                                />
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* Error Message */}
            {error && (
                <div className="fixed bottom-6 left-1/2 -translate-x-1/2 px-6 py-3 bg-red-600 text-white rounded-full shadow-2xl flex items-center gap-3 animate-fade-up z-toast">
                    <XCircle className="w-5 h-5" />
                    <span className="text-sm font-medium">{error}</span>
                    <button onClick={() => fetchLeaves()} className="ml-2 hover:underline text-xs opacity-80">Thử lại</button>
                </div>
            )}

            {/* Modal */}
            <LeaveFormModal
                isOpen={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                onSubmit={submitLeave}
                isSubmitting={isSubmitting}
            />
        </div>
    );
};
