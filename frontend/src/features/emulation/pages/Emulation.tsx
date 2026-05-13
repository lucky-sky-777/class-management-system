import { useState, useEffect, useMemo } from "react";
import { Plus, Minus } from "lucide-react";
import { useParams } from "react-router-dom";
import { useEmulation } from "@features/emulation/hooks/useEmulation";
import { FilterSelect } from "@features/emulation/pages/FilterSelect";
import { RankingTable } from "@features/emulation/pages/RankingTable";
import { HistoryTable } from "@features/emulation/pages/HistoryTable";
import { emulationAPI } from "@features/emulation/api"; 
import { Modal } from "@shared/components/ui/Modal";

// --- Hàm bổ trợ tính các tuần trong tháng ---
const getWeeksInMonth = (month: number, year: number) => {
  const weeks = [];
  const firstDate = new Date(year, month - 1, 1);
  const lastDate = new Date(year, month, 0);

  const currentStart = new Date(firstDate);
  while (currentStart.getDay() !== 1) {
    currentStart.setDate(currentStart.getDate() + 1);
  }

  while (currentStart <= lastDate) {
    const currentEnd = new Date(currentStart);
    currentEnd.setDate(currentEnd.getDate() + 6);

    weeks.push({
      label: `${currentStart.getDate()}/${month} - ${currentEnd.getDate()}/${month}`,
      start: new Date(currentStart).toISOString(),
      end: new Date(currentEnd).toISOString(),
    });

    currentStart.setDate(currentStart.getDate() + 7);
  }
  return weeks;
};

