import { useState, useEffect, useCallback } from "react";
import type { ID } from "@shared/utils/common";
import { fundAPI } from "../api";
import type { FundResponseDto, FundSummaryResponseDto, CreateFundRequestDto, BankConfig } from "../types";
import { toast } from "react-toastify";

export const useFundInternal = (classId: ID) => {
    const [summary, setSummary] = useState<FundSummaryResponseDto | null>(null);
    const [funds, setFunds] = useState<FundResponseDto[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [bankConfig, setBankConfig] = useState<BankConfig | null>(() => {
        try {
            const saved = localStorage.getItem(`class_${classId}_bank_config`);
            return saved ? JSON.parse(saved) : null;
        } catch {
            return null;
        }
    });

    const updateBankConfig = useCallback(async (config: BankConfig) => {
        setBankConfig(config);
        try {
            const res = await fundAPI.updatePaymentAccount(classId, {
                bank_code: config.bank_code,
                number: config.account_number,
                name: config.account_name
            });
            if (res.success == false) {
                setError("Cập nhật thông tin tài khoản thất bại");
                console.error("Failed to update payment account:");
                console.error(res);
            }
            toast.success("Cập nhật thông tin tài khoản thành công");
        } catch (err) {
            setError("Lỗi kết nối khi cập nhật thông tin tài khoản");
            console.error("Failed to update payment account:", err);
        }
    }, [classId]);


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
            toast.success("Tạo giao dịch thành công");
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
            toast.success("Xóa giao dịch thành công");
        } catch (err) {
            setError("Lỗi kết nối khi xóa giao dịch");
            return false;
        } finally {
            setIsLoading(false);
        }
    };

    // initial load
    useEffect(() => {
        fetchData();
    }, [fetchData]);

    // display error toast
    useEffect(() => {
        if (error) {
            toast.error(error);
            console.error("Error:", error);
        }
    }, [error]);
    return {
        summary,
        funds,
        isLoading,
        error,
        refresh: fetchData,
        createFund,
        deleteFund,
        bankConfig,
        updateBankConfig
    };
};
