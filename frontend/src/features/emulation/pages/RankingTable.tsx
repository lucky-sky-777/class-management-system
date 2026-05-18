import type { TeamRanking } from "@features/emulation/types";
import Rank1 from "@assets/rank1.png";
import Rank2 from "@assets/rank2.png";
import Rank3 from "@assets/rank3.png";

interface RankingTableProps {
  title: string;
  rows: TeamRanking[];
}

export const RankingTable = ({ title, rows }: RankingTableProps) => {
  // Sắp xếp thứ tự hiển thị bục từ trái qua phải chuẩn quốc tế: Hạng 2 - Hạng 1 - Hạng 3
  const podiumOrder = [
    rows.find((r) => r.rank === 2),
    rows.find((r) => r.rank === 1),
    rows.find((r) => r.rank === 3),
  ].filter(Boolean);

  const others = rows.filter((r) => r.rank > 3);

  const getMedal = (rank: number) => {
    if (rank === 1) return Rank1;
    if (rank === 2) return Rank2;
    if (rank === 3) return Rank3;
    return null;
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* TIÊU ĐỀ BIỂU ĐỒ */}
      <div className="flex items-center gap-2 px-1">
        <div className="w-1 h-4 bg-[var(--warm-400)] rounded-full" />
        <h3 className="text-xs md:text-sm font-black uppercase tracking-widest text-[var(--ink-1)]">
          {title}
        </h3>
      </div>

      {/* 1. KHU VỰC BỤC VINH QUANG (PODIUM BOX) */}
      <div className="flex items-end justify-center gap-3 md:gap-6 pt-16 pb-12 px-4 bg-surface rounded-2xl border border-[var(--rule)] shadow-xs relative overflow-hidden">
        {/* Đường line viền mờ trang trí phía trên bục */}
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-[var(--warm-400)] to-transparent opacity-20" />

        {podiumOrder.map((team) => {
          if (!team) return null;
          const isTop1 = team.rank === 1;

          // Chiều cao bục giật cấp nhịp nhàng
          const podiumHeight = isTop1
            ? "h-36 md:h-44"
            : team.rank === 2
              ? "h-26 md:h-32"
              : "h-20 md:h-24";

          return (
            <div
              key={team.teamId}
              className="flex flex-col items-center flex-1 max-w-[140px] group"
            >
              {/* Huy hiệu/Huy chương nổi bật, hover vào sẽ phóng to nhẹ */}
              <div className="relative mb-3 transition-transform group-hover:scale-110 duration-300">
                <img
                  src={getMedal(team.rank)!}
                  alt={`Hạng ${team.rank}`}
                  className={`${isTop1 ? "w-16 h-16 md:w-20 md:h-20" : "w-12 h-12 md:w-14 md:h-14"} object-contain drop-shadow-lg`}
                />
              </div>

              {/* Thân Cột bục: Hạng 1 phủ màu Xanh Thương hiệu nổi bật, Hạng 2 & 3 phủ màu xám nền nhạt */}
              <div
                className={`w-full rounded-t-2xl flex flex-col items-center justify-end pb-4 transition-all duration-500 shadow-xs ${podiumHeight} ${
                  isTop1
                    ? "bg-gradient-to-b from-[var(--warm-400)] to-[var(--warm-600)]"
                    : "bg-[var(--bg-surface-2)] dark:bg-[var(--bg-surface-3)] border-t border-x border-[var(--rule)]"
                }`}
              >
                <span
                  className={`text-[9px] font-bold uppercase tracking-wider mb-0.5 ${
                    isTop1
                      ? "text-[var(--warm-fill)] opacity-80"
                      : "text-[var(--ink-3)]"
                  }`}
                >
                  Pts
                </span>
                <span
                  className={`text-xl md:text-2xl font-mono font-black [font-variant-numeric:lining-nums] ${
                    isTop1 ? "text-white" : "text-[var(--ink-1)]"
                  }`}
                >
                  {team.points}
                </span>
              </div>

              {/* TÊN TỔ (Đặt dưới chân cột) */}
              <div className="mt-3 text-center">
                <span className="text-xs font-bold text-[var(--ink-1)] uppercase tracking-tight">
                  Tổ {team.teamId}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* 2. CÁC TỔ XẾP HẠNG CÒN LẠI (DẠNG GRID BENTO) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {others.map((team) => (
          <div
            key={team.teamId}
            className="flex items-center justify-between p-3.5 bg-surface rounded-xl border border-[var(--rule)] hover:border-[var(--rule-lg)] hover:shadow-xs transition-all group"
          >
            <div className="flex items-center gap-3">
              {/* Vòng tròn số thứ tự hạng, hover chuột vào sẽ đảo ngược màu cực bắt mắt */}
              <div className="w-8 h-8 rounded-lg bg-[var(--bg-surface-2)] flex items-center justify-center font-mono font-black text-xs text-[var(--ink-2)] border border-[var(--rule)] group-hover:bg-[var(--ink-1)] group-hover:text-surface transition-colors">
                #{team.rank}
              </div>
              <span className="text-xs font-bold text-[var(--ink-1)] uppercase tracking-tight">
                Tổ {team.teamId}
              </span>
            </div>
            <div className="text-right">
              {/* Điểm số ăn theo font-mono sọc hiện đại, không sợ trồi sụt số */}
              <span className="text-sm font-mono font-black [font-variant-numeric:lining-nums] text-[var(--warm-400)]">
                {team.points}
              </span>
              <span className="text-[9px] block font-bold text-[var(--ink-3)] uppercase tracking-wider">
                Điểm
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
