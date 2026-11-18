export interface SearchDocument {
  id: number | string;
  title: string;
  category: string;
  author: string;
  date: string;
  downloads: string | number;
  fileExtension: string; 
}

export interface DocumentResponseDto {
  id: number;
  title?: string;
  description?: string;
  file_name?: string;
  view_count?: number;
  download_count?: number;
  bookmark_count?: number;
  is_bookmarked?: boolean;
  creator_user_id?: number;
  creator_display_name?: string;
  creator_avatar_url?: string;
  created_at?: string;
  status?: string;
}