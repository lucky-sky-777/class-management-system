export interface FolderItem {
  id: string | number;
  name: string;
  itemCount: number;
}

export interface FileItem {
  id: string | number;
  name: string;
  size: string; // VD: "71.4 MB"
  uploader: string; // VD: "Bạn đăng"
  fileExtension: string; // Dùng để render màu và icon (pdf, docx, pptx...)
}