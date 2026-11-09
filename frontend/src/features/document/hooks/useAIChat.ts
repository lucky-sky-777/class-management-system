// src/features/document/hooks/useAIChat.ts
import { useState, useRef, useEffect, useCallback, useMemo } from "react";
import type { Message, FileItem, ChatContext } from "@/features/document/types";
import { chatApi } from "@/features/document/api/chatApi";

const normalizeContext = (
  param: ChatContext | FileItem | null,
  classId?: string | number,
): ChatContext => {
  // Nếu đã truyền đúng chuẩn object ChatContext rồi thì giữ nguyên
  if (param && typeof param === "object" && "type" in param) {
    return param as ChatContext;
  }

  // Nếu truyền vào là một FileItem cụ thể -> Ngữ cảnh Document
  if (param && "name" in param) {
    return {
      type: "document",
      file: param as FileItem,
      classId,
    };
  }

  // 💡 NẾU KHÔNG CÓ FILE NHƯNG CÓ CLASSID -> TỰ ĐỘNG HIỂU LÀ CHAT LỚP HỌC (CLASSROOM)
  if (classId) {
    return {
      type: "classroom",
      classId,
      className: "Nhóm học",
    };
  }

  // Mặc định global nếu không có gì
  return {
    type: "global",
  };
};

const getInitialGreeting = (ctx: ChatContext): string => {
  switch (ctx.type) {
    case "document":
      return ctx.file
        ? `Chào bạn! Mình là trợ lý AI. Mình đã sẵn sàng hỗ trợ về tài liệu "${ctx.file.name}". Bạn muốn hỏi gì về tài liệu này?`
        : "Vui lòng chọn tài liệu để bắt đầu đặt câu hỏi.";
    case "classroom":
      return `Chào bạn! Mình là trợ lý AI hỗ trợ lớp học "${ctx.className}". Bạn cần giải đáp điều gì về môn học này?`;
    case "global":
    default:
      return "Chào bạn! Mình là trợ lý AI toàn hệ thống. Bạn có thể hỏi mình bất kỳ câu hỏi nào!";
  }
};

export const useAIChat = (
  contextOrFile: ChatContext | FileItem | null,
  legacyClassId?: string | number,
) => {
  const context = useMemo(
    () => normalizeContext(contextOrFile, legacyClassId),
    [contextOrFile, legacyClassId],
  );

  // Tạo định danh chuỗi nguyên thủy để so sánh:
  const contextKey = useMemo(() => {
    switch (context.type) {
      case "document":
        return `doc-${context.file?.id ?? "none"}-${context.classId ?? "0"}`;
      case "classroom":
        return `class-${context.classId}`;
      case "global":
      default:
        return "global";
    }
  }, [context]);

  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const [messages, setMessages] = useState<Message[]>(() => [
    {
      id: "welcome",
      sender: "ai",
      text: getInitialGreeting(context),
    },
  ]);

  // Chỉ reset tin nhắn khi contextKey THỰC SỰ thay đổi (đổi file khác / đổi lớp khác)
  useEffect(() => {
    setMessages([
      {
        id: "welcome",
        sender: "ai",
        text: getInitialGreeting(context),
      },
    ]);
  }, [contextKey]);

  // Cuộn thanh trượt xuống tin nhắn mới nhất
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages, isLoading]);

  // Xử lý gửi câu hỏi
  const handleSend = useCallback(async () => {
    const questionText = input.trim();
    if (!questionText || isLoading) return;

    // if (context.type === "document" && !context.file) {
    //   setMessages((prev) => [
    //     ...prev,
    //     {
    //       id: Date.now().toString(),
    //       sender: "ai",
    //       text: "Chưa có tài liệu nào được chọn. Vui lòng chọn tài liệu trước khi hỏi.",
    //     },
    //   ]);
    //   return;
    // }

    const userMessage: Message = {
      id: Date.now().toString(),
      sender: "user",
      text: questionText,
    };

    // Đẩy ngay tin nhắn người dùng lên giao diện
    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setIsLoading(true);

    try {
      const res = await chatApi.askByContext(context, questionText);

      // Bóc tách phòng thủ mọi trường hợp trả về của Axios Client
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const rawData: any = res;
      const answerContent =
        rawData?.answer ||
        rawData?.data?.answer ||
        rawData?.data?.data?.answer ||
        (typeof rawData === "string" ? rawData : null) ||
        "AI không thể tìm thấy câu trả lời phù hợp.";

      const aiMessage: Message = {
        id: (Date.now() + 1).toString(),
        sender: "ai",
        text: answerContent,
        sources: rawData?.sources || rawData?.data?.sources,
      };

      setMessages((prev) => [...prev, aiMessage]);
    } catch (error) {
      console.error("Lỗi khi kết nối với AI:", error);
      setMessages((prev) => [
        ...prev,
        {
          id: (Date.now() + 1).toString(),
          sender: "ai",
          text: "Đã có lỗi xảy ra khi xử lý yêu cầu. Vui lòng thử lại sau.",
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  }, [input, isLoading, context]);

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

export default useAIChat;
