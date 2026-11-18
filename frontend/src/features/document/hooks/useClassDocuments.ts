// src/features/document/hooks/useClassDocuments.ts
import { useState, useEffect, useCallback, useRef } from "react";
import {
  classDocumentsAPI,
  formatBytes,
  getExtension,
} from "@/features/document/api";
import type {
  FolderItem,
  FileItem,
  DocumentResponseDto,
} from "@/features/document/types";

export const useClassDocuments = (classId: string | number | undefined) => {
  const [folders, setFolders] = useState<FolderItem[]>([]);
  const [recentFiles, setRecentFiles] = useState<FileItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Thư mục hiện tại
  const [currentFolder, setCurrentFolder] = useState<FolderItem | null>(null);
  const [folderFiles, setFolderFiles] = useState<FileItem[]>([]);
  const [isFolderLoading, setIsFolderLoading] = useState(false);

  // Lưu tập ID các tài liệu đã được người dùng yêu thích để đồng bộ xuyên suốt (kể cả khi F5)
  const [bookmarkedIds, setBookmarkedIds] = useState<Set<string>>(new Set());
  const bookmarkedIdsRef = useRef<Set<string>>(new Set());

  const lastClassIdRef = useRef<string | number | undefined>(undefined);

  // Ánh xạ từ DTO Backend sang FileItem UI
  const mapDtoToFileItem = useCallback(
    (
      dto: DocumentResponseDto | any,
      fallbackDirId?: string | number,
      overrideBookmarkedSet?: Set<string>,
    ): FileItem => {
      const fileName = dto.file_name || dto.fileName || dto.name || "";
      const displayTitle = dto.title || fileName || "Tài liệu không tên";

      const rawExt = dto.file_extension || dto.fileExtension;
      const resolvedExt = (rawExt || getExtension(fileName || displayTitle))
        .replace(".", "")
        .toLowerCase();

      const rawSize = dto.file_size ?? dto.size;
      const formattedSize =
        rawSize !== undefined && rawSize !== null ? formatBytes(rawSize) : "--";

      let parsedTags: string[] = [];
      if (Array.isArray(dto.tags)) {
        parsedTags = dto.tags.map((t: any) =>
          typeof t === "string" ? t : t.name,
        );
      }

      const extractedDirectoryId =
        dto.directoryId ?? dto.directory_id ?? fallbackDirId;

      const activeSet = overrideBookmarkedSet ?? bookmarkedIdsRef.current;
      const isItemLiked = Boolean(
        dto.is_bookmarked ??
        dto.isLiked ??
        dto.isBookmarked ??
        activeSet.has(String(dto.id)),
      );

      return {
        id: dto.id,
        name: displayTitle,
        size: formattedSize,
        directoryId: extractedDirectoryId,
        uploader: dto.creator_display_name || dto.uploader || "Người dùng",
        uploaderAvatar: dto.creator_avatar_url,
        uploadDate: dto.created_at || dto.createdAt || "Vừa xong",
        fileExtension: resolvedExt,
        views: Number(dto.view_count ?? 0),
        downloads: Number(dto.download_count ?? 0),
        likes: Number(dto.bookmark_count ?? dto.likes ?? 0),
        isLiked: isItemLiked,
        description: dto.description || "",
        tags: parsedTags,
        rawMetadata: {
          file_name: fileName,
          file_size: rawSize,
          directory_id: extractedDirectoryId,
        },
      };
    },
    [],
  );

  // Hàm phụ trợ lấy danh sách ID đã bookmark từ Backend (tương thích linh hoạt tên hàm API)
  const fetchUserBookmarkedIds = async (): Promise<Set<string>> => {
    try {
      const apiAny = classDocumentsAPI as any;
      const fetchFn =
        apiAny.getBookmarkedDocuments ||
        apiAny.getBookmarks ||
        apiAny.getFavorites ||
        apiAny.getMyBookmarks;

      if (typeof fetchFn !== "function") return new Set();

      const res = await fetchFn();
      const list = Array.isArray(res) ? res : res?.data || [];
      return new Set(
        list
          .map((item: any) =>
            String(item.id ?? item.document_id ?? item.documentId),
          )
          .filter(Boolean),
      );
    } catch {
      return new Set();
    }
  };

  const fetchRootData = useCallback(async () => {
    if (!classId) return;
    if (lastClassIdRef.current === classId) return;

    setIsLoading(true);
    setCurrentFolder(null);
    setFolderFiles([]);
    setFolders([]);
    setRecentFiles([]);

    try {
      const [foldersRes, filesRes, fetchedBookmarks] = await Promise.all([
        classDocumentsAPI.getFolders(classId),
        classDocumentsAPI.getRecentFiles(),
        fetchUserBookmarkedIds(),
      ]);

      bookmarkedIdsRef.current = fetchedBookmarks;
      setBookmarkedIds(fetchedBookmarks);
      setFolders(foldersRes.data || []);

      const rawRecent = Array.isArray(filesRes)
        ? filesRes
        : (filesRes as any)?.data || [];
      setRecentFiles(
        rawRecent.map((f: any) =>
          mapDtoToFileItem(f, undefined, fetchedBookmarks),
        ),
      );

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

  // Đồng bộ trạng thái Tim (Yêu thích) cho cả card trong thư mục và màn hình chi tiết
  const handleToggleFileLike = async (
    id: string | number,
    explicitNextState?: boolean,
  ) => {
    const strId = String(id);
    const currentFile = [...folderFiles, ...recentFiles].find(
      (f) => String(f.id) === strId,
    );
    const nextLiked =
      explicitNextState !== undefined
        ? explicitNextState
        : !(currentFile?.isLiked ?? bookmarkedIdsRef.current.has(strId));

    // 1. Cập nhật ngay UI (Optimistic Update)
    const nextSet = new Set(bookmarkedIdsRef.current);
    if (nextLiked) nextSet.add(strId);
    else nextSet.delete(strId);

    bookmarkedIdsRef.current = nextSet;
    setBookmarkedIds(nextSet);

    const applyLike = (list: FileItem[]) =>
      list.map((f) =>
        String(f.id) === strId ? { ...f, isLiked: nextLiked } : f,
      );
    setFolderFiles(applyLike);
    setRecentFiles(applyLike);

    // 2. Gọi API Backend nếu người dùng bấm trực tiếp từ Card
    if (explicitNextState === undefined) {
      try {
        await classDocumentsAPI.toggleBookmark(id);
      } catch (error) {
        // Hoàn tác nếu API thất bại
        const revertSet = new Set(bookmarkedIdsRef.current);
        if (!nextLiked) revertSet.add(strId);
        else revertSet.delete(strId);

        bookmarkedIdsRef.current = revertSet;
        setBookmarkedIds(revertSet);

        const revertLike = (list: FileItem[]) =>
          list.map((f) =>
            String(f.id) === strId ? { ...f, isLiked: !nextLiked } : f,
          );
        setFolderFiles(revertLike);
        setRecentFiles(revertLike);
        console.error("Lỗi yêu thích tài liệu:", error);
      }
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

      setFolders((prev) => [...(prev || []), newFolder]);
    } catch (error) {
      console.error("Lỗi khi tạo thư mục:", error);
    }
  };

  const handleRenameFolder = async (id: string | number, newName: string) => {
    if (!classId) return;
    try {
      await classDocumentsAPI.renameFolder(classId, id, newName);
      setFolders((prev) =>
        prev.map((f) => (f.id === id ? { ...f, name: newName } : f)),
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
      setFolders((prev) => prev.filter((f) => f.id !== id));
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

  const handleDeleteFile = async (
    id: string | number,
    folderId?: string | number,
  ) => {
    const targetDirId = folderId ?? currentFolder?.id;
    if (!classId || !targetDirId) {
      console.error("Thiếu classId hoặc directoryId để thực hiện xóa");
      return;
    }

    try {
      await classDocumentsAPI.deleteDocuments(classId, targetDirId, {
        documentIds: [Number(id)],
      });

      const filterFiles = (prev: FileItem[]) => prev.filter((f) => f.id !== id);
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
    bookmarkedIds,
    handleOpenFolder,
    handleGoToRoot,
    handleCreateFolder,
    handleSearch,
    handleRenameFolder,
    handleDeleteFolder,
    handleDeleteFile,
    handleRenameFile,
    handleUploadDocument,
    handleToggleFileLike,
  };
};
