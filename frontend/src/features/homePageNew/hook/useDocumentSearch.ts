import { useState, useEffect, useCallback } from "react";
import { searchDocumentAPI } from "@features/homePageNew/api";
import type { SearchDocument } from "@features/homePageNew/types";

export const useDocumentSearch = () => {
  // State dữ liệu khởi tạo
  const [recentSearches, setRecentSearches] = useState<string[]>([]);
  const [recommendedDocs, setRecommendedDocs] = useState<SearchDocument[]>([]);
  const [isInitialLoading, setIsInitialLoading] = useState(true);

  // State tìm kiếm
  const [isSearching, setIsSearching] = useState(false);
  const [isSearchLoading, setIsSearchLoading] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState<SearchDocument[]>([]);

  // Tải dữ liệu ban đầu (Gợi ý & Lịch sử)
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const [searches, docs] = await Promise.all([
          searchDocumentAPI.getRecentSearches(),
          searchDocumentAPI.getRecommendedDocs()
        ]);
        setRecentSearches(searches);
        setRecommendedDocs(docs);
      } catch (error) {
        console.error("Lỗi lấy dữ liệu ban đầu:", error);
      } finally {
        setIsInitialLoading(false);
      }
    };
    queueMicrotask(() => {
      fetchInitialData();
    });
  }, []);

  // Hàm xử lý tìm kiếm
  const handleSearch = useCallback(async (term: string) => {
    const trimmedTerm = term.trim();
    if (!trimmedTerm) {
      setIsSearching(false);
      setSearchQuery("");
      setSearchResults([]);
      return;
    }

    setSearchQuery(trimmedTerm);
    setIsSearching(true);
    setIsSearchLoading(true);

    try {
      const results = await searchDocumentAPI.searchDocuments(trimmedTerm);
      setSearchResults(results);
    } catch (error) {
      console.error("Lỗi tìm kiếm:", error);
    } finally {
      setIsSearchLoading(false);
    }
  }, []);

  return {
    recentSearches,
    recommendedDocs,
    isInitialLoading,
    isSearching,
    isSearchLoading,
    searchQuery,
    searchResults,
    handleSearch,
  };
};