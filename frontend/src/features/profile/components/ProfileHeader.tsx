import React from "react";
import { Camera, Link as LinkIcon } from "lucide-react";
import type { UserProfile } from "@/features/profile/types";

export const ProfileHeader: React.FC<{ profile: UserProfile }> = ({ profile }) => {
  return (
    <div className="bg-[var(--bg-surface)] rounded-2xl border border-[var(--rule)] overflow-hidden mb-6 shadow-sm">
      {/* Cover Bìa màu xanh */}
      <div className="h-48 bg-[#5B63C6] relative group">
        <button className="absolute top-4 right-4 p-2 bg-black/20 hover:bg-black/40 text-white rounded-lg backdrop-blur-sm transition-colors opacity-0 group-hover:opacity-100">
          <Camera size={20} />
        </button>
      </div>

      <div className="px-6 pb-6 relative">
        <div className="flex flex-col md:flex-row justify-between items-start md:items-end">
          {/* Avatar & Tên (Kéo avatar trồi lên trên bằng -mt-16) */}
          <div className="flex flex-col md:flex-row items-center md:items-end gap-4 -mt-16 relative z-10">
            <div className="w-32 h-32 rounded-full border-4 border-[var(--bg-surface)] bg-[#00B4D8] overflow-hidden flex items-center justify-center shrink-0">
              <img src={profile.avatarUrl} alt="Avatar" className="w-full h-full object-cover" />
            </div>
            <div className="text-center md:text-left pb-2">
              <h1 className="text-2xl font-bold text-[var(--ink-1)]">{profile.name}</h1>
              <p className="text-[var(--ink-2)] text-sm">{profile.title}</p>
            </div>
          </div>

          {/* Thống kê & Nút Share */}
          <div className="flex flex-col sm:flex-row items-center gap-6 mt-6 md:mt-0 w-full md:w-auto">
            <div className="flex gap-6 text-center">
              <div>
                <p className="text-xl font-bold text-[var(--ink-1)]">{profile.stats.documents}</p>
                <p className="text-xs font-semibold text-[var(--ink-3)] uppercase tracking-wider">Tài liệu</p>
              </div>
              <div>
                <p className="text-xl font-bold text-[#00B4D8]">{profile.stats.downloads}</p>
                <p className="text-xs font-semibold text-[var(--ink-3)] uppercase tracking-wider">Lượt tải</p>
              </div>
              <div>
                <p className="text-xl font-bold text-[var(--ink-1)]">{profile.stats.groups}</p>
                <p className="text-xs font-semibold text-[var(--ink-3)] uppercase tracking-wider">Nhóm học</p>
              </div>
            </div>
            
            <button className="flex items-center gap-2 px-5 py-2.5 bg-[#222222] hover:bg-black text-white text-sm font-semibold rounded-lg transition-colors w-full sm:w-auto justify-center">
              <LinkIcon size={16} /> Chia sẻ Profile
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};