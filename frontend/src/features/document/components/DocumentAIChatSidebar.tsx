// src/features/document/components/DocumentAIChatSidebar.tsx
import React from "react";
import { X, Send, User, Sparkles, ExternalLink } from "lucide-react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import { useNavigate } from "react-router-dom";
import { useAIChat } from "@/features/document/hooks/useAIChat";
import type { FileItem, DocumentSourceDto } from "@/features/document/types";

interface DocumentAIChatSidebarProps {
  isOpen: boolean;
  onClose: () => void;
  file: FileItem | null;
  classId?: string | number;
  onSelectDocument?: (documentId: number) => void;
}

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const cleanProps = <T extends Record<string, any>>(props: T) => {
  const { node, ...rest } = props;
  void node;
  return rest;
};

// Chuyển đổi chuỗi [Nguồn: fileName] thành Markdown link mang protocol "doc://{id}"
const injectSourceLinks = (text: string, sources?: DocumentSourceDto[]) => {
  if (!text) return "";
  if (!sources || sources.length === 0) {
    return text.replace(/(\[(?:Nguồn|nguồn):\s*[^\]]+\])/gi, " *$1* ");
  }

  let formattedText = text;
  sources.forEach((src) => {
    const escapedFileName = src.fileName.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
    const regex = new RegExp(
      `\\[(?:Nguồn|nguồn):\\s*${escapedFileName}\\]`,
      "gi",
    );
    formattedText = formattedText.replace(
      regex,
      `[[Nguồn: ${src.fileName}]](doc://${src.id})`,
    );
  });

  return formattedText;
};

