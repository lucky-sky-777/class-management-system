import React from "react";
import type { ActivitySummary } from "@features/activity/types";

interface SummaryTableProps {
    summaries: ActivitySummary[];
    isLoading: boolean;
}

export const SummaryTable: React.FC<SummaryTableProps> = ({summaries, isLoading}: SummaryTableProps) => {
    if (isLoading) {
        return (
            <div className="space-y-3">
                {[1, 2, 3, 4].map((i) => (
                    <div key={i} className="h-12 skeleton rounded-xl"/>
                ))}
            </div>
        );
    }

    if (summaries.length === 0) {
        return (
            <div className="py-16 text-center space-y-2 bg-surface-2/50 rounded-xl border-2 border-dashed border-rule">
                <p className="text-3xl">◈</p>
                <p className="font-semibold text-ink-2">
                    Chưa có dữ liệu thống kê
                </p>
            </div>
        );
    }

    return (
        <div className="overflow-x-auto">
            <table className="data-table">
                <thead>
                <tr>
                    <th className="w-16 text-center">Hạng</th>
                    <th>Thành viên</th>
                    <th className="text-center">Điểm</th>
                </tr>
                </thead>

                <tbody>
                {summaries.map((summary) => (
                    <tr key={summary.userId}>
                        <td className="text-center font-mono text-ink-3">
                            {summary.rank ?? "—"}
                        </td>

                        <td>
                            <div className="flex items-center gap-2.5">
                                {summary.userAvatarUrl ? (
                                    <img
                                        src={summary.userAvatarUrl}
                                        alt={summary.userDisplayName}
                                        className="w-8 h-8 rounded-full object-cover border border-rule"
                                    />
                                ) : (
                                    <div
                                        className="w-8 h-8 rounded-full bg-surface-3 flex items-center justify-center text-xs font-bold border border-rule">
                                        {summary.userDisplayName
                                            .charAt(0)
                                            .toUpperCase()}
                                    </div>
                                )}

                                <span className="font-medium text-ink-1">
                                        {summary.userDisplayName}
                                    </span>
                            </div>
                        </td>

                        <td className="font-semibold font-mono text-ink-1">
                            {summary.totalPoint}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};