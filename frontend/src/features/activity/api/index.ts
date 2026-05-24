import { apiClient } from "@services/api-client";
import type { Activity, ActivityRegistration, ActivitySummary } from "@features/activity/types";
import type { CreateActivityDTO, UpdateActivityDTO } from "@features/activity/types";
import type { ID } from "@shared/utils/common";
import type { ResponseDTO } from "@shared/types";

const mapRegistration = (item: any): ActivityRegistration => ({
    id: item.id,
    activityId: item.activity_id || item.activityId,
    registeredUser: {
        id: item.creator_user_id || item.creatorUserId,
        displayName: item.creator_display_name || item.creatorDisplayName || "Học sinh",
        avatarUrl: item.creator_avatar_url || item.creatorAvatarUrl,
        username: "",
    } as any,
    proofImageUrl: item.proof_url || item.proofUrl || null,
    status: item.status,
    registeredAt: item.created_at || item.createdAt || ""
});

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
    getRegistrations: async (classId: ID, activityId: ID): Promise<ActivityRegistration[]> => {
        const response = await apiClient.get<ResponseDTO<any[]>>(`/classes/${classId}/activities/${activityId}/registrations`);
        if (Array.isArray(response.data)) {
            return response.data.map(item => mapRegistration(item));
        }
        return [];
    },

    /** Đăng ký tham gia hoạt động (Member) */
    registerActivity: async (classId: ID, activityId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.post<ResponseDTO<any>>(`/classes/${classId}/activities/${activityId}/registrations`, {});
        return mapRegistration(response.data);
    },

    /** Duyệt đăng ký (Admin) */
    approveRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.patch<ResponseDTO<any>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/approve`,
            {}
        );
        return mapRegistration(response.data);
    },

    /** Từ chối đăng ký (Admin) */
    rejectRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.patch<ResponseDTO<any>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/reject`,
            {}
        );
        return mapRegistration(response.data);
    },

    /** Hủy đăng ký (Member) */
    cancelRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.patch<ResponseDTO<any>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/cancel`,
            {}
        );
        return mapRegistration(response.data);
    },

    /** Thống kê điểm rèn luyện tất cả members trong lớp */
    getSummaries: async (classId: ID): Promise<ActivitySummary[]> => {
        const response = await apiClient.get<ResponseDTO<any[]>>(`/classes/${classId}/activities/summaries`);
        if (Array.isArray(response.data)) {
            return response.data.map(item => ({
                rank: item.rank,
                userId: item.user_id || item.userId,
                userDisplayName: item.user_display_name || item.userDisplayName,
                userAvatarUrl: item.user_avatar_url || item.userAvatarUrl,
                totalPoint: item.total_point ?? item.totalPoint
            })) as ActivitySummary[];
        }
        return response.data as ActivitySummary[];
    },
};
