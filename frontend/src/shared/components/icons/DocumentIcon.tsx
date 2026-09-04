import React from "react";
import {
  FileText,
  Presentation,
  FileImage,
  FileVideo,
  File,
} from "lucide-react";

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
  // Trích xuất logic để trả về thẳng Component Icon thay vì thẻ JSX
  const getIconData = (ext: string) => {
    switch (ext.toLowerCase()) {
      case "pdf":
        return {
          IconComponent: FileText,
          colorClass: "text-red-500",
          bgClass: "bg-red-50",
        };
      case "docx":
      case "doc":
        return {
          IconComponent: FileText,
          colorClass: "text-blue-500",
          bgClass: "bg-blue-50",
        };
      case "pptx":
      case "ppt":
        return {
          IconComponent: Presentation,
          colorClass: "text-orange-500",
          bgClass: "bg-orange-50",
        };
      case "png":
      case "jpg":
      case "jpeg":
      case "webp":
        return {
          IconComponent: FileImage,
          colorClass: "text-green-500",
          bgClass: "bg-green-50",
        };
      case "mp4":
      case "mov":
      case "avi":
        return {
          IconComponent: FileVideo,
          colorClass: "text-purple-500",
          bgClass: "bg-purple-50",
        };
      default:
        return {
          IconComponent: File,
          colorClass: "text-[var(--ink-2)]",
          bgClass: "bg-[var(--bg-surface-3)]",
        };
    }
  };

  const { IconComponent, colorClass, bgClass } = getIconData(extension);

  return (
    // Render khung nền và render Icon với size được truyền vào
    <div
      className={`flex items-center justify-center p-2 rounded-[var(--r-sm)] shrink-0 ${bgClass} ${colorClass} ${className}`}
    >
      <IconComponent size={size} />
    </div>
  );
};
