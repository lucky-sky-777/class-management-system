import type { SearchDocument } from "@features/homePageNew/types";

export const searchDocumentAPI = {
  getRecentSearches: async (): Promise<string[]> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
    return ["Thuật toán K-mean", "Mô hình RAG", "Tài liệu NCKH sinh viên"];
  },

  getRecommendedDocs: async (): Promise<SearchDocument[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return Array.from({ length: 4 }, (_, idx) => ({
      id: idx + 1,
      title: "Tài liệu nghiên cứu khoa học sinh viên năm 2026 - 2027",
      category: "Tài liệu tham khảo • NCKHSV",
      author: "Đặng Phong Hào",
      date: "Ngày 13/7/2026",
      downloads: "2.2k",
      fileExtension: idx % 2 === 0 ? "docx" : "pptx", // Mock đuôi file
    }));
  },

  searchDocuments: async (term: string): Promise<SearchDocument[]> => {
    await new Promise((resolve) => setTimeout(resolve, 800)); // Giả lập delay
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