import { apiClient } from "@services/api-client";
import type { ApiResponse, ID } from "@shared/utils/common";
import type { LeaveRequest, CreateLeaveRequestDTO } from "../types";

/**
 * Service thao tác với dữ liệu nghỉ phép từ Backend
 */
export const leaveAPI = {
    /**
     * Lấy danh sách đơn nghỉ phép theo lớp
     */
    getLeavesByClass: async (classId: ID): Promise<ApiResponse<LeaveRequest[]>> => {
        return await apiClient.get(`/absence-requests/classes/${classId}/requests`);
    },

    /**
     * Lấy danh sách đơn nghỉ phép của bản thân
     */
    getLeavesByUser: async (userId: ID): Promise<ApiResponse<LeaveRequest[]>> => {
        return await apiClient.get(`/absence-requests/users/${userId}/requests`);
    },

    /**
     * Tạo mới đơn xin nghỉ phép
     */
    createLeave: async (classId: ID, data: CreateLeaveRequestDTO): Promise<ApiResponse<LeaveRequest>> => {
        return await apiClient.post(`/absence-requests/classes/${classId}`, data);
    },

    /**
     * Phê duyệt đơn nghỉ phép (Admin)
     */
    approveLeave: async (classId: ID, requestId: ID): Promise<ApiResponse<{ id: ID }>> => {
        return await apiClient.post(`/absence-requests/classes/${classId}/requests/${requestId}/approve`, {});
    },

    /**
     * Từ chối đơn nghỉ phép (Admin)
     */
    rejectLeave: async (classId: ID, requestId: ID): Promise<ApiResponse<{ id: ID }>> => {
        return await apiClient.post(`/absence-requests/classes/${classId}/requests/${requestId}/reject`, {});
    },

    /**
     * Hủy đơn xin nghỉ phép (Người tạo)
     */
    cancelLeave: async (classId: ID, requestId: ID): Promise<ApiResponse<{ id: ID }>> => {
        return await apiClient.patch(`/absence-requests/classes/${classId}/requests/${requestId}/cancel`, {});
    }
};
