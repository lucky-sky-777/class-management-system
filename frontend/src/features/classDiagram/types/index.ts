// Thêm 'late' (Đi trễ) và 'unmarked' (Chưa điểm danh)
export type AttendanceStatus = 
  | 'present' 
  | 'absent_excused' 
  | 'absent_unexcused' 
  | 'late'        // Mới: Đi trễ
  | 'unmarked'    // Mới: Chưa điểm danh (Trạng thái mặc định khi vừa vào lớp)
  | 'empty';      // (Vẫn giữ nếu UI của bạn đang dùng để render ô xám)

export interface StudentSeat {
  id: string;
  name: string;
  avatarUrl?: string | null; // Mới: Hỗ trợ hiển thị ảnh đại diện sau này
  
  // Tọa độ dành cho UI hiển thị
  row: number;    // Hàng 1, 2, 3...
  column: number; // Cột 1, 2, 3, 4
  side: 'left' | 'right';
  
  // Tọa độ gốc của Backend (Mới: Giúp gọi API Xếp chỗ cực dễ, không cần dịch ngược)
  groupId?: number; 
  deskId?: number;
  positionId?: number;

  status: AttendanceStatus;
}

export interface ClassDiagramData {
  totalStudents: number;
  presentCount: number;
  excusedCount: number;
  unexcusedCount: number;
  lateCount?: number; // Mới: Thống kê số người đi trễ
  seats: StudentSeat[];
}