import { useState, useRef, useEffect } from "react";
import type { Message, FileItem } from "@/features/document/types";
import { chatApi } from "@/features/document/api/chatApi";

export const useDocumentAIChat = (file: FileItem | null) => {
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const [messages, setMessages] = useState<Message[]>(() => [
    {
      id: "welcome",
      sender: "ai",
      text: file
        ? `Chào bạn! Mình là trợ lý AI. Mình đã sẵn sàng hỗ trợ về tài liệu "${file.name}". Bạn muốn hỏi gì về tài liệu này?`
        : "Vui lòng chọn tài liệu để bắt đầu đặt câu hỏi.",
    },
  ]);

  // Cuộn xuống tin nhắn mới nhất
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isLoading]);

  const handleSend = async () => {
    const questionText = input.trim();
    if (!questionText || isLoading) return;

    if (!file) {
      setMessages((prev) => [
        ...prev,
        {
          id: Date.now().toString(),
          sender: "ai",
          text: "Chưa có tài liệu nào được chọn. Vui lòng chọn tài liệu trước khi hỏi.",
        },
      ]);
      return;
    }

    const userMessage: Message = {
      id: Date.now().toString(),
      sender: "user",
      text: questionText,
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      const res = await chatApi.askQuestion(questionText, file);

      const answerContent =
        (res as any)?.answer ||
        (res as any)?.data?.answer ||
        "AI không thể tìm thấy câu trả lời phù hợp trong tài liệu.";

      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: answerContent,
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("Lỗi khi chat với AI:", error);
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
