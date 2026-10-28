// src/features/document/components/DocumentAIChatSidebar.tsx
import React from "react";
import { X, Send, Bot, User, Sparkles, Loader2 } from "lucide-react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { useDocumentAIChat } from "../hooks/useDocumentAIChat";
import type { FileItem } from "@/features/document/types";

interface DocumentAIChatSidebarProps {
  isOpen: boolean;
  onClose: () => void;
  file: FileItem | null;
}

// Helper tách 'node' khỏi props để không truyền vào thẻ DOM và triệt tiêu lỗi ESLint no-unused-vars
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const cleanProps = <T extends Record<string, any>>(props: T) => {
  const { node, ...rest } = props;
  void node;
  return rest;
};

// 1. Component con chứa toàn bộ logic chat và state của hook
const DocumentAIChatContent: React.FC<{
  file: FileItem | null;
  onClose: () => void;
}> = ({ file, onClose }) => {
  const {
    input,
    setInput,
    isLoading,
    messages,
    messagesEndRef,
    handleSend,
    handleKeyDown,
  } = useDocumentAIChat(file);

  return (
    <>
      {/* Header của Chat */}
      <div className="h-[60px] shrink-0 border-b border-[#222] flex items-center justify-between px-4 bg-[#111]">
        <div className="flex items-center gap-2 text-white font-semibold min-w-0">
          <Sparkles size={20} className="text-purple-500 shrink-0" />
          <div className="flex flex-col min-w-0">
            <span className="text-sm font-semibold text-white">Trợ lý AI</span>
            {file?.name && (
              <span className="text-[11px] text-gray-400 truncate max-w-[240px]">
                {file.name}
              </span>
            )}
          </div>
        </div>
        <button
          type="button"
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
            className={`flex gap-3 ${
              msg.sender === "user" ? "flex-row-reverse" : "flex-row"
            }`}
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

            {/* Bubble tin nhắn: Chữ Bot được set text-[#E4E4E7] nền #222 tương phản rõ nét */}
            <div
              className={`max-w-[85%] rounded-2xl px-4 py-2.5 text-sm leading-relaxed ${
                msg.sender === "user"
                  ? "bg-[#2D68FE] text-white rounded-tr-sm whitespace-pre-wrap"
                  : "bg-[#222] text-[#E4E4E7] rounded-tl-sm border border-[#333]/50 overflow-hidden"
              }`}
            >
              {msg.sender === "user" ? (
                msg.text
              ) : (
                <div className="markdown-content space-y-2 [&>*:first-child]:mt-0 [&>*:last-child]:mb-0 text-[#E4E4E7]">
                  <ReactMarkdown
                    remarkPlugins={[remarkGfm]}
                    components={{
                      // Headings
                      h1: (props) => (
                        <h1
                          className="text-base font-bold text-white mt-3 mb-1"
                          {...cleanProps(props)}
                        />
                      ),
                      h2: (props) => (
                        <h2
                          className="text-sm font-bold text-white mt-2.5 mb-1"
                          {...cleanProps(props)}
                        />
                      ),
                      h3: (props) => (
                        <h3
                          className="text-xs font-semibold text-purple-400 mt-2 mb-1 uppercase tracking-wider"
                          {...cleanProps(props)}
                        />
                      ),

                      // Paragraph & Lists: Màu sáng rõ rệt text-gray-100
                      p: (props) => (
                        <p
                          className="leading-relaxed mb-2 last:mb-0 text-gray-100"
                          {...cleanProps(props)}
                        />
                      ),
                      ul: (props) => (
                        <ul
                          className="list-disc pl-5 space-y-1 mb-2 text-gray-100"
                          {...cleanProps(props)}
                        />
                      ),
                      ol: (props) => (
                        <ol
                          className="list-decimal pl-5 space-y-1 mb-2 text-gray-100"
                          {...cleanProps(props)}
                        />
                      ),
                      li: (props) => (
                        <li
                          className="pl-0.5 leading-normal text-gray-100"
                          {...cleanProps(props)}
                        />
                      ),

                      // Emphasis
                      strong: (props) => (
                        <strong
                          className="font-semibold text-white"
                          {...cleanProps(props)}
                        />
                      ),
                      em: (props) => (
                        <em
                          className="text-gray-300 italic"
                          {...cleanProps(props)}
                        />
                      ),

                      // Code & Inline Code
                      code: (props) => {
                        const { className, children, ...restProps } =
                          cleanProps(props);
                        const isBlock = /language-/.test(className || "");
                        return isBlock ? (
                          <pre className="bg-[#161616] border border-[#333] rounded-lg p-3 my-2 overflow-x-auto text-xs text-gray-200 font-mono">
                            <code className={className} {...restProps}>
                              {children}
                            </code>
                          </pre>
                        ) : (
                          <code
                            className="bg-[#181818] text-purple-300 border border-[#333] rounded px-1.5 py-0.5 text-xs font-mono"
                            {...restProps}
                          >
                            {children}
                          </code>
                        );
                      },

                      // Table
                      table: (props) => (
                        <div className="overflow-x-auto my-2 rounded-lg border border-[#333]">
                          <table
                            className="w-full text-left text-xs border-collapse"
                            {...cleanProps(props)}
                          />
                        </div>
                      ),
                      thead: (props) => (
                        <thead
                          className="bg-[#181818] border-b border-[#333] text-gray-100 font-medium"
                          {...cleanProps(props)}
                        />
                      ),
                      th: (props) => (
                        <th
                          className="px-3 py-2 border-r border-[#333] last:border-none"
                          {...cleanProps(props)}
                        />
                      ),
                      td: (props) => (
                        <td
                          className="px-3 py-2 border-t border-r border-[#333] last:border-none text-gray-200"
                          {...cleanProps(props)}
                        />
                      ),
                      hr: (props) => (
                        <hr
                          className="border-[#333] my-3"
                          {...cleanProps(props)}
                        />
                      ),
                    }}
                  >
                    {msg.text}
                  </ReactMarkdown>
                </div>
              )}
            </div>
          </div>
        ))}

        {/* Hiệu ứng loading: "Đang suy nghĩ..." */}
        {isLoading && (
          <div className="flex gap-3 flex-row items-center">
            <div className="w-8 h-8 rounded-full bg-purple-600 flex items-center justify-center shrink-0">
              <Bot size={16} className="text-white" />
            </div>
            <div className="bg-[#222] border border-[#333]/50 rounded-2xl rounded-tl-sm px-4 py-2.5 flex items-center gap-2">
              <Loader2 size={15} className="text-purple-400 animate-spin" />
              <span className="text-xs text-gray-300 font-medium animate-pulse tracking-wide">
                Đang suy nghĩ...
              </span>
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
            className="w-full bg-transparent text-white text-sm px-4 py-3 max-h-32 outline-none resize-none overflow-y-auto placeholder:text-gray-500"
            rows={1}
          />
          <button
            type="button"
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
    </>
  );
};

// 2. Component cha điều khiển hiển thị Drawer & gán key reset
export const DocumentAIChatSidebar: React.FC<DocumentAIChatSidebarProps> = ({
  isOpen,
  onClose,
  file,
}) => {
  return (
    <>
      {/* Backdrop mờ trên mobile */}
      {isOpen && (
        <div
          className="fixed inset-0 bg-black/50 z-[101] lg:hidden"
          onClick={onClose}
        />
      )}

      {/* Container Drawer */}
      <div
        className={`fixed top-[60px] right-0 bottom-0 w-full sm:w-[420px] bg-[#0A0A0A] border-l border-[#222] z-[102] flex flex-col transition-transform duration-300 ease-in-out ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        <DocumentAIChatContent
          key={file?.id ?? "empty"}
          file={file}
          onClose={onClose}
        />
      </div>
    </>
  );
};
