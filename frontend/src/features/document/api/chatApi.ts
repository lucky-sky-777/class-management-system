// src/features/document/api/chatApi.ts
import { apiClient } from "@services/api-client";
import type {
  FileItem,
  ChatContext,
  RagAnswerResponseDto,
  RagAskRequestDto,
} from "@/features/document/types";

export const chatApi = {
  // 1. POST /api/rag: Global
  askGlobal: async (question: string): Promise<RagAnswerResponseDto> => {
    const body: RagAskRequestDto = { question };
    return apiClient.post<RagAnswerResponseDto>("/rag", body);
  },

  // 2. POST /api/rag/class/{classId}: Classroom
  askInClass: async (
    classId: string | number,
    question: string,
  ): Promise<RagAnswerResponseDto> => {
    const body: RagAskRequestDto = { question };
    return apiClient.post<RagAnswerResponseDto>(`rag/class/${classId}`, body);
  },

  // 3. POST /api/rag/class/{classId}/document/{documentId}: Document
  askInDocument: async (
    classId: string | number = 0,
    documentId: string | number,
    question: string,
  ): Promise<RagAnswerResponseDto> => {
    const body: RagAskRequestDto = { question };
    return apiClient.post<RagAnswerResponseDto>(
      `rag/class/${classId}/document/${documentId}`,
      body,
    );
  },

  // Router gọi theo Context
  askByContext: async (
    context: ChatContext,
    question: string,
  ): Promise<RagAnswerResponseDto> => {
    switch (context.type) {
      case "classroom":
        return chatApi.askInClass(context.classId, question);

      case "document": {
        const classId = context.classId ?? 0;
        return chatApi.askInDocument(classId, context.file.id, question);
      }

      case "global":
      default:
        return chatApi.askGlobal(question);
    }
  },

  // Hàm tương thích ngược nếu còn chỗ dùng cũ
  askQuestion: async (
    question: string,
    file: FileItem | string | number,
    classId: string | number = 0,
  ): Promise<RagAnswerResponseDto> => {
    const documentId = typeof file === "object" ? file.id : file;
    return chatApi.askInDocument(classId, documentId, question);
  },
};
