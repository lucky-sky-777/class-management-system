import type {
  FolderItem,
  FileItem,
  CreateDirectoryRequestDto,
  DocumentResponseDto,
  DocumentMetadataResponseDto,
  TagResponse,
} from "@features/document/types";
import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";

// Helper tính dung lượng từ byte
export const formatBytes = (bytes?: number | string): string => {
  if (!bytes) return "0 B";
  const num = typeof bytes === "string" ? parseFloat(bytes) : bytes;
  if (isNaN(num)) return String(bytes);

  const units = ["B", "KB", "MB", "GB"];
  let size = num;
  let unitIndex = 0;
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }
  return `${size.toFixed(1)} ${units[unitIndex]}`;
};

// Helper trích xuất phần mở rộng thông minh
export const getExtension = (fileName?: string, mimeType?: string): string => {
  if (mimeType) {
    const mime = mimeType.toLowerCase();
    if (mime.includes("pdf")) return "pdf";
    if (mime.includes("wordprocessingml") || mime.includes("msword"))
      return "docx";
    if (mime.includes("presentationml") || mime.includes("powerpoint"))
      return "pptx";
    if (mime.includes("spreadsheetml") || mime.includes("excel")) return "xlsx";
    if (mime.includes("image/png")) return "png";
    if (mime.includes("image/jpeg") || mime.includes("image/jpg")) return "jpg";
    if (mime.includes("zip") || mime.includes("compressed")) return "zip";
  }

  if (fileName && fileName.includes(".")) {
    const ext = fileName.split(".").pop()?.trim().toLowerCase();
    if (ext) return ext;
  }

  return "file";
};

export const classDocumentsAPI = {
  // --- DIRECTORY ---
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

  // --- DOCUMENT ---
  getDocumentsByDirectory: async (
    classId: string | number,
    directoryId: string | number,
  ): Promise<ResponseDTO<DocumentResponseDto[]>> => {
    return apiClient.get(
      `/classes/${classId}/directories/${directoryId}/documents`,
    );
  },

  uploadDocuments: async (
    classId: string | number,
    directoryId: string | number,
    files: File[],
  ): Promise<ResponseDTO<string>> => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append("files", file);
    });

    return apiClient.post(
      `/classes/${classId}/directories/${directoryId}/documents`,
      formData,
    );
  },

  getFileBlob: async (
    classId: string | number,
    directoryId: string | number,
    documentId: string | number,
  ): Promise<Blob> => {
    return apiClient.get(
      `/classes/${classId}/directories/${directoryId}/documents/${documentId}/file`,
      {
        axiosConfig: {
          responseType: "blob",
        },
      },
    );
  },

  getDocumentMetadata: async (
    documentId: string | number,
  ): Promise<ResponseDTO<DocumentMetadataResponseDto>> => {
    return apiClient.get(`/documents/${documentId}/metadata`);
  },

  getDocumentTags: async (
    documentId: string | number,
  ): Promise<ResponseDTO<TagResponse[]>> => {
    return apiClient.get(`/document-tags/${documentId}/tags`);
  },

  // --- GỘP THÔNG TIN CẦN THIẾT CHO SIDEBAR/PREVIEW ---
  getFileDetail: async (
    fileId: string | number,
    baseFile?: Partial<FileItem> | null,
  ): Promise<FileItem> => {
    const [metaRes, tagRes] = await Promise.allSettled([
      classDocumentsAPI.getDocumentMetadata(fileId),
      classDocumentsAPI.getDocumentTags(fileId),
    ]);

    // Trích xuất metadata cần thiết: file_size, mime_type
    const metaPayload: any =
      metaRes.status === "fulfilled" ? metaRes.value?.data : null;
    const metadataObj = metaPayload?.metadata || metaPayload;

    // Trích xuất danh sách tags
    const tagPayload: any =
      tagRes.status === "fulfilled" ? tagRes.value?.data : null;
    const rawTags = Array.isArray(tagPayload)
      ? tagPayload
      : tagPayload?.data || [];
    const tagList = Array.isArray(rawTags)
      ? rawTags.map((t: any) => (typeof t === "string" ? t : t.name))
      : [];

    const resolvedName = baseFile?.name || metadataObj?.file_name || "Tài liệu";

    // Tính dung lượng: ưu tiên từ metadata backend -> file ban đầu
    const resolvedSize = metadataObj?.file_size
      ? formatBytes(metadataObj.file_size)
      : baseFile?.size || "--";

    // Tính đuôi file: kết hợp tên và mime_type
    const resolvedExtension = getExtension(
      resolvedName,
      metadataObj?.mime_type || baseFile?.fileExtension,
    );

    return {
      id: fileId,
      name: resolvedName,
      size: resolvedSize,
      fileExtension: resolvedExtension,
      // Lấy toàn bộ thông tin thực tế từ file đã click
      uploader: baseFile?.uploader || "Người dùng",
      uploadDate: baseFile?.uploadDate || "--",
      downloads: baseFile?.downloads ?? 0,
      likes: baseFile?.likes ?? 0,
      description: baseFile?.description || "",
      isLiked: baseFile?.isLiked ?? false,
      fileUrl: baseFile?.fileUrl,
      // Gộp tags (ưu tiên tags mới từ API)
      tags: tagList.length > 0 ? tagList : baseFile?.tags || [],
    };
  },

  renameFile: async (id: string | number, newName: string): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
  },

  deleteFile: async (id: string | number): Promise<void> => {
    await new Promise((resolve) => setTimeout(resolve, 300));
  },

  getRecentFiles: async (): Promise<FileItem[]> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return [
      {
        id: 1,
        name: "NCKHSV 26-27.pdf",
        size: "71.4 MB",
        uploader: "Đặng Phong Hào",
        uploadDate: "21/10/2026",
        fileExtension: "pdf",
        downloads: 12,
        likes: 5,
        description: "Báo cáo nghiên cứu khoa học sinh viên",
        tags: ["Nghiên cứu", "Báo cáo"],
      },
      {
        id: 2,
        name: "Tài liệu đặc tả.docx",
        size: "50.1 MB",
        uploader: "Đặng Phong Hào",
        uploadDate: "22/10/2026",
        fileExtension: "docx",
        downloads: 8,
        likes: 3,
        description: "Tài liệu đặc tả yêu cầu phần mềm",
        tags: ["SRS", "Thiết kế"],
      },
      {
        id: 3,
        name: "NCKHSV 26-27.pptx",
        size: "171.4 MB",
        uploader: "Đặng Phong Hào",
        uploadDate: "25/10/2026",
        fileExtension: "pptx",
        downloads: 20,
        likes: 9,
        description: "Slide thuyết trình tổng kết",
        tags: ["Slide", "Thuyết trình"],
      },
    ];
  },
};
