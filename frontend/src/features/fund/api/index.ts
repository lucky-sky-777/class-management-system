import { apiClient } from "@services/api-client";
import type { ID, ApiResponse } from "@shared/utils/common";
import type {
    FundResponseDto,
    FundSummaryResponseDto,
    CreateFundRequestDto,
    VietQrBankResponseDto,
    GetQrCodeRequestDto,
    BankQrCodeUrlResponseDto,
    FundPaymentResponseDto,
    CreateFundPaymentRequestDto,
    PaymentAccountResponse,
    BankConfig,
    CreateOrUpdatePaymentAccountRequest
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

    getFundPayments: async (classId: ID, fundId: ID): Promise<ApiResponse<FundPaymentResponseDto[]>> => {
        return await apiClient.get(`/classes/${classId}/fund-payments/funds/${fundId}`);
    },

    createFundPayment: async (classId: ID, fundId: ID, data: CreateFundPaymentRequestDto): Promise<ApiResponse<FundPaymentResponseDto>> => {
        return await apiClient.post(`/classes/${classId}/fund-payments/funds/${fundId}`, data);
    },

    approveFundPayment: async (classId: ID, fundPaymentId: ID): Promise<ApiResponse<{ fund_payment_id: ID }>> => {
        return await apiClient.patch(`/classes/${classId}/fund-payments/${fundPaymentId}/approve`, {});
    },

    rejectFundPayment: async (classId: ID, fundPaymentId: ID): Promise<ApiResponse<{ fund_payment_id: ID }>> => {
        return await apiClient.patch(`/classes/${classId}/fund-payments/${fundPaymentId}/reject`, {});
    },

    cancelFundPayment: async (classId: ID, fundPaymentId: ID): Promise<ApiResponse<{ fund_payment_id: ID }>> => {
        return await apiClient.patch(`/classes/${classId}/fund-payments/${fundPaymentId}/cancel`, {});
    },

    getBanks: async (): Promise<ApiResponse<VietQrBankResponseDto[]>> => {
        return await apiClient.get('/public/banks');
    },

    getQrCodeUrl: async (data: GetQrCodeRequestDto): Promise<ApiResponse<BankQrCodeUrlResponseDto>> => {
        return await apiClient.post('/public/qrcode-url', data);
    },

    updatePaymentAccount: async (classId: ID, data: CreateOrUpdatePaymentAccountRequest): Promise<ApiResponse<PaymentAccountResponse>> => {
        return await apiClient.put(`/classes/${classId}/payment-account`, data);
    }
};
