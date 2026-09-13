// src/features/classDocuments/hooks/useClassDocuments.ts
import { useState, useEffect, useCallback, useRef } from "react";
import { classDocumentsAPI } from "../api";
import type { FolderItem, FileItem } from "../types";

export const useClassDocuments = () => {
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
    setIsLoading(true);
    try {
      const [foldersData, filesData] = await Promise.all([
        classDocumentsAPI.getFolders(),
        classDocumentsAPI.getRecentFiles(),
      ]);
      setFolders(foldersData);
      setRecentFiles(filesData);
      hasFetched.current = true;
    } catch (error) {
      console.error("Lỗi:", error);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    queueMicrotask(() => { fetchRootData(); });
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
    
    handleSearch,
  };
};