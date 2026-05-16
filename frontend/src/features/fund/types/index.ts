import type { ID, Timestamp } from "@shared/utils/common";

export interface FundResponseDto {
    id: ID;
    class_id: ID;
    type: "INCOME" | "EXPENSE";
    amount: number;
    title: string;
    description: string;
    created_at: Timestamp;
    creator_user_id: ID;
}

export interface FundSummaryResponseDto {
    balance: number;
    total_income: number;
    total_expense: number;
}

export interface CreateFundRequestDto {
    type: "INCOME" | "EXPENSE";
    amount: number;
    title: string;
    description: string;
}

export interface VietQrBankResponseDto {
    id: ID;
    name: string;
    code: string;
    bin: string;
    shortName: string;
    logo: string;
    transferSupported: number;
    lookupSupported: number;
    short_name: string;
    support: number;
    isTransfer: number;
    swift_code: string;
}

export interface GetQrCodeRequestDto {
    bank_code: string;
    account_number: string;
    account_name: string;
    amount: number;
    notes: string;
}

export interface BankQrCodeUrlResponseDto {
    qr_code_url: string;
}

export type FundPaymentStatus = "PENDING" | "APPROVED" | "REJECTED" | "CANCELLED";

export interface FundPaymentResponseDto {
    id: ID;
    class_id: ID;
    fund_id: ID;
    proof_url: string;
    created_at: Timestamp;
    creator_user_id: ID;
    creator_display_name: string;
    creator_avatar_url: string;
    status: FundPaymentStatus;
    actor_user_id?: ID;
    actor_display_name?: string;
    actor_avatar_url?: string;
}

export interface CreateFundPaymentRequestDto {
    proof_url: string;
}

export interface BankConfig {
    bank_code: string;
    account_number: string;
    account_name: string;
}

export interface CreateOrUpdatePaymentAccountRequest {
    bank_code: string;
    number: string;
    name: string;
}

export interface PaymentAccountResponse {
    "id": ID,
    "class_id": ID,
    "bank_code": string,
    "number": string,
    "name": string,
    "qr_code_url": string,
    "creator_user_id": string,
    "creator_display_name": string,
    "creator_avatar_url": string,
    "created_at": Timestamp
}