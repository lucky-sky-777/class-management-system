import React from "react";
import type { UserActivitySummary } from "@features/activity/types";
import type { ID } from "@shared/utils/common";

interface SummaryTableProps {
    summaries: UserActivitySummary[];
    isLoading: boolean;
    /** Optional: map userId → display name */
    userNames?: Map<ID, string>;
}

const PointBar: React.FC<{ value: number; max: number }> = ({ value, max }) => {
    const pct = max > 0 ? Math.min((value / max) * 100, 100) : 0;
    const color =
        pct >= 80
            ? "bg-ink-green-text"
            : pct >= 50
              ? "bg-ink-blue-text"
              : pct >= 30
                ? "bg-amber-text"
                : "bg-ink-red-text";

    return (
        <div className="flex items-center gap-2.5">
            <div className="flex-1 h-1.5 bg-surface-3 rounded-full overflow-hidden">
                <div
                    className={`h-full rounded-full transition-all duration-500 ${color}`}
                    style={{ width: `${pct}%` }}
                />
            </div>
            <span className="font-mono text-xs text-ink-3 w-8 text-right">{value}</span>
        </div>
    );
};

export const SummaryTable: React.FC<SummaryTableProps> = ({
    summaries,
    isLoading,
    userNames,
}) => {
    const maxPoint = Math.max(...summaries.map((s) => s.totalPoint), 1);

    if (isLoading) {
        return (
            <div className="space-y-3">
                {[1, 2, 3, 4].map((i) => (
                    <div key={i} className="h-12 skeleton rounded-xl" />
                ))}
            </div>
        );
    }

    if (summaries.length === 0) {
        return (
            <div className="py-16 text-center space-y-2 bg-surface-2/50 rounded-xl border-2 border-dashed border-rule">
                <p className="text-3xl">◈</p>
                <p className="font-semibold text-ink-2">Chưa có dữ liệu thống kê</p>
                <p className="text-sm text-ink-3">
                    Khi thành viên đăng ký và được duyệt, thống kê sẽ xuất hiện ở đây.
                </p>
            </div>
        );
    }

    // Sort by totalPoint desc
    const sorted = [...summaries].sort((a, b) => b.totalPoint - a.totalPoint);

    return (
        <div className="overflow-x-auto">
            <table className="data-table">
                <thead>
                    <tr>
                        <th className="w-12 text-center">#</th>
                        <th>Thành viên</th>
                        <th className="text-center">Hoạt động đã duyệt</th>
                        <th className="text-center">Bắt buộc</th>
                        <th className="min-w-[180px]">Điểm rèn luyện</th>
                    </tr>
                </thead>
                <tbody>
                    {sorted.map((summary, idx) => {
                        const name = userNames?.get(summary.userId as ID) ?? `User #${summary.userId}`;
                        const mandatoryDone = summary.mandatoryApproved >= summary.mandatoryTotal;

                        return (
                            <tr key={summary.userId}>
                                <td className="text-center font-mono text-ink-3">{idx + 1}</td>
                                <td>
                                    <div className="flex items-center gap-2.5">
                                        <div className="w-7 h-7 rounded-full bg-surface-3 flex items-center justify-center text-ink-2 font-bold text-xs border border-rule">
                                            {name.charAt(0).toUpperCase()}
                                        </div>
                                        <span className="font-medium text-ink-1">{name}</span>
                                    </div>
                                </td>
                                <td className="text-center font-mono font-semibold text-ink-1">
                                    {summary.approvedCount}
                                </td>
                                <td className="text-center">
                                    {summary.mandatoryTotal === 0 ? (
                                        <span className="text-ink-3 text-xs">—</span>
                                    ) : mandatoryDone ? (
                                        <span className="pill pill-green text-xs">
                                            <span className="pill-dot" />
                                            {summary.mandatoryApproved}/{summary.mandatoryTotal}
                                        </span>
                                    ) : (
                                        <span className="pill pill-amber text-xs">
                                            <span className="pill-dot" />
                                            {summary.mandatoryApproved}/{summary.mandatoryTotal}
                                        </span>
                                    )}
                                </td>
                                <td>
                                    <PointBar value={summary.totalPoint} max={maxPoint} />
                                </td>
                            </tr>
                        );
                    })}
                </tbody>
            </table>
        </div>
    );
};
