import type {
  FolderItem,
  FileItem,
  CreateDirectoryRequestDto,
} from "@features/document/types";
import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";

export const classDocumentsAPI = {
  //api directory (thư mục)
  getFolders: async (
    classId: string | number,
  ): Promise<ResponseDTO<FolderItem[]>> => {
    return apiClient.get(`/directories/classes/${classId}`);
  },

  createFolder: async (
    classId: number | string,
    data: CreateDirectoryRequestDto,
  ): Promise<ResponseDTO<FolderItem>> => {
    return apiClient.post<ResponseDTO<FolderItem>>(
      `/directories/classes/${classId}`,
      data,
    );
  },

  renameFolder: async (
    directoryId: number | string,
    newName: string,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.patch<ResponseDTO<void>>(`/directories/${directoryId}`, {
      name: newName,
    });
  },

  deleteFolder: async (
    directoryId: number | string,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.delete<ResponseDTO<void>>(`/directories/${directoryId}`);
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
      {
        id: 101,
        name: "Tài liệu hướng dẫn.pdf",
        size: "2.5 MB",
        uploader: "Giáo viên",
        fileExtension: "pdf",
      },
      {
        id: 102,
        name: "Danh sách nhóm.xlsx",
        size: "1.1 MB",
        uploader: "Giáo viên",
        fileExtension: "xlsx",
      },
      {
        id: 103,
        name: "Slide bài giảng.pptx",
        size: "15 MB",
        uploader: "Giáo viên",
        fileExtension: "pptx",
      },
      {
        id: 104,
        name: "Source code tham khảo.zip",
        size: "45 MB",
        uploader: "Giáo viên",
        fileExtension: "zip",
      },
    ];
  },

  getFileDetail: async (fileId: string | number): Promise<FileItem> => {
    await new Promise((resolve) => setTimeout(resolve, 400));
    return {
      id: fileId,
      name: "NCKHSV 26-27.docx",
      size: "7.1 MB",
      uploader: "Phong Hào",
      uploadDate: "20/07/2026",
      fileExtension: "docx",
      downloads: 125,
      likes: 10,
      description:
        "Tài liệu nghiên cứu khoa học sinh viên năm học 2026 - 2027. Đề tài Xây dựng nền tảng chia sẻ tài liệu và học nhóm sinh viên",
      tags: ["NCKHSV 26-27", "QNU"],
      isLiked: false,
      fileUrl:
        "https://docs.google.com/gview?url=https://calibre-ebook.com/downloads/demos/demo.docx&embedded=true",
    };
  },

  renameFile: async (id: string | number, newName: string): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
  },

  deleteFile: async (id: string | number): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
  },
};
