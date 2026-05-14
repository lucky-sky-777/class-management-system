import type { ID, Timestamp } from "@shared/utils/common";

/**
 * Các trạng thái của một đơn xin nghỉ phép
 */
export type LeaveStatus = "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";

/**
 * Model đại diện cho một bản ghi đơn xin nghỉ phép
 */
export interface LeaveRequest {
    id: ID;
    classId: ID;
    userId: ID;
    user_display_name?: string;
    user_avatar_url?: string;
    reason: string;
    from: Timestamp; // ISO Instant from backend
    to: Timestamp;   // ISO Instant from backend
    proof_url?: string;
    status: LeaveStatus;
    created_at: Timestamp;
}

/**
 * DTO dùng để tạo đơn xin nghỉ phép mới
 */
export interface CreateLeaveRequestDTO {
    reason: string;
    from: string; // ISO String or YYYY-MM-DD
    to: string;   // ISO String or YYYY-MM-DD
    proof_url?: string;
}

/**
 * DTO dùng để cập nhật trạng thái đơn (dành cho Admin)
 */
export interface UpdateLeaveStatusDTO {
    status: LeaveStatus;
}
