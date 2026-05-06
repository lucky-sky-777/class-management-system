import type { Activity, ActivityRegistration, UserActivitySummary } from "@features/activity/types";
import type { CreateActivityDTO, UpdateActivityDTO } from "@features/activity/types";
import { ActivityRegistrationStatus } from "@shared/domain/enums";
import type { ID } from "@shared/utils/common";

// =============================================================
// Mock data
// =============================================================

let mockActivities: Activity[] = [
    {
        id: 1,
        classId: 1,
        name: "Chiến dịch Mùa Hè Xanh 2026",
        description: "Tham gia tình nguyện hỗ trợ người dân vùng khó khăn, xây dựng công trình dân sinh.",
        startAt: "2026-06-10T07:00:00Z",
        endAt: "2026-06-20T17:00:00Z",
        location: "Huyện Bình Chánh, TP.HCM",
        point: 20,
        isMandatory: false,
        createdAt: "2026-04-15T08:00:00Z",
    },
    {
        id: 2,
        classId: 1,
        name: "Hiến máu nhân đạo đợt 1",
        description: "Hoạt động hiến máu do Đoàn Khoa tổ chức. Bắt buộc đối với thành viên lớp.",
        startAt: "2026-05-20T08:00:00Z",
        endAt: "2026-05-20T11:00:00Z",
        location: "Hội trường A – Trường ĐH Bách Khoa",
        point: 10,
        isMandatory: true,
        createdAt: "2026-04-20T10:00:00Z",
    },
    {
        id: 3,
        classId: 1,
        name: "Tọa đàm kỹ năng mềm: Giao tiếp & Thuyết trình",
        description: "Buổi tọa đàm trang bị kỹ năng giao tiếp, thuyết trình cho sinh viên trước kỳ thực tập.",
        startAt: "2026-05-15T14:00:00Z",
        endAt: "2026-05-15T17:00:00Z",
        location: "Phòng B2-01",
        point: 5,
        isMandatory: false,
        createdAt: "2026-04-25T09:00:00Z",
    },
];

let mockRegistrations: ActivityRegistration[] = [
    {
        id: 1,
        activityId: 2,
        registeredUser: { id: 101, displayName: "Nguyễn Văn An", username: "nguyenvanan" },
        proofImageUrl: "https://via.placeholder.com/300",
        status: ActivityRegistrationStatus.APPROVED,
        registeredAt: "2026-04-21T10:00:00Z",
    },
    {
        id: 2,
        activityId: 2,
        registeredUser: { id: 102, displayName: "Trần Thị Bích", username: "tranthibich" },
        proofImageUrl: null,
        status: ActivityRegistrationStatus.PENDING,
        registeredAt: "2026-04-22T09:00:00Z",
    },
    {
        id: 3,
        activityId: 2,
        registeredUser: { id: 103, displayName: "Lê Quốc Cường", username: "lequoccuong" },
        proofImageUrl: "https://via.placeholder.com/300",
        status: ActivityRegistrationStatus.REJECTED,
        registeredAt: "2026-04-22T14:00:00Z",
    },
    {
        id: 4,
        activityId: 1,
        registeredUser: { id: 101, displayName: "Nguyễn Văn An", username: "nguyenvanan" },
        proofImageUrl: null,
        status: ActivityRegistrationStatus.PENDING,
        registeredAt: "2026-04-23T08:00:00Z",
    },
];

// =============================================================
// API
// =============================================================

const delay = (ms = 500) => new Promise((r) => setTimeout(r, ms));

export const activityAPI = {
    /** Lấy danh sách hoạt động theo lớp */
    getActivities: async (classId: ID): Promise<Activity[]> => {
        await delay();
        return mockActivities.filter((a) => a.classId === classId);
    },

    /** Tạo hoạt động mới (Admin) */
    createActivity: async (classId: ID, dto: CreateActivityDTO): Promise<Activity> => {
        await delay(600);
        const newActivity: Activity = {
            id: Date.now(),
            classId,
            name: dto.name,
            description: dto.description ?? null,
            startAt: dto.startAt ?? null,
            endAt: dto.endAt ?? null,
            location: dto.location ?? null,
            point: dto.point ?? null,
            isMandatory: dto.isMandatory,
            createdAt: new Date().toISOString(),
        };
        mockActivities = [newActivity, ...mockActivities];
        return newActivity;
    },

    /** Cập nhật hoạt động (Admin) */
    updateActivity: async (id: ID, dto: UpdateActivityDTO): Promise<Activity> => {
        await delay(500);
        const idx = mockActivities.findIndex((a) => a.id === id);
        if (idx === -1) throw new Error("Không tìm thấy hoạt động");
        mockActivities[idx] = { ...mockActivities[idx], ...dto };
        return mockActivities[idx];
    },

    /** Xóa hoạt động (Admin) */
    deleteActivity: async (id: ID): Promise<void> => {
        await delay(400);
        mockActivities = mockActivities.filter((a) => a.id !== id);
    },

    /** Lấy danh sách đăng ký của một hoạt động (Admin) */
    getRegistrations: async (activityId: ID): Promise<ActivityRegistration[]> => {
        await delay();
        return mockRegistrations.filter((r) => r.activityId === activityId);
    },

    /** Duyệt đăng ký (Admin) */
    approveRegistration: async (regId: ID): Promise<ActivityRegistration> => {
        await delay(400);
        const idx = mockRegistrations.findIndex((r) => r.id === regId);
        if (idx === -1) throw new Error("Không tìm thấy đăng ký");
        mockRegistrations[idx] = {
            ...mockRegistrations[idx],
            status: ActivityRegistrationStatus.APPROVED,
        };
        return mockRegistrations[idx];
    },

    /** Từ chối đăng ký (Admin) */
    rejectRegistration: async (regId: ID): Promise<ActivityRegistration> => {
        await delay(400);
        const idx = mockRegistrations.findIndex((r) => r.id === regId);
        if (idx === -1) throw new Error("Không tìm thấy đăng ký");
        mockRegistrations[idx] = {
            ...mockRegistrations[idx],
            status: ActivityRegistrationStatus.REJECTED,
        };
        return mockRegistrations[idx];
    },

    /** Thống kê điểm rèn luyện tất cả members trong lớp */
    getUserSummaries: async (classId: ID): Promise<UserActivitySummary[]> => {
        await delay(700);
        const classActivities = mockActivities.filter((a) => a.classId === classId);
        const actMap = new Map(classActivities.map((a) => [a.id, a]));

        // Gom theo userId
        const userMap = new Map<ID, ActivityRegistration[]>();
        for (const reg of mockRegistrations) {
            if (!actMap.has(reg.activityId)) continue;
            const uid = reg.registeredUser.id as ID;
            if (!userMap.has(uid)) userMap.set(uid, []);
            userMap.get(uid)!.push(reg);
        }

        const summaries: UserActivitySummary[] = [];
        for (const [userId, regs] of userMap.entries()) {
            const approved = regs.filter((r) => r.status === ActivityRegistrationStatus.APPROVED);
            const totalPoint = approved.reduce((sum, r) => {
                const act = actMap.get(r.activityId);
                return sum + (act?.point ?? 0);
            }, 0);
            const mandatoryApproved = approved.filter((r) => actMap.get(r.activityId)?.isMandatory).length;
            const mandatoryTotal = classActivities.filter((a) => a.isMandatory).length;
            summaries.push({
                userId,
                approvedCount: approved.length,
                totalPoint,
                mandatoryApproved,
                mandatoryTotal,
            });
        }
        return summaries;
    },
};
