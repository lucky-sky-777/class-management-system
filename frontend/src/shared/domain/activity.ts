// ============================================================
// activity.ts — domain entity cho Activity và ActivityRegistration
// ============================================================
import { ActivityRegistrationStatus } from "@shared/domain/enums";
import type { ID, Timestamp } from "@shared/utils/common";

export interface Activity {
    id: ID;
    classId: ID;
    name: string;
    description: string | null;
    startAt: Timestamp | null;
    endAt: Timestamp | null;
    registrationEndAt: Timestamp | null;
    location: string | null;
    point: number | null;
    isMandatory: boolean;
    createdAt: Timestamp;
}

export interface ActivityRegistration {
    id: ID;
    classId: ID;
    activityId: ID;
    proofUrl: string | null;
    createdAt: Timestamp;
    creatorUserId: ID;
    creatorDisplayName: string;
    creatorAvatarUrl: string | null;
    status: ActivityRegistrationStatus;
    actorUserId?: ID | null;
    actorDisplayName?: string | null;
    actorAvatarUrl?: string | null;
}

// State machine helpers — encode transition rule tại đây
// thay vì rải logic kiểm tra status ra khắp các component

export function canApprove(reg: ActivityRegistration): boolean {
    return reg.status === ActivityRegistrationStatus.PENDING;
}

export function canReject(reg: ActivityRegistration): boolean {
    return reg.status === ActivityRegistrationStatus.PENDING;
}

// Derived data — tổng hợp điểm rèn luyện của 1 user trong 1 class
// FE tự tính từ danh sách registrations đã APPROVED
// Khi BE có endpoint riêng thì mapper sẽ map vào đây thay vì tính lại
export interface ActivitySummary {
    rank: number | null;
    userId: ID;
    userDisplayName: string;
    userAvatarUrl?: string | null;
    totalPoint: number;
}