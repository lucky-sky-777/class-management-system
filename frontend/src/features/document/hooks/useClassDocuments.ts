// src/features/classDocuments/hooks/useClassDocuments.ts
import { useState, useEffect, useCallback, useRef } from "react";
import {
  classDocumentsAPI,
  formatBytes,
  getExtension,
} from "@/features/document/api";
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

  // Lưu classId đã fetch gần nhất thay vì dùng boolean cờ chết
  const lastClassIdRef = useRef<string | number | undefined>(undefined);

  // LOGIC: Ánh xạ từ DTO của Backend sang FileItem giao diện
  const mapDtoToFileItem = useCallback(
    (dto: any, fallbackDirId?: string | number): FileItem => {
      const fullName =
        dto.title ||
        dto.file_name ||
        dto.fileName ||
        dto.name ||
        "Tài liệu không tên";

      const rawMime = dto.mime_type || dto.mimeType || dto.metadata?.mime_type;
      const resolvedExt = (
        dto.file_extension ||
        dto.fileExtension ||
        getExtension(fullName, rawMime)
      )
        .replace(".", "")
        .toLowerCase();

      const rawSize =
        dto.file_size ?? dto.size ?? dto.fileSize ?? dto.metadata?.file_size;
      const formattedSize =
        rawSize !== undefined && rawSize !== null ? formatBytes(rawSize) : "--";

      const createdDate = dto.created_at || dto.createdAt;

      let parsedTags: string[] = [];
      if (Array.isArray(dto.tags)) {
        parsedTags = dto.tags.map((t: any) =>
          typeof t === "string" ? t : t.name,
        );
      } else if (typeof dto.tags === "string" && dto.tags.trim()) {
        parsedTags = dto.tags.split(",").map((t: string) => t.trim());
      }

      // TRÍCH XUẤT DIRECTORY ID PHÒNG THỦ
      const extractedDirectoryId =
        dto.directoryId ??
        dto.directory_id ??
        dto.folderId ??
        dto.folder_id ??
        dto.rawMetadata?.directory_id ??
        fallbackDirId;

      return {
        id: dto.id,
        name: fullName,
        size: formattedSize,
        directoryId: extractedDirectoryId, // <-- Gán trực tiếp directoryId vào FileItem
        folderId: extractedDirectoryId,
        uploader:
          dto.creator_name ||
          dto.creatorName ||
          dto.uploader ||
          `User ${dto.creator_user_id || ""}`.trim() ||
          "Bạn đăng",
        uploadDate: createdDate || "Vừa xong",
        fileExtension: resolvedExt,
        isLiked: Boolean(dto.is_liked || dto.isLiked),
        description: dto.description || "",
        downloads:
          dto.downloads ?? dto.download_count ?? dto.downloadCount ?? 0,
        likes: dto.likes ?? dto.like_count ?? dto.likesCount ?? 0,
        tags: parsedTags,
        rawMetadata: dto.metadata || {
          file_name: fullName,
          file_size: rawSize,
          mime_type: rawMime,
          directory_id: extractedDirectoryId,
        },
      };
    },
    [],
  );

  const fetchRootData = useCallback(async () => {
    if (!classId) return;

    if (lastClassIdRef.current === classId) return;

    setIsLoading(true);
    setCurrentFolder(null);
    setFolderFiles([]);
    setFolders([]);
    setRecentFiles([]);

    try {
      const [foldersRes, filesRes] = await Promise.all([
        classDocumentsAPI.getFolders(classId),
        classDocumentsAPI.getRecentFiles(),
      ]);

      setFolders(foldersRes.data || []);

      // Chuẩn hóa danh sách recentFiles
      const rawRecent = Array.isArray(filesRes)
        ? filesRes
        : filesRes?.data || [];
      const mappedRecent = rawRecent.map((f: any) => mapDtoToFileItem(f));
      setRecentFiles(mappedRecent);

      lastClassIdRef.current = classId;
    } catch (error) {
      console.error("Lỗi khi tải dữ liệu trang gốc:", error);
    } finally {
      setIsLoading(false);
    }
  }, [classId, mapDtoToFileItem]);

  useEffect(() => {
    fetchRootData();
  }, [fetchRootData]);

  // Mở thư mục
  const handleOpenFolder = async (folder: FolderItem) => {
    if (!classId || !folder?.id) return;
    setCurrentFolder(folder);
    setIsFolderLoading(true);

    try {
      const res = await classDocumentsAPI.getDocumentsByDirectory(
        classId,
        folder.id,
      );
      const rawList = res.data || [];
      // Truyền folder.id vào làm fallbackDirId
      const documents = rawList.map((doc: any) =>
        mapDtoToFileItem(doc, folder.id),
      );
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

      setFolders((prevFolders) => [...(prevFolders || []), newFolder]);
    } catch (error) {
      console.error("Lỗi khi tạo thư mục:", error);
    }
  };

  const handleRenameFolder = async (id: string | number, newName: string) => {
    if (!classId) return;
    try {
      await classDocumentsAPI.renameFolder(classId, id, newName);

      setFolders((prevFolders) =>
        prevFolders.map((folder) =>
          folder.id === id ? { ...folder, name: newName } : folder,
        ),
      );

      if (currentFolder?.id === id) {
        setCurrentFolder((prev) => (prev ? { ...prev, name: newName } : null));
      }
    } catch (error) {
      console.error("Lỗi khi sửa tên thư mục:", error);
    }
  };

  const handleDeleteFolder = async (id: string | number) => {
    if (!classId) return;
    try {
      await classDocumentsAPI.deleteFolder(classId, id);

      setFolders((prevFolders) =>
        prevFolders.filter((folder) => folder.id !== id),
      );

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

      const res = await classDocumentsAPI.getDocumentsByDirectory(
        classId,
        folderId,
      );
      setFolderFiles(
        (res.data || []).map((doc: any) => mapDtoToFileItem(doc, folderId)),
      );
    } catch (error) {
      console.error("Lỗi khi tải lên tài liệu:", error);
      throw error;
    } finally {
      setIsLoading(false);
    }
  };

  const handleViewFile = async (
    file: FileItem,
    directoryId?: string | number,
  ) => {
    if (!classId) return;
    const targetDirId = directoryId ?? file.directoryId ?? currentFolder?.id;
    if (!targetDirId) {
      console.error("Thiếu directoryId để tải file");
      return;
    }

    try {
      setIsPreviewLoading(true);
      setViewingFile(file);

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

  const handleGoToRoot = () => {
    setCurrentFolder(null);
    setFolderFiles([]);
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
