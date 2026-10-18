import { useState, useRef, useEffect } from "react";
import type { Message } from "@/features/document/types";
import { chatApi } from "@/features/document/api/chatApi";

export const useDocumentAIChat = (fileName: string) => {
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Khởi tạo tin nhắn chào mừng
  const [messages, setMessages] = useState<Message[]>([
    {
      id: "1",
      sender: "ai",
      text: `Chào bạn! Mình là trợ lý AI. Mình đã đọc xong tài liệu "${fileName}". Bạn muốn hỏi gì về tài liệu này?`,
    },
  ]);

  // Tự động cuộn xuống tin nhắn mới nhất
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  const handleSend = async () => {
    if (!input.trim() || isLoading) return;

    // 1. Thêm tin nhắn của user
    const userMessage: Message = {
      id: Date.now().toString(),
      sender: "user",
      text: input,
    };
    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      // 2. Gọi API
      const aiResponseText = await chatApi.sendMessage(userMessage.text, fileName);
      
      // 3. Thêm tin nhắn của AI
      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: aiResponseText,
      };
      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("Lỗi khi chat với AI:", error);
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