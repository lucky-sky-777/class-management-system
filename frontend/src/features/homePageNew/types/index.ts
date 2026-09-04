export interface SearchDocument {
  id: number | string;
  title: string;
  category: string;
  author: string;
  date: string;
  downloads: string | number;
  fileExtension: string; 
}