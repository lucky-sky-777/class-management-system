import React from "react";
import { Edit2, User, Calendar, Phone, Briefcase, Film, Code, PenTool, Trophy, Link as LinkIcon } from "lucide-react";
import type { UserProfile } from "@/features/profile/types";

export const ProfileInfo: React.FC<{ profile: UserProfile }> = ({ profile }) => {
  // Map chữ hobby ra Icon tương ứng
  const getHobbyIcon = (hobby: string) => {
    switch (hobby) {
      case "Xem phim": return <Film size={14} />;
      case "Code": return <Code size={14} />;
      case "Thiết kế": return <PenTool size={14} />;
      case "Bóng đá": return <Trophy size={14} />;
      default: return <span className="w-1.5 h-1.5 bg-gray-400 rounded-full"></span>;
    }
  };

  return (
    <div className="bg-[var(--bg-surface)] rounded-2xl border border-[var(--rule)] p-5 shadow-sm h-fit">
      <div className="flex justify-between items-center mb-6">
        <h2 className="text-lg font-bold text-[var(--ink-1)]">Hồ sơ cá nhân</h2>
        <button className="text-[var(--ink-3)] hover:text-[var(--primary)] border border-[var(--rule)] p-1.5 rounded-md transition-colors">
          <Edit2 size={16} />
        </button>
      </div>

      <div className="flex flex-col gap-5 text-sm">
        {/* Thông tin cá nhân */}
        <div>
          <h3 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-3">Thông tin cá nhân</h3>
          <div className="flex flex-col gap-2.5 text-[var(--ink-2)]">
            <div className="flex items-center gap-3"><User size={16} /> {profile.personalInfo.gender}</div>
            <div className="flex items-center gap-3"><Calendar size={16} /> {profile.personalInfo.dob}</div>
            <div className="flex items-center gap-3"><Phone size={16} /> {profile.personalInfo.phone}</div>
          </div>
        </div>

        {/* Nghề nghiệp */}
        <div>
          <h3 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-3">Nghề nghiệp</h3>
          <div className="flex items-center gap-3 text-[var(--ink-2)]">
            <Briefcase size={16} /> {profile.profession}
          </div>
        </div>

        {/* Sở thích */}
        <div>
          <h3 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-3">Sở thích</h3>
          <div className="flex flex-col gap-2.5 text-[var(--ink-2)]">
            {profile.hobbies.map((hobby, index) => (
              <div key={index} className="flex items-center gap-3">
                {getHobbyIcon(hobby)} {hobby}
              </div>
            ))}
          </div>
        </div>

        {/* Liên kết */}
        <div>
          <h3 className="text-xs font-bold text-[var(--ink-3)] uppercase tracking-wider mb-3">Liên kết</h3>
          <div className="flex flex-col gap-2.5">
            {profile.links.map((link, index) => (
              <a key={index} href={link.url} target="_blank" rel="noreferrer" className="flex items-center gap-3 text-[var(--ink-2)] hover:text-[var(--primary)] transition-colors">
                <LinkIcon size={16} /> {link.label}
              </a>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};