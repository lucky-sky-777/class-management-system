import type { FileItem } from "@/features/document/types";

export const favoriteAPI = {
  getFavoriteDocuments: async (): Promise<FileItem[]> => {
    // Giả lập delay mạng
    await new Promise((resolve) => setTimeout(resolve, 500));
    
    return [
      {
        id: "fav1",
        name: "Kiến trúc Microservices.pdf",
        size: "2.4 MB",
        uploader: "Phong Hào",
        uploadDate: "20/07/2026",
        fileExtension: "pdf",
        downloads: 342,
        likes: 56,
        tags: ["System Design", "Backend"],
        isLiked: true,
      },
      {
        id: "fav2",
        name: "Đặc tả yêu cầu phần mềm.docx",
        size: "1.1 MB",
        uploader: "Nhóm KTPM46",
        uploadDate: "15/08/2026",
        fileExtension: "docx",
        downloads: 120,
        likes: 24,
        tags: ["Requirement", "Doc"],
        isLiked: true,
      },
    ];
  }
};