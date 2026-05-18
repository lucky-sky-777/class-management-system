export interface CompetitionHistory {
  id: string;
  date: string;
  content: string;
  points: number;
  teamId: number;
  actor: string;
}

export interface TeamRanking {
  rank: number;
  teamId: number;
  points: number;
  // Optional cho bảng xếp hạng tháng
  weeks?: { [key: string]: number };
}

export interface TeamMember {
  id: string;
  name: string;
}

export interface CompetitionData {
  teamCount: number;
  teams: { [key: number]: TeamMember[] };
  history: CompetitionHistory[];
  weeklyRanking: TeamRanking[];
  monthlyRanking: TeamRanking[];
}

export interface TeamRanking {
  teamId: number;
  rank: number;
  points: number;
}

export type GroupItem = {
  id: number;
  name: string;
};

export type GroupMember = {
  id?: string | number;
  user_id?: string | number;
  user_display_name?: string;
  display_name?: string;
  full_name?: string;
  attendance_status?: "PRESENT" | "ABSENT";
  joined_at?: string;
};

export type WeekItem = {
  week: number;
  start_at: string;
  end_at: string;
  formatted_start_at: string;
  formatted_end_at: string;
  is_current_week: boolean;
};
