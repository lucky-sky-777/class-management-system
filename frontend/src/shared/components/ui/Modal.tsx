import React, { useEffect } from 'react';
import { createPortal } from 'react-dom';
import { X } from 'lucide-react';

export interface ModalProps {
    isOpen: boolean;
    onClose: () => void;
    title?: React.ReactNode;
    children: React.ReactNode;
    footer?: React.ReactNode;
}

export const Modal: React.FC<ModalProps> = ({ isOpen, onClose, title, children, footer }) => {
    // Prevent scrolling on body when modal is open
    useEffect(() => {
        if (isOpen) {
            document.body.style.overflow = 'hidden';
        } else {
            document.body.style.overflow = 'unset';
        }

        return () => {
            document.body.style.overflow = 'unset';
        };
    }, [isOpen]);

    if (!isOpen) return null;

    const modalContent = (
        <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 sm:p-6 animate-in fade-in duration-200">
            {/* Backdrop Overlay */}
            <div 
                className="absolute inset-0 bg-[var(--ink-1)]/40 transition-opacity"
                onClick={onClose}
                aria-hidden="true"
            />

            {/* Modal Container: Đã thêm viền (border-[var(--rule)]) để phân biệt với nền trắng */}
            <div 
                className="relative flex flex-col w-full max-w-lg max-h-[90vh] bg-[var(--bg-paper)] rounded-2xl border border-[var(--rule)] shadow-[var(--shadow-lg)] overflow-hidden transform transition-all"
                role="dialog"
                aria-modal="true"
            >
                {/* Fixed Header */}
                <div className="flex items-center justify-between p-4 sm:px-6 sm:py-4 border-b border-[var(--rule)] shrink-0 bg-[var(--bg-paper)]">
                    <div className="text-lg font-bold text-[var(--ink-1)] truncate pr-4">
                        {title}
                    </div>
                    <button 
                        onClick={onClose}
                        className="p-1.5 rounded-lg hover:bg-[var(--bg-surface-2)] text-[var(--ink-2)] hover:text-[var(--ink-1)] transition-colors focus:outline-none focus:ring-2 focus:ring-[var(--ink-blue-text)] shrink-0"
                        aria-label="Close"
                    >
                        <X className="w-5 h-5" />
                    </button>
                </div>

                {/* Scrollable Content Body */}
                <div className="overflow-y-auto p-4 sm:p-6 custom-scrollbar">
                    {children}
                </div>

                {/* Fixed Footer */}
                {footer && (
                    <div className="p-4 sm:px-6 sm:py-4 border-t border-[var(--rule)] shrink-0 bg-[var(--bg-surface-2)]">
                        {footer}
                    </div>
                )}
            </div>
        </div>
    );

    return createPortal(modalContent, document.body);
};