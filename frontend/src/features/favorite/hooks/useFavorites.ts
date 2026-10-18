import { useState, useEffect } from "react";
import { favoriteAPI } from "../api";
import type { FileItem } from "@/features/document/types";

export const useFavorites = () => {
  const [favorites, setFavorites] = useState<FileItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchFavorites = async () => {
      setIsLoading(true);
      try {
        const data = await favoriteAPI.getFavoriteDocuments();
        setFavorites(data);
      } catch (error) {
        console.error("Lỗi lấy danh sách yêu thích:", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchFavorites();
  }, []);

  return { favorites, isLoading };
};