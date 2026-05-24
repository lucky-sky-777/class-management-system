// src/features/home/pages/HomePage.tsx
import React, { useState, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { useHome } from "@features/home/hooks/useHome";
import {
  Lock,
  MoreVertical,
  ArrowRight,
  LogOut,
  Edit2,
  Trash2,
  Plus,
  AlertTriangle,
} from "lucide-react";
import { ClassPrivacy, ClassStatus, ToastType } from "@shared/domain/enums";
import { useAuth } from "@features/auth";
import type { ClassItems } from "@features/home/types";
import { useToastStore } from "@app/store";

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
  } = useHome();
  const { user } = useAuth();
  const myClasses = classes || [];
  const [openMenuId, setOpenMenuId] = useState<number | null>(null);
  const showToast = useToastStore((state) => state.showToast);

  useEffect(() => {
    const handleRefresh = () => {
      refresh();
    };
    window.addEventListener("refreshHomeClasses", handleRefresh);

    return () => {
      window.removeEventListener("refreshHomeClasses", handleRefresh);
    };
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
      if (
        openMenuId &&
        menuContainerRef.current &&
        !menuContainerRef.current.contains(event.target as Node)
      ) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [openMenuId]);

  const handleToggleMenu = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    setOpenMenuId(openMenuId === id ? null : id);
  };

  const handleEdit = (e: React.MouseEvent, item: ClassItems) => {
    e.stopPropagation();
    setOpenMenuId(null);
    setEditModal({
      isOpen: true,
      classId: item.id,
      name: item.name,
      description: item.description || "",
    });
  };

  const handleSaveEdit = async () => {
    if (!editModal.classId) return;

    if (!editModal.name.trim()) {
      showToast("Tên lớp không được để trống!", ToastType.WARNING); //Gọi Toast
      return;
    }

    setIsProcessing(true);
    try {
      await updateClassMutation(editModal.classId, {
        name: editModal.name,
        description: editModal.description,
      });
      setEditModal({ isOpen: false, classId: null, name: "", description: "" });
      showToast("Cập nhật thông tin lớp thành công!", ToastType.SUCCESS); // Gọi Toast
    } catch (err: unknown) {
      showToast("Lỗi khi cập nhật lớp: " + err, ToastType.ERROR); // Gọi Toast
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
        showToast("Đã xóa lớp học!", ToastType.SUCCESS); // Gọi Toast
      } else if (confirmModal.type === "leave") {
        await leaveClassMutation(confirmModal.classId);
        showToast("Đã rời khỏi lớp!", ToastType.SUCCESS); // Gọi Toast
      }
      setConfirmModal({ isOpen: false, type: null, classId: null });
    } catch (err: unknown) {
      showToast("Có lỗi xảy ra: " + err, ToastType.ERROR); // Gọi Toast
    } finally {
      setIsProcessing(false);
    }
  };

  const handleClassClick = (item: ClassItems) => {
    if (item.status === ClassStatus.JOINED) {
      navigate(`/class/${item.id}/diagram`);
      return;
    }
    showToast(
      "Yêu cầu tham gia của bạn đang chờ chủ nhóm duyệt.",
      ToastType.WARNING,
    );
  };

  return (
    <div className="w-full max-w-7xl px-6 py-6" ref={menuContainerRef}>
      {/* 1. Trạng thái đang tải (Loading) */}
      {isLoading && (
        <div className="flex flex-col items-center justify-center py-32">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--primary)]"></div>
          <p className="mt-4 text-[var(--ink-2)] text-sm italic font-medium">
            Đang đồng bộ dữ liệu lớp học...
          </p>
        </div>
      )}

      {/* 2. Trạng thái có danh sách lớp */}
      {!isLoading && myClasses.length > 0 && (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6">
          {myClasses.map((item) => {
            const isAdmin =
              user?.id &&
              item.owner_user_id &&
              String(user.id) === String(item.owner_user_id);

            return (
              <div
                key={item.id}
                onClick={() => handleClassClick(item)}
                className="group bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-xl)] overflow-hidden shadow-[var(--shadow-sm)] hover:shadow-[var(--shadow-lg)] transition-all duration-300 cursor-pointer flex flex-col h-[260px]"
              >
                {/* Banner lớp học */}
                <div
                  className={`relative h-[100px] p-4 transition-colors duration-300 ${
                    item.privacy === ClassPrivacy.PUBLIC
                      ? "bg-gradient-to-br from-[var(--primary)] to-[var(--primary-hover)]"
                      : "bg-gradient-to-br from-[var(--ink-1)] to-[var(--ink-3)]"
                  }`}
                >
                  <div className="flex justify-between items-start">
                    <div className="flex items-center min-w-0">
                      <h3 className="font-bold text-lg leading-tight truncate pr-2 group-hover:underline text-white min-w-0">
                        {item.name}
                      </h3>

                      <span className="text-[12px] font-bold uppercase px-2 py-0.5 rounded-full bg-white/15 text-white/80 border border-white/10">
                        {item.code}
                      </span>
                    </div>

                    {/* KHU VỰC MENU 3 CHẤM */}
                    <div className="relative">
                      <button
                        onClick={(e) => handleToggleMenu(e, item.id)}
                        className="text-white/70 hover:text-white hover:bg-white/20 transition-colors p-1 rounded-full"
                      >
                        <MoreVertical size={18} />
                      </button>

                      {/* DROPDOWN MENU */}
                      {openMenuId === item.id && (
                        <div className="absolute right-0 mt-2 w-40 bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-md)] shadow-[var(--shadow-xl)] py-1 z-50 animate-in fade-in zoom-in-95 duration-100">
                          {isAdmin ? (
                            <>
                              <button
                                onClick={(e) => handleEdit(e, item)}
                                className="flex items-center gap-2 w-full px-4 py-2 text-sm font-medium text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)] transition-colors text-left"
                              >
                                <Edit2 size={15} /> Chỉnh sửa
                              </button>
                              <button
                                onClick={(e) => handleDelete(e, item.id)}
                                className="flex items-center gap-2 w-full px-4 py-2 text-sm font-bold text-[var(--red-text)] hover:bg-[var(--red-fill)] transition-colors text-left"
                              >
                                <Trash2 size={15} /> Xóa lớp
                              </button>
                            </>
                          ) : (
                            <button
                              onClick={(e) => handleLeave(e, item.id)}
                              className="flex items-center gap-2 w-full px-4 py-2 text-sm font-bold text-[var(--red-text)] hover:bg-[var(--red-fill)] transition-colors text-left"
                            >
                              <LogOut size={15} /> Rời lớp
                            </button>
                          )}
                        </div>
                      )}
                    </div>
                  </div>
                  <p className="text-white/80 text-xs mt-1 truncate opacity-90">
                    {item.owner_display_name || "Giáo viên"}
                  </p>

                  {/* Avatar viết tắt chủ phòng */}
                  <div className="absolute -bottom-6 right-4 w-12 h-12 rounded-full bg-[var(--bg-surface)] shadow-[var(--shadow-md)] flex items-center justify-center border-4 border-[var(--bg-surface)] overflow-hidden">
                    <div className="w-full h-full bg-[var(--primary-fill)] flex items-center justify-center text-[var(--primary-text)] font-bold text-sm overflow-hidden">
                      {item.owner_avatar_url ? (
                        <img
                          src={item.owner_avatar_url}
                          alt={item.owner_display_name}
                          className="w-full h-full object-cover rounded-full"
                        />
                      ) : item.owner_display_name ? (
                        item.owner_display_name
                          .trim()
                          .split(" ")
                          .pop()
                          ?.charAt(0)
                          ?.toUpperCase()
                      ) : (
                        "G"
                      )}
                    </div>
                  </div>
                </div>

                {/* Nội dung bên dưới banner */}
                <div className="p-4 pt-8 flex-1 flex flex-col justify-between bg-[var(--bg-surface)]">
                  <div className="space-y-3">
                    <div className="flex items-center gap-2">
                      <span
                        className={`text-[10px] px-2.5 py-0.5 rounded-[var(--r-sm)] font-extrabold uppercase tracking-wider ${
                          item.privacy === ClassPrivacy.PUBLIC
                            ? "bg-[var(--green-fill)] text-[var(--green-text)] border border-[var(--green-border)]"
                            : "bg-[var(--amber-fill)] text-[var(--amber-text)] border border-[var(--amber-border)]"
                        }`}
                      >
                        {item.privacy === ClassPrivacy.PUBLIC
                          ? "Công khai"
                          : "Riêng tư"}
                      </span>
                    </div>

                    <div className="flex flex-col gap-2">
                      {item.status === ClassStatus.PENDING_REQUEST && (
                        <div className="flex items-center gap-2 text-xs text-[var(--amber-text)] font-medium">
                          <Lock size={14} />
                          <span>Chờ duyệt</span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* Nút vào lớp chân thẻ */}
                  <div className="pt-3 border-t border-[var(--rule)] flex justify-end">
                    <span className="text-xs font-bold text-[var(--primary)] group-hover:text-[var(--primary-hover)] flex items-center gap-1 group-hover:translate-x-1 transition-all">
                      VÀO LỚP <ArrowRight size={14} />
                    </span>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* 3. Trạng thái trống (Empty State) */}
      {!isLoading && myClasses.length === 0 && !error && (
        <div className="w-full flex flex-col items-center justify-center py-24">
          <div className="relative mb-8">
            <div className="w-32 h-32 bg-[var(--primary-fill)] rounded-full flex items-center justify-center">
              <span className="text-6xl">🏫</span>
            </div>
            <div className="absolute -bottom-2 -right-2 bg-[var(--bg-surface)] p-2 rounded-full shadow-[var(--shadow-lg)] border border-[var(--rule)]">
              <Plus className="text-[var(--primary-text)]" size={24} />
            </div>
          </div>
          <h2 className="text-2xl font-bold text-[var(--ink-1)]">
            Bắt đầu hành trình học tập
          </h2>
          <p className="text-[var(--ink-2)] mt-2 text-center max-w-sm font-medium">
            Bạn chưa tham gia lớp học nào. Hãy sử dụng tính năng trên Header để
            tham gia hoặc tạo lớp mới.
          </p>
        </div>
      )}

      {/* 4. Trạng thái lỗi (Error) */}
      {error && (
        <div className="text-center py-20 bg-[var(--red-fill)] rounded-[var(--r-xl)] border border-[var(--red-border)] mx-4">
          <p className="text-[var(--red-text)] font-bold text-lg">⚠️ {error}</p>
          <button
            onClick={() => window.location.reload()}
            className="mt-4 px-6 py-2 bg-[var(--bg-surface)] border border-[var(--red-border)] text-[var(--red-text)] rounded-[var(--r-md)] hover:bg-[var(--red-fill)] transition-colors shadow-[var(--shadow-sm)] text-sm font-bold"
          >
            THỬ LẠI
          </button>
        </div>
      )}

      {/* FORM XÁC NHẬN (CONFIRM MODAL) */}
      {confirmModal.isOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-in fade-in duration-200"
          onClick={() =>
            setConfirmModal({ isOpen: false, type: null, classId: null })
          }
        >
          <div
            className="bg-[var(--bg-surface)] w-full max-w-sm rounded-[var(--r-xl)] shadow-[var(--shadow-lg)] border border-[var(--rule)] overflow-hidden animate-in zoom-in-95 duration-200"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="p-6">
              <div
                className={`w-12 h-12 rounded-full flex items-center justify-center mb-4 ${
                  confirmModal.type === "delete"
                    ? "bg-[var(--red-fill)] text-[var(--red-text)]"
                    : "bg-[var(--amber-fill)] text-[var(--amber-text)]"
                }`}
              >
                <AlertTriangle size={24} />
              </div>

              <h3 className="text-lg font-bold text-[var(--ink-1)] mb-2">
                {confirmModal.type === "delete"
                  ? "Xóa lớp học"
                  : "Rời khỏi lớp học"}
              </h3>

              <p className="text-[var(--ink-2)] text-sm">
                {confirmModal.type === "delete"
                  ? "Bạn có chắc chắn muốn xóa lớp học này không? Toàn bộ dữ liệu của lớp sẽ bị xóa vĩnh viễn và không thể khôi phục."
                  : "Bạn có chắc chắn muốn rời khỏi lớp học này? Bạn sẽ không thể xem tài liệu của lớp trừ khi tham gia lại."}
              </p>
            </div>

            <div className="px-6 py-4 bg-[var(--bg-surface-2)] flex items-center justify-end gap-3 border-t border-[var(--rule)]">
              <button
                onClick={() =>
                  setConfirmModal({ isOpen: false, type: null, classId: null })
                }
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-[var(--ink-2)] hover:bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-md)] transition-colors disabled:opacity-50"
              >
                Hủy bỏ
              </button>

              <button
                onClick={handleConfirmAction}
                disabled={isProcessing}
                className={`px-4 py-2 text-sm font-bold text-white rounded-[var(--r-md)] transition-colors shadow-[var(--shadow-sm)] flex items-center gap-2 ${
                  confirmModal.type === "delete"
                    ? "bg-[var(--red-text)] hover:opacity-90"
                    : "bg-[var(--amber-text)] hover:opacity-90"
                } disabled:opacity-70`}
              >
                {isProcessing && (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                )}
                {confirmModal.type === "delete" ? "Xóa lớp" : "Rời lớp"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/*FORM CHỈNH SỬA LỚP HỌC (EDIT MODAL) */}
      {editModal.isOpen && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm animate-in fade-in duration-200"
          onClick={() => setEditModal({ ...editModal, isOpen: false })}
        >
          <div
            className="bg-[var(--bg-surface)] w-full max-w-md rounded-[var(--r-xl)] shadow-[var(--shadow-lg)] border border-[var(--rule)] overflow-hidden animate-in zoom-in-95 duration-200"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="px-6 py-4 border-b border-[var(--rule)] flex items-center gap-2">
              <div className="w-8 h-8 rounded-full bg-[var(--primary-fill)] flex items-center justify-center text-[var(--primary-text)]">
                <Edit2 size={16} />
              </div>
              <h3 className="text-lg font-bold text-[var(--ink-1)]">
                Chỉnh sửa lớp học
              </h3>
            </div>

            <div className="p-6 space-y-4">
              <div>
                <label className="block text-sm font-bold text-[var(--ink-1)] mb-1">
                  Tên lớp <span className="text-[var(--red-text)]">*</span>
                </label>
                <input
                  type="text"
                  value={editModal.name}
                  onChange={(e) =>
                    setEditModal({ ...editModal, name: e.target.value })
                  }
                  className="w-full px-3 py-2 border border-[var(--rule-md)] rounded-[var(--r-md)] focus:ring-2 focus:ring-[var(--primary)] focus:border-[var(--primary)] outline-none transition-all bg-[var(--bg-surface)] text-[var(--ink-1)]"
                  placeholder="Nhập tên lớp..."
                />
              </div>

              <div>
                <label className="block text-sm font-bold text-[var(--ink-1)] mb-1">
                  Mô tả
                </label>
                <textarea
                  value={editModal.description}
                  onChange={(e) =>
                    setEditModal({ ...editModal, description: e.target.value })
                  }
                  className="w-full px-3 py-2 border border-[var(--rule-md)] rounded-[var(--r-md)] focus:ring-2 focus:ring-[var(--primary)] focus:border-[var(--primary)] outline-none transition-all bg-[var(--bg-surface)] text-[var(--ink-1)]"
                  rows={3}
                  placeholder="Thêm mô tả cho lớp học..."
                ></textarea>
              </div>
            </div>

            <div className="px-6 py-4 bg-[var(--bg-surface-2)] flex items-center justify-end gap-3 border-t border-[var(--rule)]">
              <button
                onClick={() => setEditModal({ ...editModal, isOpen: false })}
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-[var(--ink-2)] hover:bg-[var(--bg-surface)] border border-[var(--rule)] rounded-[var(--r-md)] transition-colors disabled:opacity-50"
              >
                Hủy bỏ
              </button>

              <button
                onClick={handleSaveEdit}
                disabled={isProcessing}
                className="px-4 py-2 text-sm font-bold text-white bg-[var(--primary)] hover:bg-[var(--primary-hover)] rounded-[var(--r-md)] transition-colors shadow-[var(--shadow-sm)] flex items-center gap-2 disabled:opacity-70"
              >
                {isProcessing && (
                  <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin"></div>
                )}
                Lưu thay đổi
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
};
