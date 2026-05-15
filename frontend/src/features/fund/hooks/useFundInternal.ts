import { useState, useEffect, useCallback } from "react";
import type { ID } from "@shared/utils/common";
import { fundAPI } from "../api";
import type { FundResponseDto, FundSummaryResponseDto, CreateFundRequestDto } from "../types";

export const useFundInternal = (classId: ID) => {
    const [summary, setSummary] = useState<FundSummaryResponseDto | null>(null);
    const [funds, setFunds] = useState<FundResponseDto[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const fetchData = useCallback(async () => {
        if (!classId) return;
        setIsLoading(true);
        setError(null);
        try {
            const [summaryRes, fundsRes] = await Promise.all([
                fundAPI.getFundSummary(classId),
                fundAPI.getFunds(classId)
            ]);

            if (summaryRes.success && fundsRes.success) {
                setSummary(summaryRes.data);
                setFunds(fundsRes.data);
            } else {
                setError(summaryRes.message || fundsRes.message || "Lỗi khi lấy dữ liệu quỹ lớp");
            }
        } catch (err) {
            setError("Lỗi kết nối khi tải dữ liệu quỹ lớp");
        } finally {
            setIsLoading(false);
        }
    }, [classId]);

    const createFund = async (data: CreateFundRequestDto) => {
        setIsLoading(true);
        try {
            const response = await fundAPI.createFund(classId, data);
            if (response.success) {
                await fetchData(); // Refresh to get updated list and summary
                return true;
            } else {
                setError(response.message);
                return false;
            }
        } catch (err) {
            setError("Lỗi kết nối khi tạo giao dịch");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    const deleteFund = async (fundId: ID) => {
        setIsLoading(true);
        try {
            const response = await fundAPI.deleteFund(classId, fundId);
            if (response.success) {
                await fetchData(); // Refresh list and summary
                return true;
            } else {
                setError(response.message);
                return false;
            }
        } catch (err) {
            setError("Lỗi kết nối khi xóa giao dịch");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    return {
        summary,
        funds,
        isLoading,
        error,
        refresh: fetchData,
        createFund,
        deleteFund
    };
};
