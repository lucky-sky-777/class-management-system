// Re-export domain types — FE layer không tự định nghĩa lại
export type { Activity, ActivityRegistration, UserActivitySummary } from "@shared/domain/activity";
export { ActivityRegistrationStatus } from "@shared/domain/enums";

// DTO cho form tạo mới hoạt động
export interface CreateActivityDTO {
    name: string;
    description?: string;
    startAt?: string; // ISO string
    endAt?: string;   // ISO string
    location?: string;
    point?: number;
    isMandatory: boolean;
}

// DTO cho form chỉnh sửa hoạt động
export type UpdateActivityDTO = Partial<CreateActivityDTO>;
