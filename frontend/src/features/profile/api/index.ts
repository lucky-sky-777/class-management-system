import type { UserProfile, ProfileDocument } from "@/features/profile/types";

export const profileAPI = {
  getProfile: async (): Promise<UserProfile> => {
    await new Promise((resolve) => setTimeout(resolve, 500));
    return {
      id: "u1",
      name: "Phong Hào",
      title: "Kỹ thuật phần mềm - QNU",
      avatarUrl: "https://api.dicebear.com/9.x/avataaars/svg?seed=PhongHao", // Avatar giả lập
      stats: {
        documents: 15,
        downloads: 150,
        groups: 2,
      },
      personalInfo: {
        gender: "Nam",
        dob: "07/07/2005",
        phone: "098 937 4254",
      },
      profession: "Sinh viên",
      hobbies: ["Xem phim", "Code", "Thiết kế", "Bóng đá"],
      links: [{ label: "iclass.website", url: "https://iclass.website" }],
    };
  },

  getUserDocuments: async (): Promise<ProfileDocument[]> => {
    await new Promise((resolve) => setTimeout(resolve, 600));
    return [
      { id: "d1", name: "Tài liệu đặc tả.docx", size: "50.1 MB", extension: "docx", groupName: "KTPM46 - QNU", postedByMe: true },
      { id: "d2", name: "NCKHSV 26-27.pptx", size: "171.4 MB", extension: "pptx", groupName: "KTPM46 - QNU", postedByMe: true },
      { id: "d3", name: "NCKHSV 26-27.pptx", size: "171.4 MB", extension: "pptx", groupName: "KTPM46 - QNU", postedByMe: true },
    ];
  }
};