const DocumentAIChatContent: React.FC<{
  file: FileItem | null;
  classId?: string | number;
  onSelectDocument?: (documentId: number) => void;
  onClose: () => void;
}> = ({ file, classId, onSelectDocument, onClose }) => {
  const navigate = useNavigate();

  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const chatContext: any = file
    ? { type: "document", file, classId }
    : classId
      ? { type: "classroom", classId, className: "Lớp học" }
      : { type: "global" };

  const {
    input,
    setInput,
    isLoading,
    messages,
    messagesEndRef,
    handleSend,
    handleKeyDown,
  } = useAIChat(chatContext);

  const handleOpenSource = (docId: number) => {
    if (onSelectDocument) {
      onSelectDocument(docId);
      return;
    }
    if (classId) {
      navigate(`/class/${classId}/documents?fileId=${docId}`);
    } else {
      window.open(`/documents/${docId}`, "_blank");
    }
  };

  return (
    <>
      {/* Header */}
      <div className="h-[64px] shrink-0 flex items-center justify-between px-6 bg-[var(--bg-surface)] border-b border-[var(--rule)]">
        <div className="flex items-center gap-3 min-w-0">
          <div className="w-8 h-8 rounded-full bg-[var(--primary-fill)] flex items-center justify-center shrink-0">
            <Sparkles size={18} className="text-[var(--primary)]" />
          </div>
          <div className="flex flex-col min-w-0">
            <span className="text-[15px] font-medium text-[var(--ink-1)] tracking-tight">
              Trợ lý AI
            </span>
            <span className="text-[12px] text-[var(--ink-2)] truncate max-w-[250px]">
              {file?.name ? file.name : "Hỗ trợ nhóm học"}
            </span>
          </div>
        </div>
        <button
          type="button"
          onClick={onClose}
          className="text-[var(--ink-3)] hover:text-[var(--ink-1)] p-2 rounded-full hover:bg-[var(--bg-surface-2)] transition-colors"
        >
          <X size={20} />
        </button>
      </div>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 sm:px-6 py-6 space-y-8 bg-[var(--bg-surface)] no-scrollbar">
        {messages.map((msg) => (
          <div
            key={msg.id}
            className={`flex gap-4 ${msg.sender === "user" ? "flex-row-reverse" : "flex-row"}`}
          >
            {/* Avatar */}
            <div
              className={`w-9 h-9 rounded-full flex items-center justify-center shrink-0 shadow-[var(--shadow-sm)] ${
                msg.sender === "user"
                  ? "bg-[var(--bg-surface-2)]"
                  : "bg-gradient-to-tr from-[var(--primary)] to-[var(--violet)]"
              }`}
            >
              {msg.sender === "user" ? (
                <User size={18} className="text-[var(--ink-2)]" />
              ) : (
                <Sparkles size={18} className="text-white" />
              )}
            </div>

            {/* Khối tin nhắn */}
            <div
              className={`max-w-[90%] px-1 py-1.5 text-[15px] leading-relaxed ${
                msg.sender === "user"
                  ? "bg-[var(--bg-surface-2)] text-[var(--ink-1)] rounded-[24px] rounded-tr-[4px] px-5 py-3.5 whitespace-pre-wrap shadow-[var(--shadow-xs)]"
                  : "bg-transparent text-[var(--ink-1)]"
              }`}
            >
              {msg.sender === "user" ? (
                msg.text
              ) : (
                <div className="markdown-content space-y-3 [&>*:first-child]:mt-0 [&>*:last-child]:mb-0 w-full overflow-hidden">
                  <ReactMarkdown
                    remarkPlugins={[remarkGfm]}
                    components={{
                      a: (props) => {
                        const href = props.href || "";
                        if (href.startsWith("doc://")) {
                          const docId = Number(href.replace("doc://", ""));
                          return (
                            <button
                              type="button"
                              onClick={(e) => {
                                e.preventDefault();
                                handleOpenSource(docId);
                              }}
                              title="Nhấn để mở tài liệu này"
                              className="inline-flex items-center gap-1.5 text-[12px] italic font-medium text-[var(--primary)] hover:text-[var(--primary-hover)] underline underline-offset-4 hover:bg-[var(--primary-fill)] px-1.5 py-0.5 rounded-[var(--r-xs)] transition-colors cursor-pointer select-none mx-1 my-1"
                            >
                              <span>{props.children}</span>
                              <ExternalLink
                                size={12}
                                className="inline opacity-80"
                              />
                            </button>
                          );
                        }
                        return (
                          <a
                            href={href}
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-[var(--primary)] underline underline-offset-2 hover:text-[var(--primary-hover)] break-words transition-colors"
                            {...cleanProps(props)}
                          >
                            {props.children}
                          </a>
                        );
                      },
                      em: (props) => {
                        const content = String(props.children ?? "");
                        const isSource =
                          content.includes("Nguồn:") ||
                          content.includes("nguồn:");
                        if (isSource) {
                          return (
                            <span
                              className="text-[12px] italic font-medium text-[var(--ink-3)] inline-block px-1 opacity-80 select-none mx-1"
                              {...cleanProps(props)}
                            >
                              {props.children}
                            </span>
                          );
                        }
                        return (
                          <em
                            className="text-[var(--ink-2)] italic"
                            {...cleanProps(props)}
                          >
                            {props.children}
                          </em>
                        );
                      },
                      h1: (props) => (
                        <h1
                          className="text-lg font-bold text-[var(--ink-1)] mt-4 mb-2"
                          {...cleanProps(props)}
                        />
                      ),
                      h2: (props) => (
                        <h2
                          className="text-base font-bold text-[var(--ink-1)] mt-4 mb-2"
                          {...cleanProps(props)}
                        />
                      ),
                      h3: (props) => (
                        <h3
                          className="text-[15px] font-semibold text-[var(--primary)] mt-3 mb-2"
                          {...cleanProps(props)}
                        />
                      ),
                      p: (props) => (
                        <p
                          className="leading-7 mb-3 last:mb-0 text-[var(--ink-2)] break-words"
                          {...cleanProps(props)}
                        />
                      ),
                      ul: (props) => (
                        <ul
                          className="list-disc pl-6 space-y-1.5 mb-3 text-[var(--ink-2)] marker:text-[var(--ink-3)]"
                          {...cleanProps(props)}
                        />
                      ),
                      ol: (props) => (
                        <ol
                          className="list-decimal pl-6 space-y-1.5 mb-3 text-[var(--ink-2)] marker:text-[var(--ink-3)]"
                          {...cleanProps(props)}
                        />
                      ),
                      li: (props) => (
                        <li
                          className="pl-1 leading-relaxed break-words"
                          {...cleanProps(props)}
                        />
                      ),
                      strong: (props) => (
                        <strong
                          className="font-semibold text-[var(--ink-1)]"
                          {...cleanProps(props)}
                        />
                      ),
                      code: (props) => {
                        const { className, children, ...restProps } =
                          cleanProps(props);
                        const isBlock = /language-/.test(className || "");
                        return isBlock ? (
                          <pre className="bg-[var(--bg-surface-2)] border border-[var(--rule)] rounded-[var(--r-md)] p-4 my-3 overflow-x-auto text-[13px] text-[var(--ink-1)] font-mono shadow-[var(--shadow-xs)] pb-5 w-full">
                            <code className={className} {...restProps}>
                              {children}
                            </code>
                          </pre>
                        ) : (
                          <code
                            className="bg-[var(--bg-surface-2)] text-[var(--primary)] border border-[var(--rule)] rounded-[var(--r-xs)] px-1.5 py-0.5 text-[13px] font-mono break-all"
                            {...restProps}
                          >
                            {children}
                          </code>
                        );
                      },
                      table: (props) => (
                        <div className="overflow-x-auto my-4 rounded-[var(--r-md)] border border-[var(--rule-md)] pb-3 w-full">
                          <table
                            className="w-full text-left text-sm border-collapse bg-[var(--bg-surface)] min-w-[300px]"
                            {...cleanProps(props)}
                          />
                        </div>
                      ),
                      thead: (props) => (
                        <thead
                          className="bg-[var(--bg-surface-2)] border-b border-[var(--rule-md)] text-[var(--ink-1)] font-medium"
                          {...cleanProps(props)}
                        />
                      ),
                      th: (props) => (
                        <th
                          className="px-4 py-3 border-r border-[var(--rule)] last:border-none"
                          {...cleanProps(props)}
                        />
                      ),
                      td: (props) => (
                        <td
                          className="px-4 py-3 border-t border-r border-[var(--rule)] last:border-none text-[var(--ink-2)]"
                          {...cleanProps(props)}
                        />
                      ),
                      hr: (props) => (
                        <hr
                          className="border-[var(--rule-md)] my-6"
                          {...cleanProps(props)}
                        />
                      ),
                    }}
                  >
                    {injectSourceLinks(msg.text, msg.sources)}
                  </ReactMarkdown>
                </div>
              )}
            </div>
          </div>
        ))}

        {isLoading && (
          <div className="flex gap-4 items-center animate-in fade-in duration-300">
            <div className="w-9 h-9 rounded-full bg-gradient-to-tr from-[var(--primary)] to-[var(--violet)] flex items-center justify-center shrink-0 shadow-[var(--shadow-sm)]">
              <Sparkles size={18} className="text-white animate-pulse" />
            </div>
            <div className="flex items-center gap-3 bg-transparent text-[var(--ink-2)] px-1 py-2">
              <span className="text-[14px] italic tracking-wide animate-pulse">
                Đang suy nghĩ
              </span>
              <span className="flex space-x-1 items-center h-4 translate-y-[2px]">
                <span className="h-1.5 w-1.5 bg-[var(--primary)] rounded-full animate-bounce [animation-delay:-0.3s]"></span>
                <span className="h-1.5 w-1.5 bg-[var(--violet)] rounded-full animate-bounce [animation-delay:-0.15s]"></span>
                <span className="h-1.5 w-1.5 bg-[var(--primary)] rounded-full animate-bounce"></span>
              </span>
            </div>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div className="p-4 sm:p-5 bg-[var(--bg-surface)] border-t border-[var(--rule)]">
        <div className="relative flex flex-col bg-[var(--bg-surface-2)] rounded-[var(--r-xl)] border border-[var(--rule-md)] focus-within:border-[var(--primary)] focus-within:bg-[var(--bg-surface)] focus-within:shadow-[var(--shadow-sm)] overflow-hidden transition-all">
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyDown}
            placeholder={
              file?.name
                ? "Hỏi điều gì đó về tài liệu này..."
                : "Đặt câu hỏi cho AI..."
            }
            className="w-full bg-transparent text-[var(--ink-1)] text-[15px] px-5 py-4 pb-14 max-h-40 outline-none resize-none overflow-y-auto no-scrollbar placeholder:text-[var(--ink-3)] leading-relaxed"
            rows={1}
          />
          <div className="absolute bottom-2.5 right-3 flex items-center gap-2">
            <button
              type="button"
              onClick={handleSend}
              disabled={!input.trim() || isLoading}
              className="p-2.5 rounded-full bg-[var(--primary)] text-white hover:bg-[var(--primary-hover)] disabled:bg-[var(--bg-surface-3)] disabled:text-[var(--ink-4)] disabled:cursor-not-allowed transition-colors shadow-sm"
            >
              <Send size={18} className="translate-x-[1px] translate-y-[1px]" />
            </button>
          </div>
        </div>
        <div className="text-center mt-3 mb-1">
          <span className="text-[11px] text-[var(--ink-3)] font-medium">
            Trợ lý AI có thể cung cấp thông tin không chính xác. Hãy kiểm chứng
            lại.
          </span>
        </div>
      </div>
    </>
  );
};

export const DocumentAIChatSidebar: React.FC<DocumentAIChatSidebarProps> = ({
  isOpen,
  onClose,
  file,
  classId,
  onSelectDocument,
}) => {
  return (
    <>
      {isOpen && (
        <div
          className="fixed inset-0 bg-[var(--bg-overlay)] backdrop-blur-sm z-[101] lg:hidden transition-opacity"
          onClick={onClose}
        />
      )}

      <div
        className={`fixed top-[60px] right-0 bottom-0 w-[100%] lg:w-[480px] bg-[var(--bg-surface)] border-l border-[var(--rule)] z-[102] flex flex-col transition-transform duration-300 ease-[cubic-bezier(0.2,0.8,0.2,1)] shadow-[var(--shadow-lg)] ${
          isOpen ? "translate-x-0" : "translate-x-full"
        }`}
      >
        {isOpen && (
          <DocumentAIChatContent
            key={file?.id ?? "empty"}
            file={file}
            classId={classId}
            onSelectDocument={onSelectDocument}
            onClose={onClose}
          />
        )}
      </div>
    </>
  );
};
