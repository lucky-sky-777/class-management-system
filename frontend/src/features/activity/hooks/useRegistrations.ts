import { useState, useCallback } from "react";
import { activityAPI } from "@features/activity/api";
import type { ActivityRegistration } from "@features/activity/types";
import { ActivityRegistrationStatus } from "@shared/domain/enums";
import type { ID } from "@shared/utils/common";

export const useRegistrations = (classId?: ID) => {
    const [registrations, setRegistrations] = useState<ActivityRegistration[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchRegistrations = useCallback(async (activityId: ID, customClassId?: ID) => {
        const activeClassId = customClassId || classId;
        if (!activeClassId) {
            console.error("fetchRegistrations: Thừa nhận thiếu classId");
            return;
        }
        try {
            setIsLoading(true);
            setError(null);
            const data = await activityAPI.getRegistrations(activeClassId, activityId);
            setRegistrations(data);
        } catch (err) {
            setError("Không thể tải danh sách đăng ký.");
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    }, [classId]);

    const approve = async (regId: ID, customActivityId?: ID, customClassId?: ID): Promise<boolean> => {
        const reg = registrations.find((r) => r.id === regId);
        const activeActivityId = customActivityId || reg?.activityId;
        const activeClassId = customClassId || classId;

        if (!activeClassId || !activeActivityId) {
            console.error("approve: Thiếu classId hoặc activityId", { activeClassId, activeActivityId });
            return false;
        }

        try {
            const returnedId = await activityAPI.approveRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === returnedId ? { ...r, status: ActivityRegistrationStatus.APPROVED } : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi duyệt đăng ký:", err);
            return false;
        }
    };

    const reject = async (regId: ID, customActivityId?: ID, customClassId?: ID): Promise<boolean> => {
        const reg = registrations.find((r) => r.id === regId);
        const activeActivityId = customActivityId || reg?.activityId;
        const activeClassId = customClassId || classId;

        if (!activeClassId || !activeActivityId) {
            console.error("reject: Thiếu classId hoặc activityId", { activeClassId, activeActivityId });
            return false;
        }

        try {
            const returnedId = await activityAPI.rejectRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === returnedId ? { ...r, status: ActivityRegistrationStatus.REJECTED } : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi từ chối đăng ký:", err);
            return false;
        }
    };

    const cancel = async (regId: ID, customActivityId?: ID, customClassId?: ID): Promise<boolean> => {
        const reg = registrations.find((r) => r.id === regId);
        const activeActivityId = customActivityId || reg?.activityId;
        const activeClassId = customClassId || classId;

        if (!activeClassId || !activeActivityId) {
            console.error("cancel: Thiếu classId hoặc activityId", { activeClassId, activeActivityId });
            return false;
        }

        try {
            const returnedId = await activityAPI.cancelRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === returnedId ? { ...r, status: ActivityRegistrationStatus.CANCELLED } : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi hủy đăng ký:", err);
            return false;
        }
    };

    const register = async (activityId: ID, customClassId?: ID): Promise<ActivityRegistration | null> => {
        const activeClassId = customClassId || classId;
        if (!activeClassId) {
            console.error("register: Thiếu classId");
            return null;
        }

        try {
            const newReg = await activityAPI.registerActivity(activeClassId, activityId);
            setRegistrations((prev) => [...prev, newReg]);
            return newReg;
        } catch (err) {
            console.error("Lỗi đăng ký tham gia hoạt động:", err);
            return null;
        }
    };

    const submitProof = async (regId: ID, activityId: ID, proofUrl: string): Promise<boolean> => {
        const activeClassId = classId;
        if (!activeClassId) {
            console.error("submitProof: Thiếu classId");
            return false;
        }

        try {
            const updated = await activityAPI.submitProof(activeClassId, activityId, regId, proofUrl);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi nộp minh chứng:", err);
            return false;
        }
    };

    const cancelProof = async (regId: ID, activityId: ID): Promise<boolean> => {
        const activeClassId = classId;
        if (!activeClassId) {
            console.error("cancelProof: Thiếu classId");
            return false;
        }

        try {
            const updated = await activityAPI.cancelProof(activeClassId, activityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi hủy minh chứng:", err);
            return false;
        }
    };

    return {
        registrations,
        isLoading,
        error,
        fetchRegistrations,
        approve,
        reject,
        cancel,
        register,
        submitProof,
        cancelProof,
    };
};
