import { MemberItem } from "./MemberItem";
import type { Member, MemberRole } from "@features/member/types";

interface GroupSectionProps {
    title: string;
    data: Member[];
    borderColor: string;
    textColor: string;
    myRole: MemberRole | "OWNER";
    onUpdateRole?: (userId: number, currentRole: MemberRole) => void;
    onKick?: (userId: number) => void;
    onManagePermissions?: (member: Member) => void;
    isPendingSection?: boolean;
    onRefresh: (silent?: boolean) => void;
}

export const GroupSection = ({
    title,
    data,
    borderColor,
    textColor,
    myRole,
    onKick,
    onManagePermissions,
    isPendingSection,
    onRefresh,
}: GroupSectionProps) => (
    <div className="mb-10">
        <div className="flex justify-between items-center border-b-2 pb-2 mb-4" style={{ borderColor }}>
            <h3 className="text-sm font-black uppercase tracking-widest" style={{ color: textColor }}>
                {title}
            </h3>
            <span className="text-[10px] font-bold text-[var(--ink-3)] uppercase">
                {data.length} người
            </span>
        </div>
        <div className="space-y-1">
            {data.length > 0 ? (
                data.map((m) => (
                    <MemberItem
                        key={m.requestId || m.userId}
                        member={m}
                        myRole={myRole}
                        onKick={onKick}
                        onManagePermissions={onManagePermissions}
                        isPending={isPendingSection}
                        onRefresh={onRefresh}
                    />
                ))
            ) : (
                <p className="text-sm text-[var(--ink-4)] italic py-4 px-2">Danh sách trống</p>
            )}
        </div>
    </div>
);