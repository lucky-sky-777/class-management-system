import { apiClient } from "@services/api-client";
import type { FileItem } from "@/features/document/types";

export interface RagAnswerResponseDto {
  answer: string;
  // Bổ sung thêm các trường khác nếu DTO backend có trả về (sources, metadata, ...)
}

export interface RagAskRequestDto {
  documentId: number | string;
  question: string;
}

export const chatApi = {
  askQuestion: async (
    question: string,
    file: FileItem | string | number,
  ): Promise<RagAnswerResponseDto> => {
    const documentId = typeof file === "object" ? file.id : file;

    return apiClient.post<RagAnswerResponseDto>("/rag/ask", {
      documentId,
      question,
    });
  },
};
