import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useHome } from "@features/home/hooks/useHome";
import { Edit2, AlertTriangle} from "lucide-react";
import { ClassStatus, ToastType } from "@shared/domain/enums";
import { useAuth } from "@features/auth";
import type { ClassItems } from "@features/home/types";
import { useToastStore } from "@app/store";
import { GroupBanner } from "@/features/home/components/GroupBanner";
import { GroupCard } from "@/features/home/components/GroupCard";
import { SuggestedGroups } from "@/features/home/components/SuggestedGroups";
import { RecentDocuments } from "@/features/home/components/RecentDocuments";
import { useSuggestedClasses } from "@features/home/hooks/useSuggestedClasses";

export const HomePage = () => {
  const navigate = useNavigate();
  const {
    classes,
    isLoading,
    error,
    refresh,
    deleteClassMutation,
    leaveClassMutation,
    updateClassMutation,
    joinClassMutation,
  } = useHome();
  const { suggestedClasses, isLoading: isSuggestedLoading } = useSuggestedClasses();
  const { user } = useAuth();
  const myClasses = classes || [];
  const [openMenuId, setOpenMenuId] = useState<number | null>(null);
  // State quản lý hiển thị tối đa 6 lớp
  const [showAll, setShowAll] = useState(false);
  const showClasses = showAll ? myClasses : myClasses.slice(0, 6);
  const showToast = useToastStore((state) => state.showToast);

  useEffect(() => {
    const handleRefresh = () => refresh();
    window.addEventListener("refreshHomeClasses", handleRefresh);
    return () => window.removeEventListener("refreshHomeClasses", handleRefresh);
  }, [refresh]);

  const [confirmModal, setConfirmModal] = useState<{
    isOpen: boolean;
    type: "delete" | "leave" | null;
    classId: number | null;
  }>({ isOpen: false, type: null, classId: null });

  const [editModal, setEditModal] = useState<{
    isOpen: boolean;
    classId: number | null;
    name: string;
    description: string;
  }>({ isOpen: false, classId: null, name: "", description: "" });

  const [isProcessing, setIsProcessing] = useState(false);
  const menuContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (openMenuId && menuContainerRef.current && !menuContainerRef.current.contains(event.target as Node)) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [openMenuId]);

  const handleJoinSuggested = async (id: number, code: string) => {
    try {
      await joinClassMutation(code);
      showToast("Gửi yêu cầu tham gia thành công!", ToastType.SUCCESS);
    } catch (error: any) {
      showToast(error.message || "Không thể tham gia nhóm này", ToastType.ERROR);
      throw error; 
    }
  };

  const handleToggleMenu = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    setOpenMenuId(openMenuId === id ? null : id);
  };

  const handleEdit = (e: React.MouseEvent, item: ClassItems) => {
    e.stopPropagation();
    setOpenMenuId(null);
    setEditModal({ isOpen: true, classId: item.id, name: item.name, description: item.description || "" });
  };

  const handleSaveEdit = async () => {
    if (!editModal.classId) return;
    if (!editModal.name.trim()) {
      showToast("Tên lớp không được để trống!", ToastType.WARNING);
      return;
    }
    setIsProcessing(true);
    try {
      await updateClassMutation(editModal.classId, { name: editModal.name, description: editModal.description });
      setEditModal({ isOpen: false, classId: null, name: "", description: "" });
      showToast("Cập nhật thông tin lớp thành công!", ToastType.SUCCESS);
    } catch (err: unknown) {
      showToast("Lỗi khi cập nhật lớp: " + err, ToastType.ERROR);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleDelete = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    setOpenMenuId(null);
    setConfirmModal({ isOpen: true, type: "delete", classId: id });
  };

  const handleLeave = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    setOpenMenuId(null);
    setConfirmModal({ isOpen: true, type: "leave", classId: id });
  };

  const handleConfirmAction = async () => {
    if (!confirmModal.classId || !confirmModal.type) return;
    setIsProcessing(true);
    try {
      if (confirmModal.type === "delete") {
        await deleteClassMutation(confirmModal.classId);
        showToast("Đã xóa lớp học!", ToastType.SUCCESS);
      } else if (confirmModal.type === "leave") {
        await leaveClassMutation(confirmModal.classId);
        showToast("Đã rời khỏi lớp!", ToastType.SUCCESS);
      }
      setConfirmModal({ isOpen: false, type: null, classId: null });
    } catch (err: unknown) {
      showToast("Có lỗi xảy ra: " + err, ToastType.ERROR);
    } finally {
      setIsProcessing(false);
    }
  };

  const handleClassClick = (item: ClassItems) => {
    if (item.status === ClassStatus.JOINED) {
      navigate(`/class/${item.id}/diagram`);
      return;
    }
    showToast("Yêu cầu tham gia của bạn đang chờ chủ nhóm duyệt.", ToastType.WARNING);
  };

  return (
    <div className="w-full max-w-7xl mx-auto px-6 py-6" ref={menuContainerRef}>
      
      {/* 1. Header Banner */}
      <GroupBanner />

      {/* 2. Main Layout (Chia 2 Cột) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Cột Trái (Chiếm 2/3) */}
        <div className="lg:col-span-2 flex flex-col gap-8">
          <section>
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-xl font-bold text-[var(--ink-1)]">Nhóm học của bạn</h2>
              {myClasses.length > 6 && (
                <button 
                  onClick={() => setShowAll(!showAll)}
                  className="text-sm text-[var(--primary)] font-bold hover:underline"
                >
                  {showAll ? "Thu gọn" : "Xem tất cả"}
                </button>
              )}
            </div>

            {isLoading && (
              <div className="flex flex-col items-center justify-center py-20">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-[var(--primary)]"></div>
                <p className="mt-3 text-[var(--ink-2)] text-sm">Đang đồng bộ dữ liệu lớp học...</p>
              </div>
            )}

            {!isLoading && myClasses.length > 0 && (
              <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
                {showClasses.map((item) => {
                  const isAdmin = user?.id && item.owner_user_id && String(user.id) === String(item.owner_user_id);
                  return (
                    <GroupCard
                      key={item.id}
                      item={item}
                      isAdmin={Boolean(isAdmin)}
                      isOpenMenu={openMenuId === item.id}
                      onToggleMenu={(e) => handleToggleMenu(e, item.id)}
                      onEdit={(e) => handleEdit(e, item)}
                      onDelete={(e) => handleDelete(e, item.id)}
                      onLeave={(e) => handleLeave(e, item.id)}
                      onClick={() => handleClassClick(item)}
                    />
                  );
                })}
              </div>
            )}

            {!isLoading && myClasses.length === 0 && !error && (
              <div className="text-center py-12 bg-[var(--bg-surface)] rounded-[var(--r-xl)] border border-[var(--rule)] p-6">
                <div className="relative inline-block mb-4">
                  <div className="w-16 h-16 bg-[var(--primary-fill)] rounded-[var(--r-full)] flex items-center justify-center text-3xl">🏫</div>
                </div>
                <p className="text-[var(--ink-2)] text-sm font-medium">Bạn chưa tham gia nhóm học nào.</p>
              </div>
            )}
          </section>

          {/* Gợi ý cho bạn */}
          <SuggestedGroups 
            suggestions={suggestedClasses}
            isLoading={isSuggestedLoading}
            onJoin={handleJoinSuggested}
          />
        </div>

        {/* Cột Phải (Chiếm 1/3) */}
        <RecentDocuments />
      </div>

      {/* 3. Modals */}
      {confirmModal.isOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[var(--bg-overlay)] backdrop-blur-sm"
          onClick={() => setConfirmModal({ isOpen: false, type: null, classId: null })}
        >
          <div
            className="bg-[var(--bg-surface)] w-full max-w-sm rounded-[var(--r-xl)] shadow-[var(--shadow-lg)] border border-[var(--rule)] overflow-hidden animate-in zoom-in-95"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6">
              <div className={`w-12 h-12 rounded-[var(--r-full)] flex items-center justify-center mb-4 ${confirmModal.type === "delete" ? "bg-[var(--red-fill)] text-[var(--red-text)]" : "bg-[var(--amber-fill)] text-[var(--amber-text)]"}`}>
                <AlertTriangle size={24} />
              </div>
              <h3 className="text-lg font-bold text-[var(--ink-1)] mb-2">
                {confirmModal.type === "delete" ? "Xóa lớp học" : "Rời khỏi lớp học"}
              </h3>
              <p className="text-[var(--ink-2)] text-sm">
                {confirmModal.type === "delete"
                  ? "Bạn có chắc chắn muốn xóa lớp học này không? Dữ liệu sẽ bị xóa vĩnh viễn."
                  : "Bạn có chắc chắn muốn rời khỏi lớp học này?"}
              </p>
            </div>
            <div className="px-6 py-4 bg-[var(--bg-surface-2)] flex items-center justify-end gap-3 border-t border-[var(--rule)]">
              <button
                onClick={() => setConfirmModal({ isOpen: false, type: null, classId: null })}
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-[var(--ink-2)] hover:bg-[var(--bg-surface)] border border-[var(--rule-md)] rounded-[var(--r-md)] transition-colors"
              >
                Hủy bỏ
              </button>
              <button
                onClick={handleConfirmAction}
                disabled={isProcessing}
                className={`px-4 py-2 text-sm font-bold text-white rounded-[var(--r-md)] transition-colors ${confirmModal.type === "delete" ? "bg-[var(--red)] hover:bg-[var(--red-text)]" : "bg-[var(--amber)] hover:bg-[var(--amber-text)]"}`}
              >
                {confirmModal.type === "delete" ? "Xóa lớp" : "Rời lớp"}
              </button>
            </div>
          </div>
        </div>
      )}

      {editModal.isOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-[var(--bg-overlay)] backdrop-blur-sm"
          onClick={() => setEditModal({ ...editModal, isOpen: false })}
        >
          <div
            className="bg-[var(--bg-surface)] w-full max-w-md rounded-[var(--r-xl)] shadow-[var(--shadow-lg)] border border-[var(--rule)] overflow-hidden animate-in zoom-in-95"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="px-6 py-4 border-b border-[var(--rule)] flex items-center gap-2">
              <div className="w-8 h-8 rounded-[var(--r-full)] bg-[var(--primary-fill)] flex items-center justify-center text-[var(--primary-text)]">
                <Edit2 size={16} />
              </div>
              <h3 className="text-lg font-bold text-[var(--ink-1)]">Chỉnh sửa lớp học</h3>
            </div>
            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-bold text-[var(--ink-1)] mb-1">Tên lớp *</label>
                <input
                  type="text"
                  value={editModal.name}
                  onChange={(e) => setEditModal({ ...editModal, name: e.target.value })}
                  className="w-full px-3 py-2 bg-[var(--bg-surface)] border border-[var(--rule-md)] text-[var(--ink-1)] rounded-[var(--r-md)] focus:ring-2 focus:ring-[var(--primary)] outline-none transition-all"
                />
              </div>
              <div>
                <label className="block text-sm font-bold text-[var(--ink-1)] mb-1">Mô tả</label>
                <textarea
                  value={editModal.description}
                  onChange={(e) => setEditModal({ ...editModal, description: e.target.value })}
                  className="w-full px-3 py-2 bg-[var(--bg-surface)] border border-[var(--rule-md)] text-[var(--ink-1)] rounded-[var(--r-md)] focus:ring-2 focus:ring-[var(--primary)] outline-none transition-all"
                  rows={3}
                ></textarea>
              </div>
            </div>
            <div className="px-6 py-4 bg-[var(--bg-surface-2)] flex items-center justify-end gap-3 border-t border-[var(--rule)]">
              <button
                onClick={() => setEditModal({ ...editModal, isOpen: false })}
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-[var(--ink-2)] hover:bg-[var(--bg-surface)] border border-[var(--rule-md)] rounded-[var(--r-md)] transition-colors"
              >
                Hủy bỏ
              </button>
              <button
                onClick={handleSaveEdit}
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-[var(--bg-surface)] bg-[var(--primary)] hover:bg-[var(--primary-hover)] rounded-[var(--r-md)] transition-colors shadow-[var(--shadow-sm)]"
              >
                Lưu thay đổi
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};