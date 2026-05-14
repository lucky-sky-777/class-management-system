import { apiClient } from "@services/api-client";
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
        // We use POST or GET with body for qrcode-url, but typically GET with body isn't standard though supported by the backend.
        // Let's use POST if possible, wait, backend BankController has:
        // @GetMapping("/qrcode-url") public ResponseDTO<BankQrCodeUrlResponseDto> getQrCodeUrl(@RequestBody GetQrCodeRequestDto request)
        // Axios doesn't send bodies with GET easily, but we can pass it as data.
        return await apiClient.get('/public/banks/qrcode-url', { data });
    }
};
