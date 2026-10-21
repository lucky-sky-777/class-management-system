// src/features/classDocuments/hooks/useClassDocuments.ts
import { useState, useEffect, useCallback, useRef } from "react";
import { classDocumentsAPI } from "@/features/document/api";
import type { FolderItem, FileItem } from "@/features/document/types";

export const useClassDocuments = (classId: string | number | undefined) => {
  const [folders, setFolders] = useState<FolderItem[]>([]);
  const [recentFiles, setRecentFiles] = useState<FileItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // STATE QUẢN LÝ THƯ MỤC HIỆN TẠI
  const [currentFolder, setCurrentFolder] = useState<FolderItem | null>(null);
  const [folderFiles, setFolderFiles] = useState<FileItem[]>([]);
  const [isFolderLoading, setIsFolderLoading] = useState(false);

  // STATE XEM TRƯỚC TÀI LIỆU (PREVIEW BLOB)
  const [viewingFile, setViewingFile] = useState<FileItem | null>(null);
  const [previewBlobUrl, setPreviewBlobUrl] = useState<string | null>(null);
  const [isPreviewLoading, setIsPreviewLoading] = useState(false);

  const hasFetched = useRef(false);

  const fetchRootData = useCallback(async () => {
    if (hasFetched.current) return;
    if (!classId) return;
    setIsLoading(true);
    try {
      const [foldersRes, filesRes] = await Promise.all([
        classDocumentsAPI.getFolders(classId),
        classDocumentsAPI.getRecentFiles(),
      ]);

      setFolders(foldersRes.data || []);

      // nếu getRecentFiles gọi API thật và trả về ResponseDTO thì cũng thêm .data
      setRecentFiles(filesRes || []);

      hasFetched.current = true;
    } catch (error) {
      console.error("Lỗi:", error);
    } finally {
      setIsLoading(false);
    }
  }, [classId]);

  useEffect(() => {
    queueMicrotask(() => {
      fetchRootData();
    });
  }, [fetchRootData]);

  // LOGIC: Mở một thư mục
  // Helper ánh xạ từ DocumentResponseDto của Backend sang FileItem của UI (nếu cần đổi tên field)
  const mapDtoToFileItem = (dto: any): FileItem => {
    // 1. Tên file: ưu tiên title hoặc file_name
    const fullName =
      dto.title ||
      dto.file_name ||
      dto.fileName ||
      dto.name ||
      "Tài liệu không tên";

    // 2. Phần mở rộng: tự bóc tách từ tên file (ví dụ "abc.pdf" -> "pdf")
    const extractedExt = fullName.includes(".")
      ? fullName.split(".").pop()?.toLowerCase() || "file"
      : "file";

    // 3. Dung lượng: kiểm tra nếu backend có trả về (size / file_size)
    let formattedSize = "--";
    const rawSize = dto.file_size ?? dto.size ?? dto.fileSize;
    if (rawSize !== undefined && rawSize !== null && !isNaN(Number(rawSize))) {
      formattedSize = `${(Number(rawSize) / (1024 * 1024)).toFixed(1)} MB`;
    }

    // 4. Ngày tạo: Backend trả về chuỗi "08:41, 21/10/2026"
    const createdDate = dto.created_at || dto.createdAt;

    // 5. Xử lý từ khóa (tags): chuẩn hóa nếu backend trả về string hoặc array
    let parsedTags: string[] = [];
    if (Array.isArray(dto.tags)) {
      parsedTags = dto.tags;
    } else if (typeof dto.tags === "string" && dto.tags.trim()) {
      parsedTags = dto.tags.split(",").map((t: string) => t.trim());
    }

    return {
      id: dto.id,
      name: fullName,
      size: formattedSize,
      uploader:
        dto.creator_name ||
        dto.creatorName ||
        dto.uploader ||
        `User ${dto.creator_user_id || ""}`.trim() ||
        "Người dùng",
      uploadDate: createdDate || undefined,
      fileExtension: (
        dto.file_extension ||
        dto.fileExtension ||
        extractedExt
      ).replace(".", ""),
      isLiked: Boolean(dto.is_liked || dto.isLiked),

      // Các trường bổ sung cho DocumentSidebar:
      description: dto.description || "",
      downloads: dto.downloads ?? dto.download_count ?? dto.downloadCount ?? 0,
      likes: dto.likes ?? dto.like_count ?? dto.likesCount ?? 0,
      tags: parsedTags,
    };
  };

  // Trong useClassDocuments:
  const handleOpenFolder = async (folder: FolderItem) => {
    if (!classId) return;
    setCurrentFolder(folder);
    setIsFolderLoading(true);

    try {
      const res = await classDocumentsAPI.getDocumentsByDirectory(
        classId,
        folder.id,
      );
      const documents = (res.data || []).map(mapDtoToFileItem);
      setFolderFiles(documents);
    } catch (error) {
      console.error("Lỗi khi tải danh sách tài liệu trong thư mục:", error);
      setFolderFiles([]);
    } finally {
      setIsFolderLoading(false);
    }
  };

  const handleCreateFolder = async (folderName: string) => {
    if (!classId) return;
    try {
      const res = await classDocumentsAPI.createFolder(classId, {
        name: folderName,
      });
      const folderData = res.data;

      const newFolder: FolderItem = {
        id: folderData.id,
        name: folderData.name,
        itemCount: 0,
      };

      setFolders((prevFolders) => {
        const safePrevFolders = Array.isArray(prevFolders) ? prevFolders : [];
        return [...safePrevFolders, newFolder];
      });
    } catch (error) {
      console.error("Lỗi khi tạo thư mục:", error);
    }
  };

  const handleRenameFolder = async (id: string | number, newName: string) => {
    try {
      await classDocumentsAPI.renameFolder(id, newName);

      // 1. Cập nhật tên trong danh sách thư mục ngoài màn hình Gốc
      setFolders((prevFolders) =>
        prevFolders.map((folder) =>
          folder.id === id ? { ...folder, name: newName } : folder,
        ),
      );

      // 2. Nếu đang đứng BÊN TRONG thư mục đó thì cập nhật luôn Breadcrumbs
      if (currentFolder?.id === id) {
        setCurrentFolder((prev) => (prev ? { ...prev, name: newName } : null));
      }
    } catch (error) {
      console.error("Lỗi khi sửa tên thư mục:", error);
    }
  };

  const handleDeleteFolder = async (id: string | number) => {
    try {
      await classDocumentsAPI.deleteFolder(id);

      // Xóa thư mục khỏi danh sách hiện tại
      setFolders((prevFolders) =>
        prevFolders.filter((folder) => folder.id !== id),
      );

      // Nếu người dùng bằng cách nào đó đang ở trong thư mục bị xóa, đẩy họ ra ngoài Gốc
      if (currentFolder?.id === id) {
        handleGoToRoot();
      }
    } catch (error) {
      console.error("Lỗi khi xóa thư mục:", error);
    }
  };

  const handleUploadDocument = async (
    folderId: string | number,
    files: File[],
  ) => {
    if (!classId || files.length === 0) return;

    try {
      setIsLoading(true);
      await classDocumentsAPI.uploadDocuments(classId, folderId, files);

      // Load lại danh sách file thật từ backend
      const res = await classDocumentsAPI.getDocumentsByDirectory(
        classId,
        folderId,
      );
      setFolderFiles((res.data || []).map(mapDtoToFileItem));
    } catch (error) {
      console.error("Lỗi khi tải lên tài liệu:", error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  // LOGIC: Lấy file từ backend về render xem trước
  const handleViewFile = async (
    file: FileItem,
    directoryId?: string | number,
  ) => {
    if (!classId) return;
    const targetDirId = directoryId ?? currentFolder?.id;
    if (!targetDirId) {
      console.error("Thiếu directoryId để tải file");
      return;
    }

    try {
      setIsPreviewLoading(true);
      setViewingFile(file);

      // Thu hồi URL cũ nếu có trước khi tạo cái mới
      if (previewBlobUrl) {
        URL.revokeObjectURL(previewBlobUrl);
        setPreviewBlobUrl(null);
      }

      const blob = await classDocumentsAPI.getFileBlob(
        classId,
        targetDirId,
        file.id,
      );
      const url = URL.createObjectURL(blob);
      setPreviewBlobUrl(url);
    } catch (error) {
      console.error("Lỗi khi lấy dữ liệu stream file:", error);
      setViewingFile(null);
      setPreviewBlobUrl(null);
    } finally {
      setIsPreviewLoading(false);
    }
  };

  // Đóng Modal xem trước và giải phóng RAM
  const handleCloseViewer = () => {
    if (previewBlobUrl) {
      URL.revokeObjectURL(previewBlobUrl);
    }
    setViewingFile(null);
    setPreviewBlobUrl(null);
  };

  const handleRenameFile = async (id: string | number, newName: string) => {
    try {
      await classDocumentsAPI.renameFile(id, newName);

      const updateFiles = (prevFiles: FileItem[]) =>
        prevFiles.map((file) =>
          file.id === id ? { ...file, name: newName } : file,
        );

      setRecentFiles(updateFiles);
      setFolderFiles(updateFiles);
    } catch (error) {
      console.error("Lỗi khi đổi tên file:", error);
    }
  };

  const handleDeleteFile = async (id: string | number) => {
    try {
      await classDocumentsAPI.deleteFile(id);

      const filterFiles = (prevFiles: FileItem[]) =>
        prevFiles.filter((file) => file.id !== id);

      setRecentFiles(filterFiles);
      setFolderFiles(filterFiles);
    } catch (error) {
      console.error("Lỗi khi xóa file:", error);
    }
  };

  // Trở về thư mục gốc
  const handleGoToRoot = () => {
    setCurrentFolder(null);
    setFolderFiles([]); // Xóa data tạm
  };

  const handleSearch = (term: string) => {
    console.log("Tìm kiếm:", term);
  };

  return {
    folders,
    recentFiles,
    isLoading,
    currentFolder,
    folderFiles,
    isFolderLoading,
    viewingFile,
    previewBlobUrl,
    isPreviewLoading,
    handleViewFile,
    handleCloseViewer,
    handleOpenFolder,
    handleGoToRoot,
    handleCreateFolder,
    handleSearch,
    handleRenameFolder,
    handleDeleteFolder,
    handleDeleteFile,
    handleRenameFile,
    handleUploadDocument,
  };
};
