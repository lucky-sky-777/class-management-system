import React from "react";
import type { TeamRanking } from "@features/emulation/types";
import Rank1 from "@assets/rank1.png";
import Rank2 from "@assets/rank2.png";
import Rank3 from "@assets/rank3.png";

interface RankingTableProps {
  title: string;
  rows: TeamRanking[];
}

export const RankingTable = ({ title, rows }: RankingTableProps) => {
  // Sắp xếp thứ tự hiển thị bục: Hạng 2 - Hạng 1 - Hạng 3
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
    <div className="space-y-8 animate-fade-in">
      {/* TIÊU ĐỀ BIỂU ĐỒ */}
      <div className="flex items-center gap-2 px-1">
        <div className="w-1 h-4 bg-[var(--warm-400)] rounded-full" />
        <h3 className="text-sm font-black uppercase tracking-widest text-[var(--ink-1)]">
          {title}
        </h3>
      </div>

      {/* 1. KHU VỰC BỤC VINH QUANG (PODIUM) */}
      <div className="flex items-end justify-center gap-3 md:gap-6 pt-16 pb-12 px-4 bg-[var(--bg-surface)] rounded-[var(--r-xl)] border border-[var(--rule)] shadow-[var(--shadow-sm)] relative overflow-hidden">
        {/* Background Decor */}
        <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-transparent via-[var(--warm-400)] to-transparent opacity-20" />

        {podiumOrder.map((team) => {
          if (!team) return null;
          const isTop1 = team.rank === 1;
          const podiumHeight = isTop1
            ? "h-36 md:h-44"
            : team.rank === 2
              ? "h-24 md:h-32"
              : "h-20 md:h-24";

          return (
            <div
              key={team.teamId}
              className="flex flex-col items-center flex-1 max-w-[140px] group"
            >
              {/* Huy hiệu: Giờ chỉ có huy hiệu thôi, không bị chữ đè */}
              <div className="relative mb-3 transition-transform group-hover:scale-110 duration-300">
                <img
                  src={getMedal(team.rank)!}
                  alt={`Hạng ${team.rank}`}
                  className={`${isTop1 ? "w-16 h-16 md:w-20 md:h-20" : "w-12 h-12 md:w-14 md:h-14"} object-contain drop-shadow-xl`}
                />
              </div>

              {/* Cột bục: Chứa điểm số */}
              <div
                className={`w-full rounded-t-2xl flex flex-col items-center justify-end pb-4 transition-all duration-500 ${podiumHeight} ${
                  isTop1
                    ? "bg-gradient-to-b from-[var(--warm-400)] to-[var(--warm-600)] shadow-lg"
                    : "bg-[var(--bg-surface-3)]"
                }`}
              >
                <span
                  className={`text-[9px] font-black uppercase tracking-widest mb-1 ${isTop1 ? "text-blue-100" : "text-[var(--ink-3)]"}`}
                >
                  Pts
                </span>
                <span
                  className={`text-xl md:text-2xl font-black ${isTop1 ? "text-white" : "text-[var(--ink-1)]"}`}
                >
                  {team.points}
                </span>
              </div>

              {/* TÊN TỔ: Dời xuống hẳn dưới chân bục */}
              <div className="mt-3 text-center">
                <span className="text-[11px] font-black text-[var(--ink-1)] uppercase tracking-tighter">
                  Tổ {team.teamId}
                </span>
              </div>
            </div>
          );
        })}
      </div>

      {/* 2. CÁC TỔ CÒN LẠI (DẠNG CARD TINH GỌN) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        {others.map((team) => (
          <div
            key={team.teamId}
            className="flex items-center justify-between p-3 bg-[var(--bg-surface)] rounded-[var(--r-lg)] border border-[var(--rule)] hover:border-[var(--rule-lg)] transition-all group"
          >
            <div className="flex items-center gap-3">
              <div className="w-8 h-8 rounded-lg bg-[var(--bg-surface-2)] flex items-center justify-center font-black text-[10px] text-[var(--ink-3)] border border-[var(--rule)] group-hover:bg-[var(--ink-1)] group-hover:text-white transition-colors">
                #{team.rank}
              </div>
              <span className="text-xs font-black text-[var(--ink-1)] uppercase">
                Tổ {team.teamId}
              </span>
            </div>
            <div className="text-right">
              <span className="text-sm font-black text-[var(--warm-400)]">
                {team.points}
              </span>
              <span className="text-[9px] block font-bold text-[var(--ink-3)] uppercase">
                Điểm
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
