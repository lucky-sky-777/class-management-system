import { useState, useCallback } from "react";
import { activityAPI } from "@features/activity/api";
import type { ActivityRegistration } from "@features/activity/types";
import type { ID } from "@shared/utils/common";

export const useRegistrations = () => {
    const [registrations, setRegistrations] = useState<ActivityRegistration[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchRegistrations = useCallback(async (activityId: ID) => {
        try {
            setIsLoading(true);
            setError(null);
            const data = await activityAPI.getRegistrations(activityId);
            setRegistrations(data);
        } catch (err) {
            setError("Không thể tải danh sách đăng ký.");
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    }, []);

    const approve = async (regId: ID): Promise<boolean> => {
        try {
            const updated = await activityAPI.approveRegistration(regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi duyệt đăng ký:", err);
            return false;
        }
    };

    const reject = async (regId: ID): Promise<boolean> => {
        try {
            const updated = await activityAPI.rejectRegistration(regId);
            setRegistrations((prev) =>
                prev.map((r) => (r.id === regId ? updated : r))
            );
            return true;
        } catch (err) {
            console.error("Lỗi từ chối đăng ký:", err);
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
    };
};
