export interface UserProfile {
  id: string;
  name: string;
  title: string;
  avatarUrl: string;
  coverUrl?: string; // Ảnh bìa
  stats: {
    documents: number;
    downloads: number;
    groups: number;
  };
  personalInfo: {
    gender: string;
    dob: string;
    phone: string;
  };
  profession: string;
  hobbies: string[];
  links: { label: string; url: string }[];
}

export interface ProfileDocument {
  id: string;
  name: string;
  size: string;
  extension: string;
  groupName: string;
  postedByMe: boolean;
}
