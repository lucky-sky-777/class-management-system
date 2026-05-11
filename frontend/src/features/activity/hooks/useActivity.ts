import { useState, useEffect, useCallback } from "react";
import { activityAPI } from "@features/activity/api";
import type { Activity } from "@features/activity/types";
import type { CreateActivityDTO, UpdateActivityDTO } from "@features/activity/types";
import type { ID } from "@shared/utils/common";

export const useActivity = (classId: ID) => {
    const [activities, setActivities] = useState<Activity[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchActivities = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);
            const data = await activityAPI.getActivities(classId);
            setActivities(data);
        } catch (err) {
            setError("Không thể tải danh sách hoạt động.");
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    }, [classId]);

    useEffect(() => {
        if (classId) fetchActivities();
    }, [classId, fetchActivities]);

    const createActivity = async (dto: CreateActivityDTO): Promise<boolean> => {
        try {
            const created = await activityAPI.createActivity(classId, dto);
            setActivities((prev) => [created, ...prev]);
            return true;
        } catch (err) {
            console.error("Lỗi tạo hoạt động:", err);
            return false;
        }
    };

    const updateActivity = async (id: ID, dto: UpdateActivityDTO): Promise<boolean> => {
        try {
            const updated = await activityAPI.updateActivity(classId, id, dto);
            setActivities((prev) => prev.map((a) => (a.id === id ? updated : a)));
            return true;
        } catch (err) {
            console.error("Lỗi cập nhật hoạt động:", err);
            return false;
        }
    };

    const deleteActivity = async (id: ID): Promise<boolean> => {
        try {
            await activityAPI.deleteActivity(classId, id);
            setActivities((prev) => prev.filter((a) => a.id !== id));
            return true;
        } catch (err) {
            console.error("Lỗi xóa hoạt động:", err);
            return false;
        }
    };

    return {
        activities,
        isLoading,
        error,
        refresh: fetchActivities,
        createActivity,
        updateActivity,
        deleteActivity,
    };
};
