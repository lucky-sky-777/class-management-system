import type { FolderItem, FileItem } from "../types";

export const classDocumentsAPI = {
  getFolders: async (): Promise<FolderItem[]> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
    return [
      { id: 1, name: "Tài liệu tham khảo", itemCount: 5 },
      { id: 2, name: "Báo cáo cuối kì", itemCount: 9 },
    ];
  },

  getRecentFiles: async (): Promise<FileItem[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return [
      {
        id: 1,
        name: "NCKHSV 26-27.pdf",
        size: "71.4 MB",
        uploader: "Bạn đăng",
        fileExtension: "pdf",
      },
      {
        id: 2,
        name: "Tài liệu đặc tả.docx",
        size: "50.1 MB",
        uploader: "Bạn đăng",
        fileExtension: "docx",
      },
      {
        id: 3,
        name: "NCKHSV 26-27.pptx",
        size: "171.4 MB",
        uploader: "Bạn đăng",
        fileExtension: "pptx",
      },
    ];
  },

  getFilesInFolder: async (folderId: string | number): Promise<FileItem[]> => {
    await new Promise((resolve) => setTimeout(resolve, 400)); // Giả lập độ trễ mạng
    return [
      { id: 101, name: "Tài liệu hướng dẫn.pdf", size: "2.5 MB", uploader: "Giáo viên", fileExtension: "pdf" },
      { id: 102, name: "Danh sách nhóm.xlsx", size: "1.1 MB", uploader: "Giáo viên", fileExtension: "xlsx" },
      { id: 103, name: "Slide bài giảng.pptx", size: "15 MB", uploader: "Giáo viên", fileExtension: "pptx" },
      { id: 104, name: "Source code tham khảo.zip", size: "45 MB", uploader: "Giáo viên", fileExtension: "zip" },
    ];
  },
};