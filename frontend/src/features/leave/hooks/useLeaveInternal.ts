import { useState, useEffect, useCallback } from "react";
import type { ID } from "@shared/utils/common";
import { leaveAPI } from "../api";
import type { LeaveRequest, CreateLeaveRequestDTO } from "../types";

/**
 * Hook nội bộ xử lý logic nghiệp vụ cho module Nghỉ phép
 * @param classId ID của lớp học hiện tại
 */
export const useLeaveInternal = (classId: ID) => {
    const [leaves, setLeaves] = useState<LeaveRequest[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    /**
     * Tải danh sách đơn nghỉ phép
     */
    const fetchLeaves = useCallback(async () => {
        if (!classId) return;
        
        setIsLoading(true);
        setError(null);
        try {
            const response = await leaveAPI.getLeavesByClass(classId);
            if (response.success) {
                setLeaves(response.data);
            } else {
                setError(response.message);
            }
        } catch (err) {
            const message = err instanceof Error ? err.message : "Không thể kết nối đến máy chủ";
            setError(message);
        } finally {
            setIsLoading(false);
        }
    }, [classId]);

    /**
     * Gửi đơn xin nghỉ phép mới
     */
    const submitLeave = async (data: Omit<CreateLeaveRequestDTO, "classId">): Promise<{ success: boolean; message?: string }> => {
        setIsSubmitting(true);
        try {
            // Backend sử dụng Instant (ISO 8601). 
            // Input từ UI là 'YYYY-MM-DD', cần chuyển sang 'YYYY-MM-DDT00:00:00Z'
            // Chú ý: Backend có validation @FutureOrPresent và @Future
            
            const fromDate = new Date(data.from);
            fromDate.setHours(0, 0, 0, 0); // Đặt về đầu ngày

            const toDate = new Date(data.to);
            toDate.setHours(23, 59, 59, 999); // Đặt về cuối ngày để thỏa mãn @Future

            const payload: CreateLeaveRequestDTO = {
                ...data,
                from: fromDate.toISOString(),
                to: toDate.toISOString()
            };

            const response = await leaveAPI.createLeave(classId, payload);
            if (response.success) {
                await fetchLeaves();
                return { success: true };
            } else {
                return { success: false, message: response.message };
            }
        } catch (err) {
            const message = err instanceof Error ? err.message : "Lỗi khi gửi đơn xin nghỉ";
            return { success: false, message };
        } finally {
            setIsSubmitting(false);
        }
    };

    /**
     * Phê duyệt đơn (Admin)
     */
    const approveLeave = async (requestId: ID) => {
        setIsLoading(true);
        try {
            const res = await leaveAPI.approveLeave(classId, requestId);
            if (res.success) {
                await fetchLeaves();
                return true;
            }
            setError(res.message);
            return false;
        } catch {
            setError("Lỗi khi duyệt đơn");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    /**
     * Từ chối đơn (Admin)
     */
    const rejectLeave = async (requestId: ID) => {
        setIsLoading(true);
        try {
            const res = await leaveAPI.rejectLeave(classId, requestId);
            if (res.success) {
                await fetchLeaves();
                return true;
            }
            setError(res.message);
            return false;
        } catch {
            setError("Lỗi khi từ chối đơn");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    /**
     * Hủy đơn (Người tạo)
     */
    const cancelLeave = async (requestId: ID) => {
        setIsLoading(true);
        try {
            const res = await leaveAPI.cancelLeave(classId, requestId);
            if (res.success) {
                await fetchLeaves();
                return true;
            }
            setError(res.message);
            return false;
        } catch {
            setError("Lỗi khi hủy đơn");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    // Tự động tải dữ liệu khi hook được mount hoặc classId thay đổi
    useEffect(() => {
        fetchLeaves();
    }, [fetchLeaves]);

    return {
        leaves,
        isLoading,
        isSubmitting,
        error,
        fetchLeaves,
        submitLeave,
        approveLeave,
        rejectLeave,
        cancelLeave
    };
};