export const Emulation = () => {
  const { classId } = useParams<{ classId: string }>();
  // Lưu ý: Đảm bảo Hook của Hào trả về setFilters (có chữ s)
  const { data, isLoading, filters, setFilters, changeTeamCount, refresh } = useEmulation(classId!);
  
  const [selectedTeam, setSelectedTeam] = useState(1);
  const [showPointModal, setShowPointModal] = useState(false);
  const [pointForm, setPointForm] = useState<{
    content: string;
    points: number | string;
  }>({
    content: "",
    points: 0,
  });

  const canEdit = true; // TODO: Phân quyền sau

  // 1. TÍNH TOÁN DỮ LIỆU (Phải nằm trên Early Return)
  const weeks = useMemo(() => getWeeksInMonth(filters.month, filters.year), [filters.month, filters.year]);

  // 2. USE_EFFECT (Phải nằm trên Early Return để không vi phạm rules of hooks)
  useEffect(() => {
    if (weeks.length > 0 && !filters.startDate) {
      setFilters({ startDate: weeks[0].start, endDate: weeks[0].end });
    }
  }, [weeks, filters.startDate, setFilters]);

  // 3. EARLY RETURN (Chỉ return sau khi đã gọi hết Hooks)
  if (isLoading || !data) {
    return <div className="p-10 text-center text-ink-3 font-medium animate-pulse">Đang tải dữ liệu thi đua...</div>;
  }

  const currentTeamMembers = data?.teams?.[selectedTeam] || [];

  // 4. HÀM XỬ LÝ GHI ĐIỂM
  const handleSubmitPoint = async () => {
    const pointsToSubmit = Number(pointForm.points);
    if (!pointForm.content) return alert("Vui lòng nhập nội dung!");
    if (isNaN(pointsToSubmit) || pointsToSubmit === 0) {
      return alert("Số điểm không hợp lệ!");
    }

    try {
      await emulationAPI.addPoints(
        classId!,
        selectedTeam,
        pointForm.content,
        pointsToSubmit,
      );
      alert(`Đã cập nhật điểm cho Tổ ${selectedTeam}`);
      setShowPointModal(false);
      setPointForm({ content: "", points: 0 }); // Reset form
      refresh(); // Load lại lịch sử và bảng xếp hạng
    } catch (error) {
      console.error("Lỗi nhập điểm", error);
      alert("Lỗi nhập điểm");
    }
  };

  return (
    <div className="space-y-6 animate-fade-in">
      {/* THANH BỘ LỌC */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center bg-[var(--bg-surface)] p-4 rounded-2xl shadow-sm border border-[var(--rule)] gap-4">
        <div className="flex flex-wrap items-center gap-4 md:gap-6">
          <FilterSelect
            label="Tháng"
            val={filters.month}
            options={[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]}
            onChange={(m) => setFilters({ month: m, startDate: "", endDate: "" })} 
          />

          <div className="flex items-center gap-2">
            <span className="text-xs text-[var(--ink-3)] font-bold uppercase tracking-wider">Từ:</span>
            <select
              className="text-xs font-bold bg-[var(--bg-surface-2)] text-[var(--ink-1)] border-none rounded-lg py-2 px-3 cursor-pointer outline-none focus:ring-2 focus:ring-[var(--ink-blue-fill)]"
              value={filters.startDate}
              onChange={(e) => {
                const selected = weeks.find(w => w.start === e.target.value);
                if (selected) setFilters({ startDate: selected.start, endDate: selected.end });
              }}
            >
              {weeks.map((w, index) => (
                <option key={index} value={w.start}>{w.label}</option>
              ))}
            </select>
          </div>
        </div>
        
        <button className="bg-[var(--ink-green-text)] text-white px-5 py-2.5 rounded-xl font-bold text-xs shadow-md hover:opacity-90 transition-all">
          NỘI QUY
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-4 gap-6">
        {/* DANH SÁCH TỔ */}
        <div className="bg-[var(--bg-surface)] p-4 rounded-2xl border border-[var(--rule)] shadow-sm h-fit">
          <div className="flex items-center justify-between mb-4">
            <p className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest">
              Danh sách tổ
            </p>

            {canEdit && (
              <div className="flex gap-1">
                <button
                  onClick={() => changeTeamCount(data.teamCount - 1)}
                  className="p-1 hover:bg-[var(--ink-red-fill)] text-[var(--ink-red-text)] rounded border border-[var(--ink-red-border)] transition-colors"
                  title="Bớt 1 tổ"
                >
                  <Minus size={12} />
                </button>
                <button
                  onClick={() => changeTeamCount(data.teamCount + 1)}
                  className="p-1 hover:bg-[var(--ink-green-fill)] text-[var(--ink-green-text)] rounded border border-[var(--ink-green-border)] transition-colors"
                  title="Thêm 1 tổ"
                >
                  <Plus size={12} />
                </button>
              </div>
            )}
          </div>

          {/* CÁC NÚT CHỌN TỔ */}
          <div className="grid grid-cols-2 lg:grid-cols-1 gap-2 mb-4">
            {Array.from({ length: data.teamCount }).map((_, i) => (
              <button
                key={i}
                onClick={() => setSelectedTeam(i + 1)}
                className={`py-2 rounded-xl text-sm font-bold border transition-all ${
                  selectedTeam === i + 1
                    ? "bg-[var(--bg-surface-2)] border-[var(--rule-md)] text-[var(--ink-1)] shadow-inner"
                    : "bg-[var(--bg-surface)] border-transparent text-[var(--ink-2)] hover:bg-[var(--bg-surface-2)]"
                }`}
              >
                Tổ {i + 1}
              </button>
            ))}
          </div>

          {/* HIỂN THỊ THÀNH VIÊN TRONG TỔ (Đã dọn dẹp nút xóa) */}
          <div className="pt-4 border-t border-[var(--rule)]">
            <p className="text-[10px] font-black text-[var(--ink-3)] uppercase tracking-widest mb-3">
              Thành viên Tổ {selectedTeam}
            </p>
            <div className="space-y-2 min-h-[50px] max-h-[300px] overflow-y-auto custom-scrollbar pr-2">
              {currentTeamMembers.length > 0 ? (
                currentTeamMembers.map((member, idx) => (
                  <div
                    key={member.id}
                    className="flex items-center justify-between py-1 border-b border-dashed border-[var(--rule)] last:border-0"
                  >
                    <span className="text-sm font-medium text-[var(--ink-1)]">
                      {idx + 1}. {member.name}
                    </span>
                  </div>
                ))
              ) : (
                <p className="text-[11px] text-[var(--ink-3)] italic text-center py-4">
                  Chưa có thành viên
                </p>
              )}
            </div>
          </div>
        </div>

        {/* LỊCH SỬ THAY ĐỔI */}
        <HistoryTable
          selectedTeam={selectedTeam}
          history={data.history}
          canEdit={canEdit}
          onOpenPointModal={() => setShowPointModal(true)}
        />
      </div>

      <RankingTable title="Xếp hạng tuần" rows={data.weeklyRanking} />
      <RankingTable
        title="Xếp hạng tháng"
        rows={data.monthlyRanking}
        isMonthly
      />

      {/* MODAL GHI ĐIỂM */}
      <Modal 
        isOpen={showPointModal} 
        onClose={() => { setShowPointModal(false); setPointForm({ content: "", points: 0 }); }} 
        title={`Ghi điểm Tổ ${selectedTeam}`}
      >
        <div className="space-y-4">
          <div>
            <label className="text-[11px] font-black text-[var(--ink-3)] uppercase mb-1 block">
              Nội dung
            </label>
            <input
              type="text"
              className="w-full border border-[var(--rule)] rounded-xl p-3 text-sm focus:ring-2 focus:ring-[var(--ink-blue-text)] outline-none transition-all"
              placeholder="Ví dụ: Phát biểu xây dựng bài..."
              value={pointForm.content}
              onChange={(e) =>
                setPointForm({ ...pointForm, content: e.target.value })
              }
            />
          </div>

          <div>
            <label className="text-[11px] font-black text-[var(--ink-3)] uppercase mb-1 block">
              Số điểm (+ cộng, - trừ)
            </label>
            <input
              type="text"
              inputMode="text"
              className="w-full border border-[var(--rule)] rounded-xl p-3 text-sm focus:ring-2 focus:ring-[var(--ink-blue-text)] outline-none transition-all"
              placeholder="Ví dụ: 10 hoặc -5"
              value={pointForm.points === 0 ? "" : pointForm.points}
              onChange={(e) => {
                const val = e.target.value;
                if (val === "" || val === "-" || !isNaN(Number(val))) {
                  setPointForm({
                    ...pointForm,
                    points: val, 
                  });
                }
              }}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSubmitPoint();
              }}
            />
          </div>

          <div className="flex gap-3 pt-2">
            <button
              onClick={() => {
                setShowPointModal(false);
                setPointForm({ content: "", points: 0 });
              }}
              className="flex-1 py-3 bg-[var(--bg-surface-2)] text-[var(--ink-2)] rounded-xl font-bold text-sm hover:bg-[var(--bg-surface-3)] transition-colors"
            >
              Hủy
            </button>
            <button
              onClick={handleSubmitPoint}
              className="flex-1 py-3 bg-[var(--ink-blue-text)] text-white rounded-xl font-bold text-sm shadow-lg shadow-[var(--ink-blue-fill)] hover:opacity-90 transition-all"
            >
              Xác nhận
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
};