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
  uploadDate?: string;
  downloads?: number;
  likes?: number;
  description?: string;
  tags?: string[];
  isLiked?: boolean;
  fileUrl?: string;
  rawMetadata?: DocumentRawMetadata;
}

export interface CreateDirectoryRequestDto {
  name: string;
}

export interface Message {
  id: string;
  sender: "user" | "ai";
  text: string;
}

// DTO gửi lên cho Spring Boot khi upload tài liệu (@RequestPart("request"))
export interface CreateDocumentRequestDto {
  title?: string;
  description?: string;
}

// DTO nhận về sau khi upload tài liệu thành công
export interface DocumentResponseDto {
  id: number;
  title: string;
  fileName: string;
  fileExtension?: string;
  description?: string;
  key?: string;
  status?: "UPLOADING" | "UPLOADED" | "ERROR";
  createdAt?: string;
  fileUrl?: string;
  size?: number | string;
  [key: string]: unknown;
}

// DTO gửi lên API RAG khi hỏi đáp theo ID tài liệu
export interface RagAskRequestDto {
  documentId: number | string;
  question: string;
}

// DTO kết quả AI trả lời từ RAG Backend
export interface RagAnswerResponseDto {
  question?: string;
  answer: string;
  sources?: string[];
}

export interface ApiResponse<T> {
  code?: number;
  message: string;
  data: T;
}

export interface DocumentRawMetadata {
  file_name?: string;
  file_size?: number;
  mime_type?: string;
  [key: string]: unknown;
}

export interface DocumentMetadataResponseDto {
  document_id: number;
  metadata: DocumentRawMetadata;
}

export interface TagResponse {
  id: number;
  name: string;
  slug: string;
}
