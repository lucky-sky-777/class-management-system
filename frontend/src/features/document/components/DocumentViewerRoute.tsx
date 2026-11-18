// src/features/document/pages/DocumentViewerRoute.tsx
import { useParams, useNavigate } from "react-router-dom";
import { DocumentViewerModal } from "@/features/document/components/DocumentViewerModal";

export const DocumentViewerRoute = () => {
  const { documentId } = useParams();
  const navigate = useNavigate();

  const handleClose = () => {
    // Nếu tab được mở qua window.open từ AI Chat -> đóng luôn tab cho gọn
    if (window.opener) {
      window.close();
      return;
    }
    // Nếu người dùng tự gõ URL trực tiếp -> điều hướng về trang chủ
    navigate("/", { replace: true });
  };

  if (!documentId) return null;

  return (
    <DocumentViewerModal
      isOpen={true}
      onClose={handleClose}
      documentId={documentId}
    />
  );
};
