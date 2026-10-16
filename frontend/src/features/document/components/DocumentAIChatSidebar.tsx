import React from "react";
import { X, Send, Bot, User, Sparkles } from "lucide-react";
import { useDocumentAIChat } from "../hooks/useDocumentAIChat";

interface DocumentAIChatSidebarProps {
  isOpen: boolean;
  onClose: () => void;
  fileName: string;
}

export const DocumentAIChatSidebar: React.FC<DocumentAIChatSidebarProps> = ({
  isOpen,
  onClose,
  fileName,
}) => {
  const {
    input,
    setInput,
    isLoading,
    messages,
    messagesEndRef,
    handleSend,
    handleKeyDown,
  } = useDocumentAIChat(fileName);

  return (
    <>
      {/* Backdrop mờ khi mở trên mobile */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-[101] lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Sidebar UI */}
      <div
        className={`fixed top-[60px] right-0 bottom-0 w-full sm:w-[400px] bg-[#0A0A0A] border-l border-[#222] z-[102] flex flex-col transition-transform duration-300 ease-in-out ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {/* Header của Chat */}
        <div className="h-[60px] shrink-0 border-b border-[#222] flex items-center justify-between px-4 bg-[#111]">
          <div className="flex items-center gap-2 text-white font-semibold">
            <Sparkles size={20} className="text-purple-500" />
            <span>Trợ lý AI</span>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-white p-2 rounded-full hover:bg-white/10 transition-colors"
          >
            <X size={20} />
          </button>
        </div>

        {/* Khu vực hiển thị tin nhắn */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {messages.map((msg) => (
            <div
              key={msg.id}
              className={`flex gap-3 ${msg.sender === "user" ? "flex-row-reverse" : "flex-row"}`}
            >
              <div
                className={`w-8 h-8 rounded-full flex items-center justify-center shrink-0 ${
                  msg.sender === "user" ? "bg-blue-600" : "bg-purple-600"
                }`}
              >
                {msg.sender === "user" ? (
                  <User size={16} className="text-white" />
                ) : (
                  <Bot size={16} className="text-white" />
                )}
              </div>
              <div
                className={`max-w-[75%] rounded-2xl px-4 py-2 text-sm leading-relaxed ${
                  msg.sender === "user"
                    ? "bg-[#2D68FE] text-white rounded-tr-sm"
                    : "bg-[#222] text-gray-200 rounded-tl-sm"
                }`}
              >
                {msg.text}
              </div>
            </div>
          ))}

          {/* Hiệu ứng loading khi AI đang suy nghĩ */}
          {isLoading && (
            <div className="flex gap-3 flex-row">
              <div className="w-8 h-8 rounded-full bg-purple-600 flex items-center justify-center shrink-0">
                <Bot size={16} className="text-white" />
              </div>
              <div className="bg-[#222] rounded-2xl rounded-tl-sm px-4 py-3 flex items-center gap-1">
                <div className="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce [animation-delay:-0.3s]"></div>
                <div className="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce [animation-delay:-0.15s]"></div>
                <div className="w-1.5 h-1.5 bg-gray-400 rounded-full animate-bounce"></div>
              </div>
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Footer: Ô nhập chat */}
        <div className="p-4 border-t border-[#222] bg-[#111]">
          <div className="relative flex items-end bg-[#222] rounded-xl border border-[#333] focus-within:border-purple-500 overflow-hidden transition-colors">
            <textarea
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Hỏi AI về tài liệu này..."
              className="w-full bg-transparent text-white text-sm px-4 py-3 max-h-32 outline-none resize-none overflow-y-auto"
              rows={1}
            />
            <button
              onClick={handleSend}
              disabled={!input.trim() || isLoading}
              className="p-3 text-purple-500 hover:text-purple-400 disabled:text-gray-600 disabled:cursor-not-allowed transition-colors shrink-0"
            >
              <Send size={20} />
            </button>
          </div>
          <div className="text-center mt-2">
            <span className="text-[10px] text-gray-500">
              AI có thể mắc lỗi. Hãy kiểm tra lại các thông tin quan trọng.
            </span>
          </div>
        </div>
      </div>
    </>
  );
};