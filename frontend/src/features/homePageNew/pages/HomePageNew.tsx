// src/features/homePageNew/pages/HomePageNew.tsx
import React, { useState } from "react";
import { Loader2, Sparkles } from "lucide-react";
import { useDocumentSearch } from "@features/homePageNew/hook/useDocumentSearch";
import { HeroSearch } from "@features/homePageNew/components/HeroSearch";
import { RecentSearches } from "@features/homePageNew/components/RecentSearches";
import { DocumentCard } from "@features/homePageNew/components/DocumentCard";
import { DocumentAIChatSidebar } from "@/features/document/components/DocumentAIChatSidebar"; // Nhớ import Sidebar vào

export const HomePageNew = () => {
  const {
    recentSearches,
    recommendedDocs,
    isInitialLoading,
    isSearching,
    isSearchLoading,
    searchQuery,
    searchResults,
    handleSearch,
  } = useDocumentSearch();

  // State quản lý việc mở khung chat AI toàn hệ thống
  const [isGlobalAIOpen, setIsGlobalAIOpen] = useState(false);

  const handleViewDocument = (id: string | number) => {
    console.log("Chuyển đến trang chi tiết tài liệu ID:", id);
    // Tích hợp logic navigate của bạn ở đây
  };

  if (isInitialLoading) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
        <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
          <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
          <p className="text-ink-2 text-sm font-medium">Đang tải ...</p>
        </div>
      </div>
    );
  }

  return (
    // Wrapper ngoài cùng giữ nền, Wrapper bên trong chịu trách nhiệm đẩy content sang trái 480px
    <div className="min-h-screen bg-[var(--bg-surface-2)] overflow-x-hidden font-sans">
      <div
        className={`transition-all duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] py-16 px-4 sm:px-6 ${
          isGlobalAIOpen ? "lg:mr-[480px]" : ""
        }`}
      >
        <div className="max-w-5xl mx-auto space-y-14">
          {/* Component Nhập liệu */}
          <HeroSearch onSearch={handleSearch} currentQuery={searchQuery} />

          {/* Component Lịch sử */}
          {!isSearching && (
            <RecentSearches
              searches={recentSearches}
              onTagClick={handleSearch}
            />
          )}

          {/* Khối Hiển thị Dữ liệu */}
          <div className="max-w-[900px] mx-auto">
            {isSearching ? (
              // TRẠNG THÁI TÌM KIẾM
              <>
                <div className="mb-4">
                  <h3 className="text-sm font-semibold text-[var(--ink-2)]">
                    Kết quả tìm kiếm cho:{" "}
                    <span className="text-[var(--primary)]">
                      "{searchQuery}"
                    </span>
                  </h3>
                </div>

                {isSearchLoading ? (
                  <div className="flex justify-center py-10">
                    <Loader2
                      className="animate-spin text-[var(--ink-3)]"
                      size={24}
                    />
                  </div>
                ) : searchResults.length === 0 ? (
                  <div className="text-center py-10 text-[var(--ink-3)] text-sm">
                    Không tìm thấy kết quả nào phù hợp.
                  </div>
                ) : (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {searchResults.map((doc) => (
                      <DocumentCard
                        key={doc.id}
                        document={doc}
                        onClick={handleViewDocument}
                      />
                    ))}
                  </div>
                )}
              </>
            ) : (
              // TRẠNG THÁI GỢI Ý MẶC ĐỊNH
              <>
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-[11px] font-semibold text-[var(--ink-3)] uppercase tracking-wider">
                    Gợi ý phù hợp dành cho bạn
                  </h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                  {recommendedDocs.map((doc) => (
                    <DocumentCard
                      key={doc.id}
                      document={doc}
                      onClick={handleViewDocument}
                    />
                  ))}
                </div>
              </>
            )}
          </div>
        </div>
      </div>

      {/* Nút bấm AI Floating Style Colab (Icon tròn đơn giản nằm giữa phía dưới) */}
      {!isGlobalAIOpen && (
        <div className="fixed bottom-2 left-1/2 -translate-x-1/2 z-50 animate-in fade-in slide-in-from-bottom-2 duration-300">
          <button
            onClick={() => setIsGlobalAIOpen(true)}
            title="Trợ lý AI"
            className="flex items-center justify-center w-12 h-12 rounded-full bg-blue-500 text-white shadow-lg hover:bg-blue-600 hover:scale-105 active:scale-95 transition-all"
          >
            <Sparkles size={24} />
          </button>
        </div>
      )}

      {/* Sidebar Chat AI (Global Mode) */}
      <DocumentAIChatSidebar
        isOpen={isGlobalAIOpen}
        onClose={() => setIsGlobalAIOpen(false)}
        file={null} // Không truyền file
        // Không truyền classId -> Tự động kích hoạt type "global"
        onSelectDocument={(docId) => {
          setIsGlobalAIOpen(false);
          handleViewDocument(docId); // Dẫn người dùng đến trang xem tài liệu nếu click nguồn
        }}
      />
    </div>
  );
};
