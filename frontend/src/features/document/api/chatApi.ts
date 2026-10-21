import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";
import type { RagAnswerResponseDto } from "@/features/document/types"; // Điều chỉnh path phù hợp

export const chatApi = {
  /**
   * Gửi tài liệu và câu hỏi lên AI sử dụng RAG
   */
  askQuestion: async (
    question: string,
    file: File,
  ): Promise<ResponseDTO<RagAnswerResponseDto>> => {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("question", question);

    return apiClient.post<ResponseDTO<RagAnswerResponseDto>>(
      "/rag/ask",
      formData,
    );
  },
};
