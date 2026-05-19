// src/features/home/components/JoinClassModal.tsx
import React, { useState } from "react";
import { useHome } from "@features/home/hooks/useHome";
import { Modal } from "@shared/components/ui/Modal";
import type { JoinClassResult } from "@features/home/types";
import { useToastStore } from "@app/store";
import { ToastType } from "@shared/domain/enums";

interface JoinClassModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSuccess: () => void;
}

export const JoinClassModal = ({
  isOpen,
  onClose,
  onSuccess,
}: JoinClassModalProps) => {
  const { joinClassMutation } = useHome();
  const [step, setStep] = useState<"INPUT" | "PENDING" | "SUCCESS">("INPUT");
  const [code, setCode] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // Định kiểu dữ liệu cụ thể cho state foundClass thay vì object rỗng
  const [foundClass, setFoundClass] = useState<JoinClassResult | null>(null);

  const showToast = useToastStore((state) => state.showToast);

  if (!isOpen) return null;

  const handleClose = () => {
    onClose();
    setTimeout(() => {
      setStep("INPUT");
      setCode("");
      setError("");
      setFoundClass(null);
    }, 300);
  };

  const handleJoin = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!code.trim()) return;

    setLoading(true);
    setError("");

    try {
      const result = await joinClassMutation(code);
      const classData =
        (result as { data?: JoinClassResult })?.data ||
        (result as JoinClassResult);

      const parsedClass: JoinClassResult = classData || { name: `Mã: ${code}` };
      setFoundClass(parsedClass);

      // 1. Phân định rõ ràng điều kiện
      const isPending =
        parsedClass.type === "REQUESTED" ||
        parsedClass.userJoinStatus === "pending" ||
        parsedClass.status === "PENDING_REQUEST";

      const isAlreadyJoined =
        parsedClass.status === "JOINED" ||
        parsedClass.userJoinStatus === "joined";

      // 2. LOG CÁI NÀY ĐỂ XEM THỰC TẾ API TRẢ VỀ GÌ
      console.log("DEBUG_STATUS:", { isPending, isAlreadyJoined, parsedClass });

      // 3. Logic ưu tiên (Cực kỳ quan trọng: Phải check PENDING trước)
      if (isPending) {
        // Nếu đang chờ duyệt thì cho nó vào luồng PENDING, KHÔNG XÉT TIẾP
        setStep("PENDING");
      } else if (isAlreadyJoined) {
        // Chỉ khi không phải Pending thì mới xét tới Joined
        setStep("SUCCESS");
        showToast("Bạn đã là thành viên của lớp học này rồi!", ToastType.INFO);
        setTimeout(() => {
          onSuccess();
          handleClose();
        }, 1000);
      } else {
        // Trường hợp còn lại: Tham gia mới thành công
        setStep("SUCCESS");
        showToast("Chào mừng bạn đã gia nhập lớp học!", ToastType.SUCCESS);
        setTimeout(() => {
          onSuccess();
          handleClose();
        }, 1500);
      }
    } catch (err: unknown) {
      const apiError = err as {
        message?: string;
        response?: { status?: number; data?: { message?: string } };
      };

      const status = apiError.response?.status;
      const rawMessage = apiError.message || apiError.response?.data?.message || "";
      const lowerMsg = rawMessage.toLowerCase();

      // DEBUG: Thêm dòng này để nhìn tận mắt Backend gửi gì
      console.log("DEBUG_ERROR_MESSAGE:", lowerMsg);

      let vietnameseMessage = "Có lỗi xảy ra, vui lòng thử lại.";

      // SỬA CHỖ NÀY: Thêm 'user exists' vào danh sách kiểm tra
      if (lowerMsg.includes("not found") || lowerMsg.includes("404")) {
        vietnameseMessage = "Mã lớp học không tồn tại.";
      } else if (
        lowerMsg.includes("already") ||
        lowerMsg.includes("joined") ||
        lowerMsg.includes("user exists") // <--- THÊM CỤM NÀY VÀO
      ) {
        vietnameseMessage = "Bạn đã là thành viên của lớp học này rồi.";
      } else if (
        status === 409 || 
        lowerMsg.includes("request exists") || 
        lowerMsg.includes("pending")
      ) {
        setStep("PENDING");
        vietnameseMessage = "Bạn đã gửi yêu cầu tham gia lớp này trước đó rồi.";
        showToast(vietnameseMessage, ToastType.INFO);
        return; // Dừng lại ở đây, không hiện lỗi Error nữa
      }

      showToast(vietnameseMessage, ToastType.ERROR);
      setError(vietnameseMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} title="Tham gia lớp học">
      <div className="w-full max-w-[340px] mx-auto animate-in fade-in zoom-in duration-200">
        {/* BƯỚC 1: NHẬP MÃ */}
        {step === "INPUT" && (
          <>
            <p className="text-sm text-[var(--ink-2)] mb-4 text-center">
              Nhập mã lớp học để tham gia ngay.
            </p>
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
                  className="w-full border border-[var(--rule)] bg-[var(--bg-surface-2)] rounded-[var(--r-xl)] py-2.5 px-4 focus:ring-2 focus:ring-[var(--primary-border)] focus:border-[var(--primary-text)] outline-none font-bold text-base text-center tracking-widest uppercase placeholder:normal-case placeholder:text-[var(--ink-4)] placeholder:font-medium transition-all text-[var(--ink-1)]"
                  autoFocus
                />
                {error && (
                  <p className="text-[var(--red-text)] text-[11px] font-medium mt-1.5 text-center">
                    {error}
                  </p>
                )}
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={handleClose}
                  className="flex-1 py-2 bg-[var(--bg-surface-2)] hover:bg-[var(--bg-surface-3)] text-[var(--ink-2)] text-sm font-semibold rounded-[var(--r-md)] transition-colors"
                >
                  Hủy
                </button>
                <button
                  type="submit"
                  disabled={loading || !code.trim()}
                  // Đồng bộ nút bấm theo tông hệ thống: primary hành động, disabled làm mờ
                  className="flex-[2] py-2 bg-[var(--primary)] hover:bg-[var(--primary-hover)] text-white text-sm font-semibold rounded-[var(--r-md)] shadow-[var(--shadow-sm)] disabled:opacity-50 transition-colors"
                >
                  {loading ? "ĐANG KIỂM TRA..." : "THAM GIA"}
                </button>
              </div>
            </form>
          </>
        )}

        {/* BƯỚC 2: CHỜ DUYỆT */}
        {step === "PENDING" && (
          <div className="text-center py-2">
            <div className="w-12 h-12 bg-[var(--amber-fill)] text-[var(--amber-text)] rounded-full flex items-center justify-center mx-auto mb-3 text-xl shadow-inner">
              ⏳
            </div>
            <h2 className="text-lg font-bold text-[var(--ink-1)] mb-1">
              Đã gửi yêu cầu
            </h2>
            <p className="text-sm text-[var(--ink-2)] mb-5 leading-relaxed">
              Bạn đã yêu cầu tham gia lớp{" "}
              <b className="text-[var(--ink-1)]">{foundClass?.name}</b>. Vui
              lòng chờ giáo viên duyệt.
            </p>
            <button
              onClick={handleClose}
              className="w-full py-2 bg-[var(--bg-surface-2)] hover:bg-[var(--bg-surface-3)] text-[var(--ink-1)] text-sm font-semibold rounded-[var(--r-md)] transition-colors"
            >
              Đóng
            </button>
          </div>
        )}

        {/* BƯỚC 3: THÀNH CÔNG */}
        {step === "SUCCESS" && (
          <div className="text-center py-2">
            <div className="w-12 h-12 bg-[var(--green-fill)] text-[var(--green-text)] rounded-full flex items-center justify-center mx-auto mb-3 text-xl shadow-inner">
              ✅
            </div>
            <h2 className="text-lg font-bold text-[var(--ink-1)] mb-1">
              Thành công!
            </h2>
            <p className="text-sm text-[var(--ink-2)]">
              Chào mừng đến với lớp{" "}
              <b className="text-[var(--ink-1)]">{foundClass?.name}</b>.
            </p>
          </div>
        )}
      </div>
    </Modal>
  );
};
