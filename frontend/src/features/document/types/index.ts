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
  size: string; // VD: "71.4 MB" hoặc "--"
  uploader: string;
  uploaderAvatar?: string;
  fileExtension: string; // pdf, docx, pptx...
  directoryId?: string | number;
  uploadDate?: string;
  views?: number;
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
      classId?: string | number;
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

export interface CreateDocumentRequestDto {
  title?: string;
  description?: string;
}

export interface DeleteDocumentRequestDto {
  documentIds?: (number | string)[];
  [key: string]: any;
}

export interface DocumentResponseDto {
  id: number;
  title?: string;
  description?: string;
  file_name: string;
  view_count?: number;
  download_count?: number;
  bookmark_count?: number;
  is_bookmarked?: boolean;
  creator_user_id?: number;
  creator_display_name?: string;
  creator_avatar_url?: string;
  created_at?: string;
  status?: "UPLOADING" | "UPLOADED" | "ERROR" | string;
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
export interface RagAskRequestDto {
  question: string;
}

export interface AskContextualQuestionRequestDto {
  question: string;
  scope: "GLOBAL" | "CLASSROOM" | "DOCUMENT";
  documentId?: string | number;
  classId?: string | number;
}

export interface RagAnswerResponseDto {
  question?: string;
  answer: string;
  sources?: DocumentSourceDto[];
}

export interface ApiResponse<T> {
  code?: number;
  message: string;
  data: T;
}
