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
