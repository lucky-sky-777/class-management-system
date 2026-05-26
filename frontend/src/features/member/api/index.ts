// src/features/member/api.ts
import { apiClient } from "@services/api-client";
import type { Member, MemberRole, ClassRoleAndPermissionListResponse } from "@features/member/types";
import type { PermissionCode } from "@shared/domain/enums";

// 1. CÁC TYPE TRUNG GIAN HỨNG DỮ LIỆU THÔ TỪ BACKEND
type GenericResponse = Record<string, unknown>;

type RawMember = {
    user_id: string | number;
    user_display_name?: string;
    username?: string;
    role?: string;
    joined_at?: string;
    permissions?: PermissionCode[]; 
};

type RawClassData = {
    owner_user_id?: string | number;
};

type RawPendingRequest = {
    id: number;
    user_id: string | number;
    user_display_name?: string;
    status?: string;
    created_at?: string;
};

// 2. BỘ HELPER XỬ LÝ LỖI "UNKNOWN"
const extractData = <T>(response: unknown): T => {
    if (!response) return {} as T;
    const res = response as Record<string, unknown>;
    return (res.data ?? res ?? {}) as T;
};

const extractList = <T>(response: unknown): T[] => {
    if (!response) return [];
    const res = response as Record<string, unknown>;
    const data = res.data as Record<string, unknown> | undefined;
    return (data?.data || res.data || res || []) as T[];
};

export const memberAPI = {
    // 1. Chỉ lấy thành viên chính thức
    getMembers: async (classId: string): Promise<Member[]> => {
        const [resMembers, resClass] = await Promise.all([
            apiClient.get(`/classes/${classId}/members`),
            apiClient.get(`/classes/${classId}/data`),
        ]);

        // Bóc tách dữ liệu an toàn bằng Helper
        const rawMembers = extractList<RawMember>(resMembers);
        const classData = extractData<RawClassData>(resClass);
        const ownerId = classData.owner_user_id;

        return rawMembers.map((m) => ({
            userId: Number(m.user_id),
            displayName: m.user_display_name || "Thành viên",
            username: m.username || "",
            role:
                Number(m.user_id) === Number(ownerId)
                    ? "OWNER"
                    : (m.role as MemberRole) || "CLASS_MEMBER",
            joinedAt: m.joined_at || "",
            permissions: m.permissions || [],
        }));
    },

    // 2. Lấy danh sách chờ duyệt
    getPendingRequests: async (classId: string): Promise<Member[]> => {
        try {
            const res = await apiClient.get(`/join-class-requests/${classId}`);
            const rawData = extractList<RawPendingRequest>(res);

            // CHỈ LỌC NHỮNG THẰNG CÓ STATUS LÀ PENDING
            const pendingOnly = rawData.filter((p) => p.status === "PENDING");

            return pendingOnly.map((p) => ({
                userId: Number(p.user_id),
                requestId: p.id,
                displayName: p.user_display_name || `Học sinh #${p.user_id}`,
                username: `user_${p.user_id}`,
                role: "PENDING",
                joinedAt: p.created_at || "",
            }));
        } catch (error) {
            console.error("Lỗi lấy danh sách chờ:", error);
            return [];
        }
    },

    // 3. Cập nhật quyền (chức vụ đơn thuần)
    updateRole: async (classId: string, userId: number, role: MemberRole): Promise<GenericResponse> => {
        const res = await apiClient.patch(`/classes/${classId}/members/${userId}/role`, {
            role,
        });
        return extractData<GenericResponse>(res);
    },

    // 4. Kích ra khỏi lớp
    kickMember: async (classId: string, userId: number): Promise<GenericResponse> => {
        const res = await apiClient.delete(`/classes/${classId}/members/${userId}`);
        return extractData<GenericResponse>(res);
    },

    // 5. Duyệt yêu cầu
    approveRequest: async (classId: string | number, requestId: number): Promise<GenericResponse> => {
        const res = await apiClient.patch(
            `/join-class-requests/classes/${classId}/requests/${requestId}/approve`,
            {},
        );
        return extractData<GenericResponse>(res);
    },

    // 6. Từ chối yêu cầu
    rejectRequest: async (classId: string | number, requestId: number): Promise<GenericResponse> => {
        const res = await apiClient.patch(
            `/join-class-requests/classes/${classId}/requests/${requestId}/reject`,
            {},
        );
        return extractData<GenericResponse>(res);
    },

    // 7. Lấy danh mục các vai trò và quyền hạn
    getClassRoleAndPermissionList: async (classId: string): Promise<ClassRoleAndPermissionListResponse> => {
        const res = await apiClient.get(`/classes/${classId}/authorities`);
        return extractData<ClassRoleAndPermissionListResponse>(res);
    },

    // 8. Cập nhật vai trò và các quyền hạn cụ thể
    updateMemberAuthorities: async (
        classId: string,
        userId: number,
        role: MemberRole,
        permissions: PermissionCode[]
    ): Promise<GenericResponse> => {
        const res = await apiClient.put(`/classes/${classId}/authorities/members/${userId}`, {
            role,
            permissions,
        });
        return extractData<GenericResponse>(res);
    },
};