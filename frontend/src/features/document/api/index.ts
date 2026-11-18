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

export interface DeleteDocumentRequestDto {
  documentIds?: (number | string)[];
  // Khai báo thêm các trường khác nếu DTO bên Java có quy định
  [key: string]: any;
}

export const classDocumentsAPI = {
  // --- DIRECTORY ---
  getFolders: async (
    classId: string | number,
  ): Promise<ResponseDTO<FolderItem[]>> => {
    return apiClient.get(`/classes/${classId}/directories`);
  },

  createFolder: async (
    classId: string | number,
    data: CreateDirectoryRequestDto,
  ): Promise<ResponseDTO<FolderItem>> => {
    return apiClient.post<ResponseDTO<FolderItem>>(
      `/classes/${classId}/directories`,
      data,
    );
  },

  renameFolder: async (
    classId: string | number,
    directoryId: number | string,
    newName: string,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.patch<ResponseDTO<void>>(
      `/classes/${classId}/directories/${directoryId}`,
      {
        name: newName,
      },
    );
  },

  deleteFolder: async (
    classId: string | number,
    directoryId: number | string,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.delete<ResponseDTO<void>>(
      `/classes/${classId}/directories/${directoryId}`,
    );
  },

  // --- DOCUMENT ---

  // ĐÃ SỬA: Bỏ /documents ở cuối để khớp @GetMapping("/classes/{classId}/directories/{directoryId}")
  getDocumentsByDirectory: async (
    classId: string | number,
    directoryId: string | number,
  ): Promise<ResponseDTO<DocumentResponseDto[]>> => {
    return apiClient.get(`/classes/${classId}/directories/${directoryId}`);
  },

  uploadDocuments: async (
    classId: string | number,
    directoryId: string | number,
    files: File[],
  ): Promise<ResponseDTO<any>> => {
    const formData = new FormData();
    files.forEach((file) => {
      formData.append("files", file);
    });

    return apiClient.post(
      `/classes/${classId}/directories/${directoryId}/documents`,
      formData,
    );
  },

  // ĐÃ SỬA: Khớp @GetMapping("/{documentId}/file")
  getFileBlob: async (documentId: string | number): Promise<Blob> => {
    return apiClient.get(`/documents/${documentId}/file`, {
      axiosConfig: {
        responseType: "blob",
      },
    });
  },

  // THÊM MỚI: Khớp @GetMapping("/documents/{documentId}/download")
  downloadFile: async (documentId: string | number): Promise<Blob> => {
    return apiClient.get(`/documents/${documentId}/download`, {
      axiosConfig: {
        responseType: "blob",
      },
    });
  },

  // THÊM MỚI: Khớp @GetMapping("/documents/{documentId}")
  getDocumentDetail: async (
    documentId: string | number,
  ): Promise<ResponseDTO<DocumentResponseDto>> => {
    return apiClient.get(`/documents/${documentId}`);
  },

  // ĐÃ SỬA: Khớp @DeleteMapping("/classes/{classId}/directories/{directoryId}/documents")
  deleteDocuments: async (
    classId: string | number,
    directoryId: string | number,
    data: DeleteDocumentRequestDto,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.delete(
      `/classes/${classId}/directories/${directoryId}/documents`,
      {
        data,
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
    const [detailRes, metaRes, tagRes] = await Promise.allSettled([
      classDocumentsAPI.getDocumentDetail(fileId),
      classDocumentsAPI.getDocumentMetadata(fileId),
      classDocumentsAPI.getDocumentTags(fileId),
    ]);

    // Lấy dữ liệu từ GET /documents/{documentId} (các field trả về dạng snake_case theo @JsonProperty)
    const detailData: any =
      detailRes.status === "fulfilled" ? detailRes.value?.data : null;

    const metaPayload: any =
      metaRes.status === "fulfilled" ? metaRes.value?.data : null;
    const metadataObj = metaPayload?.metadata || metaPayload;

    const tagPayload: any =
      tagRes.status === "fulfilled" ? tagRes.value?.data : null;
    const rawTags = Array.isArray(tagPayload)
      ? tagPayload
      : tagPayload?.data || [];
    const tagList = Array.isArray(rawTags)
      ? rawTags.map((t: any) => (typeof t === "string" ? t : t.name))
      : [];

    const resolvedName =
      detailData?.title ||
      detailData?.file_name ||
      baseFile?.name ||
      metadataObj?.file_name ||
      "Tài liệu";

    const resolvedSize = metadataObj?.file_size
      ? formatBytes(metadataObj.file_size)
      : baseFile?.size || "--";

    const resolvedExtension = getExtension(
      detailData?.file_name || resolvedName,
      metadataObj?.mime_type || baseFile?.fileExtension,
    );

    return {
      id: fileId,
      name: resolvedName,
      size: resolvedSize,
      fileExtension: resolvedExtension,
      uploader:
        detailData?.creator_display_name || baseFile?.uploader || "Người dùng",
      uploadDate: detailData?.created_at || baseFile?.uploadDate || "--",
      downloads: detailData?.download_count ?? baseFile?.downloads ?? 0,
      likes: detailData?.bookmark_count ?? baseFile?.likes ?? 0,
      description: detailData?.description || baseFile?.description || "",
      isLiked: detailData?.is_bookmarked ?? baseFile?.isLiked ?? false,
      fileUrl: baseFile?.fileUrl,
      tags: tagList.length > 0 ? tagList : baseFile?.tags || [],
    };
  },

  // Giữ nguyên mock do Backend DocumentController chưa có endpoint đổi tên document
  renameFile: async (id: string | number, newName: string): Promise<void> => {
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

  // Bookmark / Like
  toggleBookmark: async (
    documentId: string | number,
  ): Promise<ResponseDTO<void>> => {
    return apiClient.post<ResponseDTO<void>>(
      `document-bookmarks/documents/${documentId}`,
      {},
    );
  },
};
