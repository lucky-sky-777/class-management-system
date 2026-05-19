import { Clock, X, CheckCircle2, XCircle, Info } from "lucide-react";
import { ToastType } from "@shared/domain/enums";
import { useToastStore } from "@app/store/useToastStore"

export const GlobalToast = () => {
  const { isOpen, message, type, hideToast } = useToastStore();

  if (!isOpen) return null;

  return (
    <div className="fixed bottom-6 md:bottom-10 left-4 right-4 md:left-1/2 md:-translate-x-1/2 md:right-auto md:w-max z-[9999] flex items-center gap-3 px-5 py-4 
      bg-[var(--bg-surface)] backdrop-blur-2xl 
      rounded-[var(--r-xl)] shadow-[var(--shadow-lg)] 
      border border-[var(--rule)] 
      animate-in slide-in-from-bottom-6 fade-in zoom-in-95 duration-300 pointer-events-auto">
      
      {/* Icon Container: Dùng màu fill theo biến global */}
      <div className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 border ${
        type === ToastType.SUCCESS ? "bg-[var(--green-fill)] border-[var(--green-border)] text-[var(--green-text)]" :
        type === ToastType.ERROR ? "bg-[var(--red-fill)] border-[var(--red-border)] text-[var(--red-text)]" :
        type === ToastType.WARNING ? "bg-[var(--amber-fill)] border-[var(--amber-border)] text-[var(--amber-text)]" :
        "bg-[var(--sky-fill)] border-[var(--sky-border)] text-[var(--sky-text)]"
      }`}>
        {type === ToastType.SUCCESS && <CheckCircle2 size={18} />}
        {type === ToastType.ERROR && <XCircle size={18} />}
        {type === ToastType.WARNING && <Clock size={18} className="animate-pulse" />}
        {type === ToastType.INFO && <Info size={18} />}
      </div>
      
      {/* Text nội dung: Dùng màu ink-1 chuẩn */}
      <span className="text-sm font-bold text-[var(--ink-1)] flex-1 leading-snug">
        {message}
      </span>

      {/* Nút đóng: Dùng màu ink-3 */}
      <button 
        onClick={(e) => {
          e.stopPropagation();
          hideToast();
        }}
        className="shrink-0 text-[var(--ink-3)] hover:text-[var(--ink-1)] hover:bg-[var(--bg-surface-2)] transition-colors rounded-full p-1.5"
      >
        <X size={18} />
      </button>
    </div>
  );
};