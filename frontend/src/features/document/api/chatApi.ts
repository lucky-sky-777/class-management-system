export const chatApi = {
  /**
   * Gửi tin nhắn cho AI và nhận câu trả lời
   */
  sendMessage: async (text: string, fileName: string): Promise<string> => {
    // Giả lập độ trễ mạng 1.5 giây
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve(
          "Đây là phản hồi mẫu từ AI. Mình đang phân tích tài liệu để tìm câu trả lời tốt nhất cho bạn. Chức năng này sẽ được nối với Backend trong tương lai!",
        );
      }, 1500);
    });
  },
};
