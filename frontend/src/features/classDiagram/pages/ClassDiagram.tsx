// src/features/classDiagram/pages/ClassDiagram.tsx
import { useState, useMemo, useRef, useEffect, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import * as htmlToImage from 'html-to-image';
import {
  GraduationCap,
  Presentation,
  Shuffle,
  ZoomIn,
  ZoomOut,
  Maximize,
  Map as MapIcon,
} from "lucide-react";
import { useClassDiagram } from "@features/classDiagram/hooks/useClassDiagram";
import { Group } from "@features/classDiagram/pages/Group";
import { TransformWrapper, TransformComponent } from "react-zoom-pan-pinch";

export const ClassDiagram = () => {
  const { classId } = useParams();
  const [mode, setMode] = useState<"view" | "attendance" | "setup">("view");
  const [selectedStudentId, setSelectedStudentId] = useState<string | null>(
      null,
  );
  const [perspective, setPerspective] = useState<"student" | "teacher">(
      "student",
  );
  const isTeacherView = perspective === "teacher";

  const {
    data,
    unseatedMembers,
    isLoading,
    shuffle,
    assignSeat,
    markAttendance,
  } = useClassDiagram(classId!, mode);

  // --- LOGIC MINIMAP KHỞI TẠO ---
  const wrapperRef = useRef<HTMLDivElement>(null); // Khung view ngoài cùng cố định
  const contentRef = useRef<HTMLDivElement>(null); // Khối Canvas sơ đồ thay đổi kích thước
  const [minimapImg, setMinimapImg] = useState<string | null>(null);
  const [showMinimap, setShowMinimap] = useState(true);

  // Lưu vị trí & kích thước hộp View Box trên Minimap (tính theo %)
  //const [viewBox, setViewBox] = useState({ left: 0, top: 0, width: 100, height: 100 });
  const viewBoxRef = useRef<HTMLDivElement>(null);

  const minimapRef = useRef<HTMLDivElement>(null); // Trỏ tới thẻ chứa toàn bộ Minimap
  const isDraggingMinimap = useRef(false); // Đánh dấu trạng thái đang nhấn giữ chuột
  const currentMapState = useRef({ scale: 0.4, x: 0, y: 0 }); // Lưu lại tọa độ & mức zoom hiện tại của Map chính

  // Hàm chụp ảnh sơ đồ đang hiển thị
  // const captureMinimap = async () => {
  //   if (contentRef.current) {
  //     try {
  //       const canvas = await html2canvas(contentRef.current, {
  //         scale: 0.5, // Giảm tỷ lệ chụp để tối ưu dung lượng ảnh base64 và hiệu năng render
  //         backgroundColor: null,
  //         useCORS: true,
  //       });
  //       setMinimapImg(canvas.toDataURL("image/png"));
  //     } catch (error) {
  //       console.error("Lỗi khi kết xuất ảnh sơ đồ thu nhỏ:", error);
  //     }
  //   }
  // };

  const captureMinimap = async () => {
    if (contentRef.current) {
      try {
        const dataUrl = await htmlToImage.toPng(contentRef.current, {
          quality: 0.8,
          pixelRatio: 0.5, // Giảm tỷ lệ để ảnh nhẹ hơn, tương đương scale: 0.5
          skipFonts: false,
          cacheBust: true,
            // filter: (node) => {
            // // Bỏ qua các thẻ img (chỉ lấy layout và text)
            //   if (node.tagName === 'IMG') return false;
            //   return true;
            // }
        });
        setMinimapImg(dataUrl);
      } catch (error) {
        console.error("Lỗi khi kết xuất ảnh sơ đồ thu nhỏ:", error);
      }
    }
  };

  // Tự động cập nhật lại ảnh nền Minimap khi dữ liệu lớp hoặc góc nhìn xoay thay đổi
  // useEffect(() => {
  //   if (data) {
  //     const timer = setTimeout(() => captureMinimap(), 1000);
  //     return () => clearTimeout(timer);
  //   }
  // }, [data, perspective]);

  useEffect(() => {
    if (data) {
      // Đặt timeout 600ms để đảm bảo CSS transition (duration-500) đã chạy xong hoàn toàn
      const timer = setTimeout(() => captureMinimap(), 600);
      return () => clearTimeout(timer);
    }
  }, [data, perspective]);

  // // Xử lý sự kiện di chuyển/phóng to sơ đồ chính để cập nhật View Box
  // const handleTransformed = useCallback((ref: any) => {
  //   if (!contentRef.current || !wrapperRef.current) return;
  //
  //   const { scale, positionX, positionY } = ref.state;
  //
  //   // Đo kích thước nguyên bản của vùng chứa sơ đồ
  //   const contentW = contentRef.current.offsetWidth;
  //   const contentH = contentRef.current.offsetHeight;
  //
  //   // Đo kích thước khung nhìn thực tế của màn hình thiết bị
  //   const wrapperW = wrapperRef.current.offsetWidth;
  //   const wrapperH = wrapperRef.current.offsetHeight;
  //
  //   // Kích thước thực tế sau khi bị tác động bởi mức phóng to (Scale)
  //   const scaledContentW = contentW * scale;
  //   const scaledContentH = contentH * scale;
  //
  //   // Tỷ lệ phần trăm diện tích hiển thị trên tổng thể sơ đồ
  //   const visibleWidthPct = (wrapperW / scaledContentW) * 100;
  //   const visibleHeightPct = (wrapperH / scaledContentH) * 100;
  //
  //   // Tỷ lệ phần trăm tọa độ điểm bắt đầu dịch chuyển (Pan)
  //   const leftPct = (-positionX / scaledContentW) * 100;
  //   const topPct = (-positionY / scaledContentH) * 100;
  //
  //   setViewBox({
  //     left: Math.max(0, leftPct),
  //     top: Math.max(0, topPct),
  //     width: Math.min(100 - Math.max(0, leftPct), visibleWidthPct),
  //     height: Math.min(100 - Math.max(0, topPct), visibleHeightPct)
  //   });
  // }, []);
  // Xử lý sự kiện di chuyển/phóng to sơ đồ chính để cập nhật View Box
  const handleTransformed = useCallback((ref: any) => {
    if (!contentRef.current || !wrapperRef.current) return;

    const { scale, positionX, positionY } = ref.state;

    currentMapState.current = { scale, x: positionX, y: positionY };

    // Đo kích thước nguyên bản của vùng chứa sơ đồ
    const contentW = contentRef.current.offsetWidth;
    const contentH = contentRef.current.offsetHeight;

    // Đo kích thước khung nhìn thực tế của màn hình thiết bị
    const wrapperW = wrapperRef.current.offsetWidth;
    const wrapperH = wrapperRef.current.offsetHeight;

    // Kích thước thực tế sau khi bị tác động bởi mức phóng to (Scale)
    const scaledContentW = contentW * scale;
    const scaledContentH = contentH * scale;

    // Tỷ lệ phần trăm diện tích hiển thị trên tổng thể sơ đồ
    const visibleWidthPct = (wrapperW / scaledContentW) * 100;
    const visibleHeightPct = (wrapperH / scaledContentH) * 100;

    // Tỷ lệ phần trăm tọa độ điểm bắt đầu dịch chuyển (Pan)
    const leftPct = (-positionX / scaledContentW) * 100;
    const topPct = (-positionY / scaledContentH) * 100;

    // ĐÃ FIX: Loại bỏ Math.max và Math.min để box tự do di chuyển vượt ranh giới
    // CSS overflow-hidden ở thẻ cha sẽ tự lo phần cắt xén hiển thị
    // setViewBox({
    //   left: leftPct,
    //   top: topPct,
    //   width: visibleWidthPct,
    //   height: visibleHeightPct
    // });
    if (viewBoxRef.current) {
      viewBoxRef.current.style.left = `${leftPct}%`;
      viewBoxRef.current.style.top = `${topPct}%`;
      viewBoxRef.current.style.width = `${visibleWidthPct}%`;
      viewBoxRef.current.style.height = `${visibleHeightPct}%`;
    }
  }, []);
  // --- KẾT THÚC LOGIC MINIMAP ---

  // THUẬT TOÁN CHIA CỘT TỔ CHỖ NGỒI
  const currentGroups = data?.groups;
  const groupColumns = useMemo(() => {
    if (!currentGroups) return [];
    const sortedGroups = [...currentGroups].sort(
        (a, b) => a.groupId - b.groupId,
    );
    const columns: (typeof sortedGroups)[] = [];
    for (let i = 0; i < sortedGroups.length; i += 2) {
      columns.push(sortedGroups.slice(i, i + 2));
    }
    return columns;
  }, [currentGroups]);

  if (isLoading || !data) {
    return (
        <div className="flex flex-col items-center justify-center min-h-[400px] space-y-4">
          <div className="animate-in fade-in duration-300 flex flex-col items-center space-y-4">
            <div className="animate-spin rounded-full h-10 w-10 border-b-2 border-[var(--warm-400)]"></div>
            <p className="text-ink-2 text-sm font-medium">
              Đang tải sơ đồ lớp học...
            </p>
          </div>
        </div>
    );
  }

  const handleSeatClick = async (
      groupId: number,
      deskId: number,
      positionId: number,
  ) => {
    const targetGroup = data.groups.find((g) => g.groupId === groupId);
    const targetDesk = targetGroup?.desks.find((d) => d.deskId === deskId);
    const targetPos = targetDesk?.positions.find(
        (p) => p.positionId === positionId,
    );
    const studentAtSeat = targetPos?.student;

    if (mode === "attendance" && studentAtSeat?.id) {
      await markAttendance(groupId, studentAtSeat.id, studentAtSeat.status);
    }

    if (mode === "setup") {
      if (!selectedStudentId) {
        if (studentAtSeat) setSelectedStudentId(studentAtSeat.id);
        return;
      }
      if (selectedStudentId === studentAtSeat?.id) {
        setSelectedStudentId(null);
        return;
      }

      let sourceGroup: number | null = null;
      let sourceDesk: number | null = null;
      let sourcePos: number | null = null;

      data.groups.forEach((g) =>
          g.desks.forEach((d) =>
              d.positions.forEach((p) => {
                if (p.student?.id === selectedStudentId) {
                  sourceGroup = p.student.groupId;
                  sourceDesk = p.student.deskId;
                  sourcePos = p.student.positionId;
                }
              }),
          ),
      );

      const isSuccess = await assignSeat(
          selectedStudentId,
          groupId,
          deskId,
          positionId,
          sourceGroup,
          sourceDesk,
          sourcePos,
      );

      if (isSuccess) setSelectedStudentId(null);
    }
  };

  return (
      <div
          className="space-y-6 select-none max-w-7xl mx-auto p-2 md:p-4 bg-[var(--bg-paper)] min-h-screen overflow-hidden"
          onClick={() => setSelectedStudentId(null)}
      >
        {/* 1. THANH CÔNG CỤ */}
        <div
            className="flex flex-col gap-4 bg-[var(--bg-surface)] p-3 rounded-xl border border-[var(--rule-md)] shadow-[var(--shadow-sm)] relative z-10"
            onClick={(e) => e.stopPropagation()}
        >
          <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3 w-full">
            <div className="flex bg-[var(--bg-surface-2)] p-1 rounded-lg w-full sm:w-auto border border-[var(--rule)]">
              {(["view", "attendance", "setup"] as const).map((m) => (
                  <button
                      key={m}
                      onClick={() => setMode(m)}
                      className={`flex-1 sm:flex-none px-3 py-2 rounded-md text-[11px] md:text-xs font-bold transition-all flex items-center justify-center gap-1 ${
                          mode === m
                              ? "bg-[var(--bg-surface)] text-[var(--warm-600)] shadow-[var(--shadow-xs)]"
                              : "text-[var(--ink-2)] hover:text-[var(--ink-1)]"
                      }`}
                  >
                <span className="hidden xs:inline">
                  {m === "view"
                      ? "Xem"
                      : m === "attendance"
                          ? "Điểm danh"
                          : "Xếp chỗ"}
                </span>
                    <span className="xs:hidden">
                  {m === "view"
                      ? "Xem"
                      : m === "attendance"
                          ? "Điểm danh"
                          : "Xếp"}
                </span>
                  </button>
              ))}
            </div>

            <div className="flex items-center gap-2 overflow-x-auto w-full sm:w-auto pb-1 sm:pb-0 no-scrollbar justify-start sm:justify-end">
              <Badge theme="neutral" label="Sĩ số" val={data.totalStudents} />
              <Badge theme="success" label="Có mặt" val={data.presentCount} />
              <Badge theme="warning" label="Vắng phép" val={data.excusedCount} />
              <Badge
                  theme="danger"
                  label="Không phép"
                  val={data.unexcusedCount}
              />
            </div>
          </div>
        </div>

        {/* 2. KHU VỰC ĐIỀU KHIỂN RIÊNG */}
        <div className="flex flex-col-reverse md:flex-row justify-between items-end gap-4 w-full relative z-10">
          <div className="w-full md:w-2/3" onClick={(e) => e.stopPropagation()}>
            {mode === "setup" && (
                <div className="bg-[var(--warm-fill)] p-3 rounded-xl border border-[var(--warm-border)] animate-in fade-in zoom-in-95 duration-300 flex flex-col gap-3">
                  <div className="flex justify-between items-start sm:items-center gap-2 mb-3">
                    <div className="flex-1 leading-snug">
                  <span className="text-[10px] md:text-xs font-bold text-[var(--warm-600)] uppercase tracking-wider">
                    Danh sách xếp chỗ
                  </span>
                      <span className="text-[9px] md:text-[10px] text-[var(--ink-3)] font-medium ml-1.5 normal-case tracking-normal inline-block">
                    (Cần phân tổ trước khi xếp chỗ.{" "}
                        <Link
                            to={`/class/${classId}/emulation`}
                            className="font-bold text-[var(--warm-600)] underline hover:text-[var(--warm-800)] transition-colors"
                        >
                      Tại đây!
                    </Link>
                    )
                  </span>
                    </div>

                    <button
                        onClick={() => shuffle()}
                        className="group flex items-center shrink-0 gap-1.5 px-3 py-1.5 bg-white border border-[var(--warm-border)] text-[var(--warm-600)] rounded-lg text-[10px] md:text-xs font-bold shadow-[var(--shadow-sm)] hover:bg-[var(--bg-surface-2)] transition-all active:scale-95"
                    >
                      <Shuffle
                          size={14}
                          strokeWidth={2.5}
                          className="transition-transform duration-300 group-hover:rotate-45"
                      />
                      <span className="tracking-wide">Xếp tự động</span>
                    </button>
                  </div>

                  <div className="flex flex-wrap gap-2 max-h-24 overflow-y-auto pr-2 custom-scrollbar">
                    {unseatedMembers.map((m) => {
                      let isAssigned = false;
                      data.groups.forEach((g) =>
                          g.desks.forEach((d) =>
                              d.positions.forEach((p) => {
                                if (p.student?.id === m.id) isAssigned = true;
                              }),
                          ),
                      );

                      return (
                          <button
                              key={m.id}
                              onClick={() => setSelectedStudentId(m.id)}
                              className={`px-3 py-1.5 rounded-md text-[10px] md:text-xs font-bold border transition-all ${
                                  selectedStudentId === m.id
                                      ? "bg-[var(--warm-600)] text-white border-[var(--warm-600)] shadow-[var(--shadow-sm)]"
                                      : isAssigned
                                          ? "bg-[var(--bg-surface-3)] text-[var(--ink-3)] border-[var(--rule)]"
                                          : "bg-[var(--bg-surface)] text-[var(--ink-1)] border-[var(--rule-md)] hover:bg-[var(--bg-surface-2)]"
                              }`}
                          >
                            {m.name} {isAssigned && "✓"}
                          </button>
                      );
                    })}
                  </div>
                </div>
            )}
          </div>

          <div
              className="flex justify-end w-full md:w-auto"
              onClick={(e) => e.stopPropagation()}
          >
            <button
                onClick={() =>
                    setPerspective(isTeacherView ? "student" : "teacher")
                }
                className="group flex items-center justify-center gap-2 px-4 py-2 rounded-xl text-[11px] md:text-xs font-bold bg-[var(--bg-surface)] text-[var(--warm-600)] border-2 border-[var(--warm-border)] hover:bg-[var(--warm-fill)] transition-all shadow-[var(--shadow-sm)] w-full sm:w-auto"
            >
              <div className="text-[var(--warm-600)] transition-transform duration-300 group-hover:scale-110">
                {isTeacherView ? (
                    <Presentation size={15} strokeWidth={2.5} />
                ) : (
                    <GraduationCap size={16} strokeWidth={2.5} />
                )}
              </div>
              <span className="uppercase tracking-wider">
              Góc nhìn: {isTeacherView ? "Giáo viên" : "Học sinh"}
            </span>
            </button>
          </div>
        </div>

        {/* 3. CONTAINER SƠ ĐỒ LỚP HỌC */}
        <div
            ref={wrapperRef}
            className="w-full h-[75vh] min-h-[500px] relative overflow-hidden px-2 md:px-0 bg-gray-50/30 rounded-xl border-2 border-[var(--rule-md)] shadow-sm"
            onClick={(e) => e.stopPropagation()}
        >
          <TransformWrapper
              initialScale={0.4}
              minScale={0.1}
              maxScale={3}
              centerOnInit={true}
              centerZoomedOut={true}
              limitToBounds={false}
              panning={{
                allowLeftClickPan: true,
                velocityDisabled: true,
              }}
              wheel={{ disabled: true }}
              pinch={{ step: 2 }}
              onTransform={handleTransformed} // Kích hoạt khi bấm nút Zoom In/Out/Reset
              onPanning={handleTransformed}     // Kích hoạt LIÊN TỤC khi đang kéo (Drag)
              onZoom={handleTransformed}     // Kích hoạt LIÊN TỤC khi đang phóng to/thu nhỏ
              onPinch={handleTransformed}    // Kích hoạt LIÊN TỤC khi dùng 2 ngón tay trên điện thoại
          >
            {({ zoomIn, zoomOut, resetTransform, setTransform }) => {
                const updateMapFromMinimap = (e: React.PointerEvent<HTMLDivElement>) => {
                    const minimapEl = minimapRef.current;
                    if (!contentRef.current || !wrapperRef.current || !minimapEl) return;

                    // 1. Lấy vị trí của Minimap trên màn hình
                    const rect = minimapEl.getBoundingClientRect();

                    // 2. Tính vị trí trỏ chuột (từ 0 đến 1, tương đương 0% -> 100%)
                    let xPct = (e.clientX - rect.left) / rect.width;
                    let yPct = (e.clientY - rect.top) / rect.height;

                    // Giới hạn để chuột không kéo Box bay ra khỏi viền Minimap
                    xPct = Math.max(0, Math.min(1, xPct));
                    yPct = Math.max(0, Math.min(1, yPct));

                    // 3. Lấy thông số hiện tại của map chính
                    const scale = currentMapState.current.scale;
                    const contentW = contentRef.current.offsetWidth;
                    const contentH = contentRef.current.offsetHeight;
                    const wrapperW = wrapperRef.current.offsetWidth;
                    const wrapperH = wrapperRef.current.offsetHeight;

                    const scaledContentW = contentW * scale;
                    const scaledContentH = contentH * scale;

                    // 4. Căn giữa View Box vào con trỏ chuột
                    const viewBoxWidthPct = wrapperW / scaledContentW;
                    const viewBoxHeightPct = wrapperH / scaledContentH;
                    const leftPct = xPct - (viewBoxWidthPct / 2);
                    const topPct = yPct - (viewBoxHeightPct / 2);

                    // 5. Quy đổi ra Pixel và ép map chính di chuyển
                    const newX = -(leftPct * scaledContentW);
                    const newY = -(topPct * scaledContentH);

                    // setTransform(x, y, scale, animationTime)
                    setTransform(newX, newY, scale, 0);
                };

                // Khi bắt đầu nhấn chuột vào Minimap
                const handlePointerDown = (e: React.PointerEvent<HTMLDivElement>) => {
                    e.stopPropagation(); // Ngăn không cho map chính nhận sự kiện click này
                    isDraggingMinimap.current = true;

                    if (minimapRef.current) {
                        minimapRef.current.setPointerCapture(e.pointerId); // Khóa con trỏ
                    }

                    updateMapFromMinimap(e); // Cập nhật vị trí ngay cú click đầu tiên
                };

                // Khi di chuyển chuột (chỉ chạy nếu đang nhấn giữ)
                const handlePointerMove = (e: React.PointerEvent<HTMLDivElement>) => {
                    if (!isDraggingMinimap.current) return;
                    updateMapFromMinimap(e);
                };

                // Khi nhả chuột ra
                const handlePointerUp = (e: React.PointerEvent<HTMLDivElement>) => {
                    isDraggingMinimap.current = false;
                    if (minimapRef.current) {
                        minimapRef.current.releasePointerCapture(e.pointerId); // Mở khóa con trỏ
                    }
                };

              return (
                  <div className="w-full h-full relative">
                    {/* THANH CÔNG CỤ ZOOM */}
                    <div className="absolute top-4 left-4 z-10 flex flex-col gap-2 bg-[var(--bg-surface)]/90 backdrop-blur-md p-1.5 rounded-2xl shadow-md border border-[var(--rule-md)]">
                      <button
                          onClick={() => zoomIn()}
                          className="p-2 text-[var(--ink-2)] hover:text-[var(--ink-1)]"
                          title="Phóng to"
                      >
                        <ZoomIn size={18} />
                      </button>
                      <div className="w-full h-px bg-[var(--rule-md)]" />
                      <button
                          onClick={() => zoomOut()}
                          className="p-2 text-[var(--ink-2)] hover:text-[var(--ink-1)]"
                          title="Thu nhỏ"
                      >
                        <ZoomOut size={18} />
                      </button>
                      <div className="w-full h-px bg-[var(--rule-md)]" />
                      <button
                          onClick={() => resetTransform()}
                          className="p-2 text-[var(--ink-2)] hover:text-[var(--ink-1)]"
                          title="Căn giữa"
                      >
                        <Maximize size={18} />
                      </button>
                      <div className="w-full h-px bg-[var(--rule-md)]" />
                      <button
                          onClick={() => setShowMinimap(!showMinimap)}
                          className={`p-2 transition-colors ${showMinimap ? "text-[var(--warm-600)]" : "text-[var(--ink-2)]"}`}
                          title="Bật/Tắt sơ đồ thu nhỏ"
                      >
                        <MapIcon size={18} />
                      </button>
                    </div>

                    {/* THÀNH PHẦN MINIMAP ĐIỀU KHIỂN NGHỊCH */}
                    {showMinimap && minimapImg && (
                        <div
                            ref={minimapRef} // Gắn ref để hàm updateMapFromMinimap đo kích thước

                            // GẮN 3 HÀM XỬ LÝ KÉO THẢ VÀO ĐÂY:
                            onPointerDown={handlePointerDown}
                            onPointerMove={handlePointerMove}
                            onPointerUp={handlePointerUp}
                            onPointerCancel={handlePointerUp} // Dự phòng khi drag ra ngoài trình duyệt

                            // CHÚ Ý: Đã xóa "pointer-events-none" ở cuối class này
                            // Thêm "cursor-move" để chuột đổi icon thành hình 4 mũi tên
                            className="absolute bottom-6 right-6 z-10 w-48 bg-[var(--bg-surface)] border-2 border-[var(--rule-md)] rounded-lg shadow-xl overflow-hidden animate-in fade-in zoom-in-95 duration-200 cursor-move select-none"

                            // Ngăn điện thoại tự động cuộn trang web khi đang vuốt Minimap
                            style={{ touchAction: "none" }}
                        >
                          <img src={minimapImg} alt="Sơ đồ lớp thu nhỏ" className="w-full h-auto opacity-60 bg-[var(--bg-paper)]" />

                          {/*/!* Hộp chỉ định vùng hiển thị hiện tại (View Box) *!/*/}
                          {/*<div*/}
                          {/*    className="absolute border-[1.5px] border-blue-500 bg-blue-500/10 shadow-[0_0_0_9999px_rgba(0,0,0,0.35)] transition-all duration-75"*/}
                          {/*    style={{*/}
                          {/*      left: `${viewBox.left}%`,*/}
                          {/*      top: `${viewBox.top}%`,*/}
                          {/*      width: `${viewBox.width}%`,*/}
                          {/*      height: `${viewBox.height}%`,*/}
                          {/*    }}*/}
                          {/*/>*/}
                          {/* Hộp chỉ định vùng hiển thị hiện tại (View Box) */}
                          <div
                              ref={viewBoxRef} // <-- THÊM DÒNG NÀY
                              // ĐÃ XÓA: "transition-all duration-75" để tránh xung đột với tốc độ drag
                              className="absolute border-[1.5px] border-blue-500 bg-blue-500/10 shadow-[0_0_0_9999px_rgba(0,0,0,0.35)]"
                              style={{
                                // Khởi tạo giá trị mặc định bao phủ 100% trước khi có sự kiện transform đầu tiên
                                left: '0%',
                                top: '0%',
                                width: '100%',
                                height: '100%',
                              }}
                          />
                        </div>
                    )}

                    {/* CANVAS SƠ ĐỒ CHÍNH */}
                    <TransformComponent
                        wrapperStyle={{ width: "100%", height: "100%", cursor: "grab" }}
                        contentStyle={{ width: "max-content", height: "max-content" }}
                    >
                      <div
                          ref={contentRef}
                          className="p-10"
                      >
                        <div
                            className="p-14 flex flex-col items-center bg-[var(--bg-surface)] border-[3px] border-[var(--rule-md)] rounded-[2rem] shadow-sm"
                            style={{ touchAction: "pan-y" }}
                        >
                          <div
                              className={`flex ${isTeacherView ? "flex-col-reverse" : "flex-col"} gap-16 transition-all duration-500`}
                          >
                            {/* BÀN GIÁO VIÊN VÀ BẢNG ĐEN */}
                            <div
                                className={`flex w-full items-center justify-center gap-16 ${isTeacherView ? "flex-row-reverse" : "flex-row"}`}
                            >
                              <div className="bg-[var(--bg-surface)] px-12 py-4 rounded-xl border-2 border-[var(--warm-border)] flex items-center justify-center relative shrink-0 min-w-[160px]">
                        <span className="text-xs font-black text-[var(--warm-600)] uppercase tracking-[0.15em]">
                          Bàn Giáo Viên
                        </span>
                              </div>
                              <div className="flex flex-col items-center gap-2 shrink-0">
                        <span className="text-xs font-black text-[var(--ink-3)] uppercase tracking-[0.4em]">
                          Bảng Đen
                        </span>
                                <div className="w-96 h-5 bg-[#1a1c23] rounded-md shadow-lg border-b-[4px] border-[#4a3f35]" />
                              </div>
                            </div>

                            {/* LƯỚI CHỖ NGỒI */}
                            <div
                                className={`flex flex-nowrap justify-center gap-12 ${mode !== "view" ? "cursor-crosshair" : ""} ${isTeacherView ? "rotate-180" : "rotate-0"}`}
                            >
                              {groupColumns.map((colGroups, colIndex) => (
                                  <div
                                      key={colIndex}
                                      className="flex flex-col gap-12 relative"
                                  >
                                    {colGroups.map((groupData) => (
                                        <Group
                                            key={groupData.groupId}
                                            groupData={groupData}
                                            isTeacherView={isTeacherView}
                                            onSeatClick={handleSeatClick}
                                            selectedStudentId={selectedStudentId}
                                        />
                                    ))}
                                  </div>
                              ))}
                            </div>
                          </div>
                        </div>
                      </div>
                    </TransformComponent>
                  </div>
              );
            }}
          </TransformWrapper>
        </div>
      </div>
  );
};

const Badge = ({
                 theme,
                 label,
                 val,
               }: {
  theme: "neutral" | "success" | "warning" | "danger";
  label: string;
  val: number;
}) => {
  const themeClasses = {
    neutral:
        "bg-[var(--bg-surface-3)] text-[var(--ink-2)] border-[var(--rule-md)]",
    success:
        "bg-[var(--green-fill)] text-[var(--green-text)] border-[var(--green-border)]",
    warning:
        "bg-[var(--amber-fill)] text-[var(--amber-text)] border-[var(--amber-border)]",
    danger:
        "bg-[var(--red-fill)] text-[var(--red-text)] border-[var(--red-border)]",
  };

  return (
      <div
          className={`px-2 py-1.5 rounded-lg text-[10px] md:text-[11px] font-bold shadow-sm border flex items-center gap-1.5 whitespace-nowrap ${themeClasses[theme]}`}
      >
        <span className="opacity-70">{label}:</span>
        <span className="font-black text-[var(--ink-1)]">{val}</span>
      </div>
  );
};