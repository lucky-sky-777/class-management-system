import type { CompetitionHistory } from "@features/emulation/types";

interface EmulationHistoryProps {
  selectedTeam: number;
  history: CompetitionHistory[];
  canEdit: boolean;
  onOpenPointModal: () => void;
}

export const HistoryTable = ({
  selectedTeam,
  history,
  canEdit,
  onOpenPointModal,
}: EmulationHistoryProps) => {
  const filteredHistory = history.filter((h) => h.teamId === selectedTeam);

  return (
    <div className="bg-[var(--bg-surface)] rounded-[var(--r-xl)] border border-[var(--rule-md)] shadow-[var(--shadow-sm)] overflow-hidden flex flex-col h-[450px] transition-all">
      {/* HEADER: Dùng font serif Lora cho tiêu đề cực sang */}
      <div className="p-3.5 border-b border-[var(--rule)] bg-[var(--bg-paper)] flex justify-between items-center sticky top-0 z-10">
        <div>
          <h3 className="text-base font-semibold italic text-[var(--ink-1)] leading-none mb-1">
            Lịch sử thi đua tổ {selectedTeam}
          </h3>
        </div>

        {canEdit && (
          <button
            onClick={onOpenPointModal}
            className="flex items-center gap-1.5 bg-[var(--warm-400)] hover:bg-[var(--warm-600)] text-white px-3.5 py-1.5 rounded-[var(--r-md)] font-black text-[10px] uppercase tracking-tight shadow-[var(--shadow-sm)] transition-all active:scale-95"
          >
            <span>+ Ghi điểm</span>
          </button>
        )}
      </div>

      {/* TABLE BODY: Tối ưu thanh cuộn và layout */}
      <div className="flex-1 overflow-y-auto no-scrollbar md:block bg-[var(--bg-surface)]">
        <table className="w-full text-left border-collapse">
          <thead className="hidden md:table-header-group sticky top-0 bg-[var(--bg-surface)] text-[var(--ink-3)] font-black uppercase text-[9px] tracking-widest border-b border-[var(--rule-md)] z-20">
            <tr>
              <th className="px-6 py-4 w-44">Thời gian</th>
              <th className="px-6 py-4">Nội dung thay đổi</th>
              <th className="px-6 py-4 text-center w-28">Biến động</th>
              <th className="px-6 py-4 text-center w-40">Người phê duyệt</th>
            </tr>
          </thead>

          <tbody className="divide-y divide-[var(--rule)]">
            {filteredHistory.length > 0 ? (
              [...filteredHistory].map((log) => (
                <tr
                  key={log.id}
                  className="hover:bg-[var(--bg-surface-2)] transition-colors group flex flex-col md:table-row p-3 md:p-0"
                >
                  {/* 1. Ngày tháng - Giảm padding py-3, text nhỏ xinh */}
                  <td className="md:table-cell md:px-6 md:py-3.5">
                    <span className="font-mono text-[10px] text-[var(--ink-2)] bg-[var(--bg-surface-3)] px-2 py-0.5 rounded-[var(--r-xs)]">
                      {log.date}
                    </span>
                  </td>

                  {/* 2. Nội dung - Giảm py-3, line-height gọn lại */}
                  <td className="md:table-cell md:px-6 md:py-3.5">
                    <p className="text-[13px] text-[var(--ink-1)] font-medium leading-snug max-w-md">
                      {log.content}
                    </p>
                  </td>

                  {/* 3. Điểm số - Đưa về lề trái cho đồng bộ tiêu đề mới */}
                  <td className="md:table-cell md:px-6 md:py-3.5 text-left">
                    <span
                      className={`text-[13px] font-black tracking-tight ${
                        log.points >= 0
                          ? "text-[var(--green-text)]"
                          : "text-[var(--red-text)]"
                      }`}
                    >
                      {log.points >= 0 ? `+${log.points}` : log.points}
                    </span>
                  </td>

                  {/* 4. Người phê duyệt - Avatar nhỏ lại một chút (w-7 h-7) */}
                  <td className="md:table-cell md:px-6 md:py-3.5">
                    <div className="flex items-center gap-2.5 justify-start">
                      <div className="w-7 h-7 rounded-full bg-[var(--blue-fill)] border border-[var(--blue-border)] flex-shrink-0 flex items-center justify-center text-[10px] font-black text-[var(--blue-text)] shadow-sm">
                        {log.actor.charAt(0).toUpperCase()}
                      </div>
                      <span className="text-[12px] font-bold text-[var(--ink-2)] whitespace-nowrap">
                        {log.actor}
                      </span>
                    </div>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={4} className="py-20 text-center">
                  <div className="inline-flex flex-col items-center opacity-30">
                    <div className="w-6 border-t border-[var(--ink-3)] mb-3" />
                    <span className="text-[var(--ink-3)] text-[9px] font-black uppercase tracking-widest">
                      Bản ghi trống
                    </span>
                  </div>
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};
