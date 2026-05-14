import { apiClient } from "@services/api-client";
import axios from "axios";
import type { ID, ApiResponse } from "@shared/utils/common";
import type { 
    FundResponseDto, 
    FundSummaryResponseDto, 
    CreateFundRequestDto,
    VietQrBankResponseDto,
    GetQrCodeRequestDto,
    BankQrCodeUrlResponseDto
} from "../types";

export const fundAPI = {
    getFunds: async (classId: ID): Promise<ApiResponse<FundResponseDto[]>> => {
        return await apiClient.get(`/classes/${classId}/funds`);
    },

    getFundSummary: async (classId: ID): Promise<ApiResponse<FundSummaryResponseDto>> => {
        return await apiClient.get(`/classes/${classId}/funds/summary`);
    },

    createFund: async (classId: ID, data: CreateFundRequestDto): Promise<ApiResponse<FundResponseDto>> => {
        return await apiClient.post(`/classes/${classId}/funds`, data);
    },

    deleteFund: async (classId: ID, fundId: ID): Promise<ApiResponse<{ fund_id: ID }>> => {
        return await apiClient.delete(`/classes/${classId}/funds/${fundId}`);
    },

    getBanks: async (): Promise<ApiResponse<VietQrBankResponseDto[]>> => {
        return await apiClient.get('/public/banks');
    },

    getQrCodeUrl: async (data: GetQrCodeRequestDto): Promise<ApiResponse<BankQrCodeUrlResponseDto>> => {
        try {
            // Using direct VietQR API as backend is currently having issues
            const response = await axios.post("https://api.vietqr.io/v2/generate", {
                accountNo: data.account_number,
                accountName: data.account_name,
                acqId: data.bank_code, // acqId is usually the BIN or bank code
                addInfo: data.notes,
                amount: data.amount,
                template: "compact" // Using compact template
            });

            if (response.data && response.data.code === "00") {
                return {
                    success: true,
                    message: "Tạo mã QR thành công",
                    data: {
                        qr_code_url: response.data.data.qrDataURL
                    },
                    time: new Date().toISOString()
                };
            } else {
                return {
                    success: false,
                    message: response.data?.desc || "Không thể tạo mã QR",
                    time: new Date().toISOString()
                } as any;
            }
        } catch (error) {
            console.error("VietQR API Error:", error);
            return {
                success: false,
                message: "Lỗi khi kết nối với VietQR API",
                timestamp: new Date().toISOString()
            } as any;
        }
    }
};
