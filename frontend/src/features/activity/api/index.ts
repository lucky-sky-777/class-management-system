import { apiClient } from "@services/api-client";
import type { Activity, ActivityRegistration, UserActivitySummary } from "@features/activity/types";
import type { CreateActivityDTO, UpdateActivityDTO } from "@features/activity/types";
import type { ID } from "@shared/utils/common";
import type { ResponseDTO } from "@shared/types";

export const activityAPI = {
    /** Lấy danh sách hoạt động theo lớp */
    getActivities: async (classId: ID): Promise<Activity[]> => {
        const response = await apiClient.get<ResponseDTO<any[]>>(`/classes/${classId}/activities`);
        if (Array.isArray(response.data)) {
            return response.data.map((item: any) => ({
                id: item.id,
                classId: item.class_id || item.classId,
                name: item.name,
                description: item.description,
                startAt: item.start_at || item.startAt,
                endAt: item.end_at || item.endAt,
                location: item.location,
                point: item.point,
                isMandatory: item.is_mandatory ?? item.isMandatory,
                createdAt: item.created_at || item.createdAt
            })) as Activity[];
        }
        return response.data as Activity[];
    },

    /** Tạo hoạt động mới (Admin) */
    createActivity: async (classId: ID, dto: CreateActivityDTO): Promise<Activity> => {
        const payload = {
            name: dto.name,
            description: dto.description,
            start_at: dto.startAt,
            end_at: dto.endAt,
            location: dto.location,
            point: dto.point,
            is_mandatory: dto.isMandatory
        };
        const response = await apiClient.post<ResponseDTO<any>>(`/classes/${classId}/activities`, payload);
        const item = response.data;
        return {
            id: item.id,
            classId: item.class_id || item.classId,
            name: item.name,
            description: item.description,
            startAt: item.start_at || item.startAt,
            endAt: item.end_at || item.endAt,
            location: item.location,
            point: item.point,
            isMandatory: item.is_mandatory ?? item.isMandatory,
            createdAt: item.created_at || item.createdAt
        } as Activity;
    },

    /** Cập nhật hoạt động (Admin) */
    updateActivity: async (classId: ID, id: ID, dto: UpdateActivityDTO): Promise<Activity> => {
        const payload = {
            name: dto.name,
            description: dto.description,
            start_at: dto.startAt,
            end_at: dto.endAt,
            location: dto.location,
            point: dto.point,
            is_mandatory: dto.isMandatory
        };
        const cleanPayload = Object.fromEntries(Object.entries(payload).filter(([_, v]) => v !== undefined));
        const response = await apiClient.patch<ResponseDTO<any>>(`/classes/${classId}/activities/${id}`, cleanPayload);
        const item = response.data;
        return {
            id: item.id,
            classId: item.class_id || item.classId,
            name: item.name,
            description: item.description,
            startAt: item.start_at || item.startAt,
            endAt: item.end_at || item.endAt,
            location: item.location,
            point: item.point,
            isMandatory: item.is_mandatory ?? item.isMandatory,
            createdAt: item.created_at || item.createdAt
        } as Activity;
    },

    /** Xóa hoạt động (Admin) */
    deleteActivity: async (classId: ID, id: ID): Promise<void> => {
        await apiClient.delete<ResponseDTO<void>>(`/classes/${classId}/activities/${id}`);
    },

    /** Lấy danh sách đăng ký của một hoạt động (Admin) */
    getRegistrations: async (activityId: ID): Promise<ActivityRegistration[]> => {
        const response = await apiClient.get<ResponseDTO<any[]>>(`/activities/${activityId}/registrations`);
        if (Array.isArray(response.data)) {
            return response.data.map(item => ({
                id: item.id,
                activityId: item.activity_id || item.activityId,
                registeredUser: item.registered_user || item.registeredUser,
                proofImageUrl: item.proof_image_url || item.proofImageUrl,
                status: item.status,
                registeredAt: item.registered_at || item.registeredAt
            })) as ActivityRegistration[];
        }
        return response.data as ActivityRegistration[];
    },

    /** Duyệt đăng ký (Admin) */
    approveRegistration: async (regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.post<ResponseDTO<any>>(`/activity-registrations/${regId}/approve`, {});
        const item = response.data;
        return {
            id: item.id,
            activityId: item.activity_id || item.activityId,
            registeredUser: item.registered_user || item.registeredUser,
            proofImageUrl: item.proof_image_url || item.proofImageUrl,
            status: item.status,
            registeredAt: item.registered_at || item.registeredAt
        } as ActivityRegistration;
    },

    /** Từ chối đăng ký (Admin) */
    rejectRegistration: async (regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.post<ResponseDTO<any>>(`/activity-registrations/${regId}/reject`, {});
        const item = response.data;
        return {
            id: item.id,
            activityId: item.activity_id || item.activityId,
            registeredUser: item.registered_user || item.registeredUser,
            proofImageUrl: item.proof_image_url || item.proofImageUrl,
            status: item.status,
            registeredAt: item.registered_at || item.registeredAt
        } as ActivityRegistration;
    },

    /** Thống kê điểm rèn luyện tất cả members trong lớp */
    getUserSummaries: async (classId: ID): Promise<UserActivitySummary[]> => {
        const response = await apiClient.get<ResponseDTO<any[]>>(`/classes/${classId}/activities/summaries`);
        if (Array.isArray(response.data)) {
            return response.data.map(item => ({
                userId: item.user_id || item.userId,
                approvedCount: item.approved_count || item.approvedCount,
                totalPoint: item.total_point || item.totalPoint,
                mandatoryApproved: item.mandatory_approved || item.mandatoryApproved,
                mandatoryTotal: item.mandatory_total || item.mandatoryTotal
            })) as UserActivitySummary[];
        }
        return response.data as UserActivitySummary[];
    },
};
