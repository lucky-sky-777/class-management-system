import { useState, useRef, useEffect } from "react";
import type { Message } from "@/features/document/types";
import { chatApi } from "@/features/document/api/chatApi";

// Nhận vào đối tượng File (hoặc File | null nếu chưa upload)
export const useDocumentAIChat = (file: File | null) => {
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Khởi tạo tin nhắn chào mừng theo tên file
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      sender: "ai",
      text: file
        ? `Chào bạn! Mình là trợ lý AI. Mình đã sẵn sàng hỗ trợ về tài liệu "${file.name}". Bạn muốn hỏi gì về tài liệu này?`
        : "Vui lòng chọn tài liệu để bắt đầu đặt câu hỏi.",
    },
  ]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Tự động cuộn xuống tin nhắn mới nhất
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = async () => {
    const questionText = input.trim();
    if (!questionText || isLoading) return;

    if (!file) {
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: "ai",
          text: "Chưa có tài liệu nào được chọn. Vui lòng tải lên tài liệu trước khi hỏi.",
        },
      ]);
      return;
    }

    // 1. Thêm tin nhắn của user
    const userMessage: Message = {
      id: Date.now().toString(),
      sender: "user",
      text: questionText,
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      // 2. Gọi API RAG Backend
      const res = await chatApi.askQuestion(questionText, file);

      // Trích xuất câu trả lời từ ResponseDTO (ưu tiên res.data.answer)
      const answerContent =
        res.data?.answer ||
        "AI không thể tìm thấy câu trả lời phù hợp trong tài liệu.";

      // 3. Thêm tin nhắn phản hồi của AI
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: answerContent,
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("Lỗi khi chat với AI:", error);

      // Thêm thông báo lỗi vào khung chat để giao diện người dùng không bị treo
      setMessages((prev) => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          sender: "ai",
          text: "Đã có lỗi xảy ra khi xử lý tài liệu. Vui lòng thử lại sau.",
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return {
    input,
    setInput,
    isLoading,
    messages,
    messagesEndRef,
    handleSend,
    handleKeyDown,
  };
};
