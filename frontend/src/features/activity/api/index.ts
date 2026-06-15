import { apiClient } from "@services/api-client";
import type { Activity, ActivityRegistration, ActivitySummary } from "@features/activity/types";
import type { CreateActivityDTO, UpdateActivityDTO } from "@features/activity/types";
import type { ID } from "@shared/utils/common";
import type { ResponseDTO } from "@shared/types";

interface ActivityResponseDto {
    id: ID;
    class_id: ID;
    name: string;
    description: string | null;
    start_at: string | null;
    end_at: string | null;
    registration_end_at: string | null;
    location: string | null;
    point: number | null;
    is_mandatory: boolean;
    created_at: string;
}

interface ActivityRegistrationResponseDto {
    id: ID;
    class_id: ID;
    activity_id: ID;
    proof_url: string | null;
    created_at: string;
    creator_user_id: ID;
    creator_display_name: string;
    creator_avatar_url: string | null;
    status: ActivityRegistration["status"];
    actor_user_id?: ID | null;
    actor_display_name?: string | null;
    actor_avatar_url?: string | null;
}

interface ActivityRegistrationIdResponseDto {
    activity_registration_id: ID;
}

const mapRegistration = (item: ActivityRegistrationResponseDto): ActivityRegistration => ({
    id: item.id,
    classId: item.class_id,
    activityId: item.activity_id,
    proofUrl: item.proof_url,
    createdAt: item.created_at,
    creatorUserId: item.creator_user_id,
    creatorDisplayName: item.creator_display_name || "Học sinh",
    creatorAvatarUrl: item.creator_avatar_url,
    status: item.status,
    actorUserId: item.actor_user_id || null,
    actorDisplayName: item.actor_display_name || null,
    actorAvatarUrl: item.actor_avatar_url || null
});

export const activityAPI = {
    /** Lấy danh sách hoạt động theo lớp */
    getActivities: async (classId: ID): Promise<Activity[]> => {
        const response = await apiClient.get<ResponseDTO<ActivityResponseDto[]>>(`/classes/${classId}/activities`);
        if (Array.isArray(response.data)) {
            return response.data.map((item) => ({
                id: item.id,
                classId: item.class_id,
                name: item.name,
                description: item.description,
                startAt: item.start_at,
                endAt: item.end_at,
                registrationEndAt: item.registration_end_at,
                location: item.location,
                point: item.point,
                isMandatory: item.is_mandatory,
                createdAt: item.created_at
            })) as Activity[];
        }
        return [];
    },

    /** Tạo hoạt động mới (Admin) */
    createActivity: async (classId: ID, dto: CreateActivityDTO): Promise<Activity> => {
        const payload = {
            name: dto.name,
            description: dto.description,
            start_at: dto.startAt,
            end_at: dto.endAt,
            registration_end_at: dto.registrationEndAt,
            location: dto.location,
            point: dto.point,
            is_mandatory: dto.isMandatory
        };
        const response = await apiClient.post<ResponseDTO<ActivityResponseDto>>(`/classes/${classId}/activities`, payload);
        const item = response.data;
        return {
            id: item.id,
            classId: item.class_id,
            name: item.name,
            description: item.description,
            startAt: item.start_at,
            endAt: item.end_at,
            registrationEndAt: item.registration_end_at,
            location: item.location,
            point: item.point,
            isMandatory: item.is_mandatory,
            createdAt: item.created_at
        } as Activity;
    },

    /** Cập nhật hoạt động (Admin) */
    updateActivity: async (classId: ID, id: ID, dto: UpdateActivityDTO): Promise<Activity> => {
        const payload = {
            name: dto.name,
            description: dto.description,
            start_at: dto.startAt,
            end_at: dto.endAt,
            registration_end_at: dto.registrationEndAt,
            location: dto.location,
            point: dto.point,
            is_mandatory: dto.isMandatory
        };
        const cleanPayload = Object.fromEntries(
            Object.entries(payload).filter(([, v]) => v !== undefined)
        );
        const response = await apiClient.patch<ResponseDTO<ActivityResponseDto>>(`/classes/${classId}/activities/${id}`, cleanPayload);
        const item = response.data;
        return {
            id: item.id,
            classId: item.class_id,
            name: item.name,
            description: item.description,
            startAt: item.start_at,
            endAt: item.end_at,
            registrationEndAt: item.registration_end_at,
            location: item.location,
            point: item.point,
            isMandatory: item.is_mandatory,
            createdAt: item.created_at
        } as Activity;
    },

    /** Xóa hoạt động (Admin) */
    deleteActivity: async (classId: ID, id: ID): Promise<void> => {
        await apiClient.delete<ResponseDTO<void>>(`/classes/${classId}/activities/${id}`);
    },

    /** Lấy danh sách đăng ký của một hoạt động (Admin) */
    getRegistrations: async (classId: ID, activityId: ID): Promise<ActivityRegistration[]> => {
        const response = await apiClient.get<ResponseDTO<ActivityRegistrationResponseDto[]>>(`/classes/${classId}/activities/${activityId}/registrations`);
        if (Array.isArray(response.data)) {
            return response.data.map(item => mapRegistration(item));
        }
        return [];
    },

    /** Đăng ký tham gia hoạt động (Member) */
    registerActivity: async (classId: ID, activityId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.post<ResponseDTO<ActivityRegistrationResponseDto>>(`/classes/${classId}/activities/${activityId}/registrations`, {});
        return mapRegistration(response.data);
    },

    /** Duyệt đăng ký (Admin) */
    approveRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ID> => {
        const response = await apiClient.patch<ResponseDTO<ActivityRegistrationIdResponseDto>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/approve`,
            {}
        );
        return response.data.activity_registration_id;
    },

    /** Từ chối đăng ký (Admin) */
    rejectRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ID> => {
        const response = await apiClient.patch<ResponseDTO<ActivityRegistrationIdResponseDto>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/reject`,
            {}
        );
        return response.data.activity_registration_id;
    },

    /** Hủy đăng ký (Member) */
    cancelRegistration: async (classId: ID, activityId: ID, regId: ID): Promise<ID> => {
        const response = await apiClient.patch<ResponseDTO<ActivityRegistrationIdResponseDto>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/cancel`,
            {}
        );
        return response.data.activity_registration_id;
    },

    /** Nộp minh chứng (Member) */
    submitProof: async (classId: ID, activityId: ID, regId: ID, proofUrl: string): Promise<ActivityRegistration> => {
        const response = await apiClient.patch<ResponseDTO<ActivityRegistrationResponseDto>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/proof`,
            { proof_url: proofUrl }
        );
        return mapRegistration(response.data);
    },

    /** Hủy minh chứng (Member) */
    cancelProof: async (classId: ID, activityId: ID, regId: ID): Promise<ActivityRegistration> => {
        const response = await apiClient.patch<ResponseDTO<ActivityRegistrationResponseDto>>(
            `/classes/${classId}/activities/${activityId}/registrations/${regId}/unproof`,
            {}
        );
        return mapRegistration(response.data);
    },

    /** Thống kê điểm rèn luyện tất cả members trong lớp */
    getSummaries: async (classId: ID): Promise<ActivitySummary[]> => {
        const response = await apiClient.get<ResponseDTO<ActivitySummary[]>>(`/classes/${classId}/activities/summaries`);
        if (Array.isArray(response.data)) {
            return response.data;
        }
        return [];
    },
};
