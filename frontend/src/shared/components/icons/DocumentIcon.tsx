import React from "react";
import pdfIcon from "@/shared/components/icons/file_icon/pdf.png";
import wordIcon from "@/shared/components/icons/file_icon/docx.png";
import pptIcon from "@/shared/components/icons/file_icon/pptx.png";
import pngIcon from "@/shared/components/icons/file_icon/png.png";
import jpgIcon from "@/shared/components/icons/file_icon/jpg.png";
import videoIcon from "@/shared/components/icons/file_icon/mp4.png";
import defaultIcon from "@/shared/components/icons/file_icon/document.png";
import txtIcon from "@/shared/components/icons/file_icon/txt.png";

interface DocumentIconProps {
  extension: string;
  size?: number; // Tùy chỉnh kích thước icon
  className?: string; // Cho phép tùy chỉnh thêm CSS cho khung bọc bên ngoài
}

export const DocumentIcon: React.FC<DocumentIconProps> = ({
  extension,
  size = 16, // Kích thước mặc định nếu không truyền
  className = "",
}) => {
  const getIconData = (ext: string) => {
    switch (ext.toLowerCase()) {
      case "txt":
        return {
          iconSrc: txtIcon,
          bgClass: "bg-gray-50",
        };
      case "pdf":
        return {
          iconSrc: pdfIcon,
          bgClass: "bg-red-50",
        };
      case "docx":
      case "doc":
        return {
          iconSrc: wordIcon,
          bgClass: "bg-blue-50",
        };
      case "pptx":
      case "ppt":
        return {
          iconSrc: pptIcon,
          bgClass: "bg-orange-50",
        };
      case "jpg":
        return {
          iconSrc: jpgIcon,
          bgClass: "bg-green-50",
        };
      case "png":
      case "jpeg":
      case "webp":
        return {
          iconSrc: pngIcon,
          bgClass: "bg-gray-50",
        };
      case "mp4":
      case "mov":
      case "avi":
        return {
          iconSrc: videoIcon,
          bgClass: "bg-purple-50",
        };
      default:
        return {
          iconSrc: defaultIcon,
          bgClass: "bg-[var(--bg-surface-3)]",
        };
    }
  };

  const { iconSrc, bgClass } = getIconData(extension);

  return (
    <div
      className={`flex items-center justify-center p-2 rounded-[var(--r-sm)] shrink-0 ${bgClass} ${className}`}
    >
      <img
        src={iconSrc}
        alt={`${extension} icon`}
        style={{ width: size, height: size }}
        className="object-contain" // Đảm bảo ảnh không bị méo
      />
    </div>
  );
};
