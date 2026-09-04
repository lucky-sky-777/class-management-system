import type {
  ClassItems,
  ClassResponse,
  ClassIdResponse,
  ClassMember,
  JoinClassResult,
  RecentGroupDocument,
} from "@features/home/types";
import { apiClient } from "@services/api-client";
import type { ResponseDTO } from "@shared/types";
import { ClassPrivacy } from "@shared/domain/enums";

export const MOCK_SUGGESTED_CLASSES: ClassItems[] = [
  {
    id: 991,
    name: "Cộng đồng lập trình",
    owner_user_id: 1,
    owner_avatar_url: "",
    description: "Cộng đồng lập trình Việt Nam",
    code: "CODE_PUBLIC",
    member_count: "1000",
    owner_display_name: "Cộng đồng",
    privacy: ClassPrivacy.PUBLIC,
    status: "" as any,
  },
  {
    id: 992,
    name: "Đồ án cuối kì",
    owner_user_id: 2,
    owner_avatar_url: "",
    description: "Đồ án 1 - QNU",
    code: "CODE_PRIVATE",
    member_count: "1000",
    owner_display_name: "Giảng viên",
    privacy: ClassPrivacy.PRIVATE,
    status: "" as any,
  },
];

// Dữ liệu Mock cho tài liệu mới
export const MOCK_RECENT_DOCUMENTS: RecentGroupDocument[] = [
  {
    id: 1,
    title: "Tài liệu NCKHSV 2026 - 2027.docx",
    authorName: "Đặng Phong Hào",
    createdAt: "2 giờ trước",
    groupName: "KTPM46",
    fileExtension: "docx",
  },
  {
    id: 2,
    title: "Slide_BaoCao_TienDo.pptx",
    authorName: "Nguyễn Văn A",
    createdAt: "5 giờ trước",
    groupName: "Đồ án chuyên ngành",
    fileExtension: "pptx",
  },
  {
    id: 3,
    title: "SoDo_KienTruc_HeThong.png",
    authorName: "Trần Thị B",
    createdAt: "1 ngày trước",
    groupName: "Cộng đồng lập trình",
    fileExtension: "png",
  },
  {
    id: 4,
    title: "Demo_ChucNang_App.mp4",
    authorName: "Lê Văn C",
    createdAt: "2 ngày trước",
    groupName: "Nhóm Kỹ Thuật",
    fileExtension: "mp4",
  }
];

export const homeAPI = {
  createClass: async (
    data: Omit<ClassResponse, "id">,
  ): Promise<ResponseDTO<ClassResponse>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;

    if (authStorage) {
      try {
        const parsed = JSON.parse(authStorage);
        token = parsed.state.user?.token || parsed.state.user?.access_token;
      } catch (e) {
        console.error("Lỗi parse JSON auth-storage", e);
      }
    }

    console.log("Token thực sự gửi đi:", token); // Nếu cái này hiện null là do bước Login chưa lưu token vào User

    return apiClient.post<ResponseDTO<ClassResponse>>("/classes", data, {
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });
  },

  getClassByCode: async (code: string): Promise<ResponseDTO<ClassItems>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    // Gọi API tìm thông tin lớp bằng mã code
    return apiClient.get<ResponseDTO<ClassItems>>(`/classes/code/${code}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  joinClass: async (code: string): Promise<ResponseDTO<JoinClassResult>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;

    if (authStorage) {
      try {
        const parsed = JSON.parse(authStorage);
        token = parsed.state.user?.token || parsed.state.user?.access_token;
      } catch (e) {
        console.error("Lỗi parse JSON auth-storage", e);
      }
    }

    return apiClient.post<ResponseDTO<JoinClassResult>>(
      `/classes/join`,
      { class_code: code },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      },
    );
  },

  getClasses: async (): Promise<ClassItems[]> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    // Gọi API thật tới Backend
    const response = await apiClient.get<ResponseDTO<ClassItems[]>>(
      "/classes",
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );

    return response.data;
  },

  // Rời khỏi lớp
  leaveClass: async (
    classId: number,
  ): Promise<ResponseDTO<ClassIdResponse>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    return apiClient.delete<ResponseDTO<ClassIdResponse>>(
      `/classes/${classId}/leave`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  },

  // Xóa lớp
  deleteClass: async (
    classId: number,
  ): Promise<ResponseDTO<ClassIdResponse>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    return apiClient.delete<ResponseDTO<ClassIdResponse>>(
      `/classes/${classId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  },

  // Cập nhật/Sửa lớp
  updateClass: async (
    classId: number,
    data: Partial<ClassResponse>,
  ): Promise<ResponseDTO<ClassResponse>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    return apiClient.patch<ResponseDTO<ClassResponse>>(
      `/classes/${classId}`,
      data,
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      },
    );
  },

  getClassMembers: async (
    classId: number,
  ): Promise<ResponseDTO<ClassMember[]>> => {
    const authStorage = localStorage.getItem("auth-storage");
    let token = null;
    if (authStorage) {
      const parsed = JSON.parse(authStorage);
      token = parsed.state.user?.token || parsed.state.user?.access_token;
    }

    return apiClient.get<ResponseDTO<ClassMember[]>>(
      `/classes/${classId}/members`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      },
    );
  },

  //gợi ý lớp
  getSuggestedClasses: async (): Promise<ResponseDTO<ClassItems[]>> => {
    // Giả lập delay mạng 800ms
    await new Promise((resolve) => setTimeout(resolve, 800));

    // Bổ sung thêm 'code' và 'time' cho đủ format của ResponseDTO
    return {
      success: true,
      message: "Lấy gợi ý thành công",
      data: MOCK_SUGGESTED_CLASSES,
      code: 200,
      time: new Date().toISOString(),
    };
  },
  
  //tài liệu mới
  getRecentDocuments: async (): Promise<ResponseDTO<RecentGroupDocument[]>> => {
    // Giả lập delay mạng 800ms
    await new Promise((resolve) => setTimeout(resolve, 800));

    return {
      success: true,
      message: "Lấy tài liệu mới thành công",
      data: MOCK_RECENT_DOCUMENTS,
      code: 200,
      time: new Date().toISOString(),
    };
  },
};


