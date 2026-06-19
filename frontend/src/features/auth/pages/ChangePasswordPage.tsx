import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Lock,
  KeyRound,
  ShieldCheck,
  Loader2,
  ArrowLeft,
  Eye,
  EyeOff,
} from "lucide-react";
import { useAuthInternal } from "@features/auth/hooks/useAuthInternal";

export const ChangePasswordPage = () => {
  const navigate = useNavigate();
  const {
    changePassword,
    isLoading: apiLoading,
    error: apiError,
  } = useAuthInternal();

  const [formData, setFormData] = useState({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
  });
  const [show, setShow] = useState({ old: false, new: false, confirm: false });
  const [localError, setLocalError] = useState("");

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLocalError("");

    if (formData.newPassword !== formData.confirmPassword) {
      setLocalError("Mật khẩu mới không khớp!");
      return;
    }

    const success = await changePassword(
      formData.oldPassword,
      formData.newPassword,
    );
    if (success) {
      alert("Đổi mật khẩu thành công!");
      navigate("/");
    }
  };

  // Helper render input để code gọn gàng
  const renderInput = (
    label: string,
    field: "oldPassword" | "newPassword" | "confirmPassword",
    icon: React.ReactNode,
    typeKey: "old" | "new" | "confirm",
  ) => (
    <div className="space-y-1.5">
      <label className="text-xs font-bold text-[var(--ink-2)] uppercase tracking-wider">
        {label}
      </label>
      <div className="relative">
        <div className="absolute left-3 top-3 text-[var(--ink-3)]">{icon}</div>
        <input
          type={show[typeKey] ? "text" : "password"}
          required
          className="input-field w-full pl-10 pr-10 py-2.5 rounded-[var(--r-md)] text-sm outline-none"
          value={formData[field]}
          onChange={(e) =>
            setFormData({ ...formData, [field]: e.target.value })
          }
        />
        <button
          type="button"
          onClick={() => setShow({ ...show, [typeKey]: !show[typeKey] })}
          className="absolute right-3 top-3 text-[var(--ink-3)] hover:text-[var(--ink-1)]"
        >
          {show[typeKey] ? <EyeOff size={16} /> : <Eye size={16} />}
        </button>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen flex items-center justify-center bg-[var(--bg-paper)] p-4">
      <div className="w-full max-w-md bg-[var(--bg-surface)] p-8 rounded-[var(--r-lg)] shadow-[var(--shadow-lg)] border border-[var(--rule)]">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center gap-2 text-sm text-[var(--ink-2)] hover:text-[var(--ink-1)] mb-6 transition-colors"
        >
          <ArrowLeft size={16} /> Quay lại
        </button>

        <h2 className="text-2xl font-black text-[var(--ink-1)] mb-6">
          Đổi mật khẩu
        </h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          {(localError || apiError) && (
            <div className="p-3 text-xs font-bold text-[var(--red-text)] bg-[var(--red-fill)] rounded-md border border-[var(--red-border)]">
              {localError || apiError}
            </div>
          )}

          {renderInput(
            "Mật khẩu hiện tại",
            "oldPassword",
            <Lock size={18} />,
            "old",
          )}
          {renderInput(
            "Mật khẩu mới",
            "newPassword",
            <KeyRound size={18} />,
            "new",
          )}
          {renderInput(
            "Xác nhận mật khẩu mới",
            "confirmPassword",
            <ShieldCheck size={18} />,
            "confirm",
          )}

          <button
            type="submit"
            disabled={apiLoading}
            className="w-full mt-4 py-3 bg-[var(--primary)] text-white font-bold rounded-[var(--r-md)] hover:opacity-90 flex items-center justify-center gap-2 transition-all"
          >
            {apiLoading ? (
              <Loader2 size={18} className="animate-spin" />
            ) : (
              "Xác nhận đổi mật khẩu"
            )}
          </button>
        </form>
      </div>
    </div>
  );
};
