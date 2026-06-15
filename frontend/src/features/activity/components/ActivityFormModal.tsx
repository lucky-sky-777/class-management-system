import React, { useState, useEffect } from "react";
import { Modal } from "@shared/components/ui/Modal";
import type { Activity, CreateActivityDTO } from "@features/activity/types";

interface ActivityFormModalProps {
    isOpen: boolean;
    onClose: () => void;
    onSubmit: (dto: CreateActivityDTO) => Promise<boolean>;
    editingActivity?: Activity | null;
    isSubmitting?: boolean;
}

const EMPTY_FORM: CreateActivityDTO = {
    name: "",
    description: "",
    startAt: "",
    endAt: "",
    registrationEndAt: "",
    location: "",
    point: undefined,
    isMandatory: false,
};

// Helper: ISO → datetime-local input value (YYYY-MM-DDTHH:mm)
const toInputValue = (iso: string | null | undefined) => {
    if (!iso) return "";
    return iso.slice(0, 16); // "2026-06-10T07:00"
};

export const ActivityFormModal: React.FC<ActivityFormModalProps> = ({
    isOpen,
    onClose,
    onSubmit,
    editingActivity,
    isSubmitting = false,
}) => {
    const [form, setForm] = useState<CreateActivityDTO>(EMPTY_FORM);

    // Điền form khi edit
    useEffect(() => {
        if (editingActivity) {
            // eslint-disable-next-line react-hooks/set-state-in-effect
            setForm({
                name: editingActivity.name,
                description: editingActivity.description ?? "",
                startAt: toInputValue(editingActivity.startAt),
                endAt: toInputValue(editingActivity.endAt),
                registrationEndAt: toInputValue(editingActivity.registrationEndAt),
                location: editingActivity.location ?? "",
                point: editingActivity.point ?? undefined,
                isMandatory: editingActivity.isMandatory,
            });
        } else {
            setForm(EMPTY_FORM);
        }
    }, [editingActivity, isOpen]);

    const set = <K extends keyof CreateActivityDTO>(key: K, value: CreateActivityDTO[K]) =>
        setForm((prev) => ({ ...prev, [key]: value }));

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        // Chuẩn hóa: chuyển datetime-local value → ISO string
        const dto: CreateActivityDTO = {
            ...form,
            startAt: form.startAt ? new Date(form.startAt).toISOString() : undefined,
            endAt: form.endAt ? new Date(form.endAt).toISOString() : undefined,
            registrationEndAt: form.registrationEndAt ? new Date(form.registrationEndAt).toISOString() : undefined,
        };
        const success = await onSubmit(dto);
        if (success) onClose();
    };

    const isEditing = !!editingActivity;

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            title={isEditing ? "Chỉnh sửa hoạt động" : "Tạo hoạt động mới"}
        >
            <form onSubmit={handleSubmit} className="space-y-5">
                {/* Tên hoạt động */}
                <div className="space-y-1.5">
                    <label className="input-label">
                        Tên hoạt động <span className="text-ink-red-text">*</span>
                    </label>
                    <div className="input-field">
                        <input
                            type="text"
                            required
                            placeholder="Ví dụ: Chiến dịch Mùa Hè Xanh 2026"
                            value={form.name}
                            onChange={(e) => set("name", e.target.value)}
                        />
                    </div>
                </div>

                {/* Mô tả */}
                <div className="space-y-1.5">
                    <label className="input-label">Mô tả</label>
                    <div className="input-field min-h-[96px] items-start">
                        <textarea
                            rows={3}
                            placeholder="Thông tin chi tiết về hoạt động..."
                            value={form.description}
                            onChange={(e) => set("description", e.target.value)}
                            className="w-full resize-none leading-relaxed"
                        />
                    </div>
                </div>

                {/* Thời gian */}
                
                    <div className="space-y-1.5">
                        <label className="input-label">Bắt đầu</label>
                        <div className="input-field">
                            <input
                                type="datetime-local"
                                value={form.startAt}
                                onChange={(e) => set("startAt", e.target.value)}
                            />
                        </div>
                    </div>
                
                
                    <div className="space-y-1.5">
                        <label className="input-label">Kết thúc</label>
                        <div className="input-field">
                            <input
                                type="datetime-local"
                                value={form.endAt}
                                onChange={(e) => set("endAt", e.target.value)}
                            />
                        </div>
                    </div>

                    <div className="space-y-1.5">
                        <label className="input-label">Hạn chót đăng ký</label>
                        <div className="input-field">
                            <input
                                type="datetime-local"
                                value={form.registrationEndAt || ""}
                                onChange={(e) => set("registrationEndAt", e.target.value)}
                            />
                        </div>
                    </div>
                
                {/* Địa điểm & Điểm */}
                <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-1.5">
                        <label className="input-label">Địa điểm</label>
                        <div className="input-field">
                            <input
                                type="text"
                                placeholder="Hội trường A, Phòng B1-02..."
                                value={form.location}
                                onChange={(e) => set("location", e.target.value)}
                            />
                        </div>
                    </div>
                    <div className="space-y-1.5">
                        <label className="input-label">Điểm rèn luyện</label>
                        <div className="input-field">
                            <input
                                type="number"
                                min={0}
                                max={100}
                                placeholder="10"
                                value={form.point ?? ""}
                                onChange={(e) =>
                                    set("point", e.target.value === "" ? undefined : Number(e.target.value))
                                }
                            />
                        </div>
                    </div>
                </div>

                {/* Bắt buộc */}
                <label className="flex items-center gap-3 cursor-pointer select-none py-3 px-4 rounded-lg border border-rule hover:bg-surface-2 transition-colors">
                    <input
                        type="checkbox"
                        checked={form.isMandatory}
                        onChange={(e) => set("isMandatory", e.target.checked)}
                        className="w-4 h-4 accent-warm-400 rounded"
                    />
                    <div>
                        <p className="text-sm font-semibold text-ink-1">Hoạt động bắt buộc</p>
                        <p className="text-xs text-ink-3">Tất cả thành viên bắt buộc phải tham gia</p>
                    </div>
                </label>

                {/* Actions */}
                <div className="flex gap-3 pt-2">
                    <button
                        type="button"
                        onClick={onClose}
                        className="btn btn-secondary flex-1 font-bold"
                    >
                        Hủy
                    </button>
                    <button
                        type="submit"
                        disabled={isSubmitting}
                        className={`btn btn-warm flex-1 font-bold shadow-lg shadow-warm-400/20 ${
                            isSubmitting ? "opacity-70 cursor-not-allowed" : ""
                        }`}
                    >
                        {isSubmitting ? (
                            <>
                                <div className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                                Đang lưu...
                            </>
                        ) : isEditing ? (
                            "Lưu thay đổi"
                        ) : (
                            "Tạo hoạt động"
                        )}
                    </button>
                </div>
            </form>
        </Modal>
    );
};
