import { useState, useEffect, useCallback } from "react";
import { activityAPI } from "@features/activity/api";
import type { UserActivitySummary } from "@features/activity/types";
import type { ID } from "@shared/utils/common";

export const useUserSummary = (classId: ID) => {
    const [summaries, setSummaries] = useState<UserActivitySummary[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const fetchSummaries = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);
            const data = await activityAPI.getUserSummaries(classId);
            setSummaries(data);
        } catch (err) {
            setError("Không thể tải dữ liệu thống kê.");
            console.error(err);
        } finally {
            setIsLoading(false);
        }
    }, [classId]);

    useEffect(() => {
        if (classId) fetchSummaries();
    }, [classId, fetchSummaries]);

    return { summaries, isLoading, error, refresh: fetchSummaries };
};
