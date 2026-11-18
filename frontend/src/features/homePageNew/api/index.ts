import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";
import type {
  SearchDocument,
  DocumentResponseDto,
} from "@features/homePageNew/types";

// Helper trích xuất đuôi file (pdf, docx, pptx...)
// Helper trích xuất đuôi file (pdf, docx, pptx...)
const extractExtension = (dto: DocumentResponseDto): string => {
  const source = dto.file_name || dto.title || "";
  const match = source.match(/\.([0-9a-z]+)(?:[?#]|$)/i);
  return match ? match[1].toLowerCase() : "pdf";
};

export const searchDocumentAPI = {
  getRecentSearches: async (): Promise<string[]> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
    return ["Thuật toán K-mean", "Mô hình RAG", "Tài liệu NCKH sinh viên"];
  },

  getRecommendedDocs: async (): Promise<SearchDocument[]> => {
    try {
      const res =
        await apiClient.get<ResponseDTO<DocumentResponseDto[]>>(
          "/recommendations",
        );

      const docs = res.data || [];

      return docs.map((doc) => ({
        id: doc.id,
        title: doc.title || doc.file_name || "Tài liệu chưa đặt tên",
        category: "Tài liệu gợi ý",
        author: doc.creator_display_name || "Thành viên hệ thống",
        date: doc.created_at || "Mới cập nhật",
        downloads: doc.download_count ?? 0,
        fileExtension: extractExtension(doc),
      }));
    } catch (error) {
      console.error("Lỗi khi lấy danh sách tài liệu gợi ý:", error);
      return [];
    }
  },

  searchDocuments: async (term: string): Promise<SearchDocument[]> => {
    await new Promise((resolve) => setTimeout(resolve, 800));
    return Array.from({ length: 3 }, (_, idx) => ({
      id: `search-${idx + 1}`,
      title: `Kết quả tìm kiếm cho: ${term} (Phần ${idx + 1})`,
      category: "Kết quả tìm kiếm • Hệ thống",
      author: "Hệ thống Search",
      date: "Hôm nay",
      downloads: "100",
      fileExtension: "pdf",
    }));
  },
};
