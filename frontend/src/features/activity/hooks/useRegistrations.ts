import { useState, useCallback } from "react";
import { activityAPI } from "@features/activity/api";
import type { ActivityRegistration } from "@features/activity/types";
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
            const updated = await activityAPI.approveRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
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
            const updated = await activityAPI.rejectRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
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
            const updated = await activityAPI.cancelRegistration(activeClassId, activeActivityId, regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
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

    return {
        registrations,
        isLoading,
        error,
        fetchRegistrations,
        approve,
        reject,
        cancel,
        register,
    };
};
