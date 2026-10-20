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
  const handleOpenFolder = async (folder: FolderItem) => {
    setCurrentFolder(folder);
    setIsFolderLoading(true);
    try {
      const files = await classDocumentsAPI.getFilesInFolder(folder.id);
      setFolderFiles(files);
    } catch (error) {
      console.error("Lỗi tải file trong thư mục", error);
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

  // Trong src/features/classDocuments/hooks/useClassDocuments.ts

  const handleUploadDocument = async (
    folderId: string | number,
    files: File[],
    description?: string,
  ) => {
    if (!classId) return;

    try {
      setIsLoading(true);

      // Lặp qua danh sách file đã chọn để tải lên từng file
      for (const file of files) {
        await classDocumentsAPI.uploadDocument(classId, folderId, file, {
          title: file.name,
          description: description || "",
        });
      }

      // Refresh lại danh sách file
      if (currentFolder && currentFolder.id === folderId) {
        const files = await classDocumentsAPI.getFilesInFolder(folderId);
        setFolderFiles(files);
      } else {
        const filesRes = await classDocumentsAPI.getRecentFiles();
        setRecentFiles(filesRes || []);
      }
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
