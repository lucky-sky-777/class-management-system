// ==========================================
// Entities UI
// ==========================================
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
  directoryId?: string | number;
  uploadDate?: string;
  downloads?: number;
  likes?: number;
  description?: string;
  tags?: string[];
  isLiked?: boolean;
  fileUrl?: string;
  rawMetadata?: DocumentRawMetadata;
}

// ==========================================
// Chat Context & UI Message
// ==========================================
export type ChatContext =
  | {
      type: "global";
      title?: string;
    }
  | {
      type: "classroom";
      classId: string | number;
      className: string;
    }
  | {
      type: "document";
      classId?: string | number | undefined;
      file: FileItem;
    };

export interface Message {
  id: string;
  sender: "user" | "ai";
  text: string;
  sources?: DocumentSourceDto[];
  isStreaming?: boolean;
}

// ==========================================
// Requests & Responses: Document Management
// ==========================================
export interface CreateDirectoryRequestDto {
  name: string;
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

export interface DocumentSourceDto {
  id: number;
  fileName: string;
}

export interface DocumentBookmarkResponseDto {
  documentId: number;
  isBookmarked: boolean;
  likesCount?: number;
}
// ==========================================
// Requests & Responses: RAG & Chat AI
// ==========================================
// DTO gửi lên API RAG khi hỏi đáp theo một tài liệu cụ thể
export interface RagAskRequestDto {
  question: string;
}

// DTO gửi lên khi hỏi đáp mở rộng theo ngữ cảnh (Global / Lớp / Tài liệu)
export interface AskContextualQuestionRequestDto {
  question: string;
  scope: "GLOBAL" | "CLASSROOM" | "DOCUMENT";
  documentId?: string | number;
  classId?: string | number;
}

// DTO kết quả AI trả lời từ RAG Backend
export interface RagAnswerResponseDto {
  question?: string;
  answer: string;
  sources?: DocumentSourceDto[];
}

// ==========================================
// Chuẩn API Response Chung
// ==========================================
export interface ApiResponse<T> {
  code?: number;
  message: string;
  data: T;
}
