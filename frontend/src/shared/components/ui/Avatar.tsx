import { useState, useMemo } from "react";

type AvatarSize = "xs" | "sm" | "md" | "lg" | "xl" | "2xl";
type AvatarShape = "circle" | "rounded";
type AvatarStatus = "online" | "offline" | "away" | "busy";

interface AvatarProps {
    /** Hiển thị ảnh từ URL */
    src?: string;
    /** Tên người dùng — dùng để tạo initials và màu nền khi không có ảnh */
    name?: string;
    /** Kích thước avatar */
    size?: AvatarSize;
    /** Hình dạng avatar */
    shape?: AvatarShape;
    /** Trạng thái hoạt động */
    status?: AvatarStatus;
    /** Alt text cho ảnh */
    alt?: string;
    /** Callback khi click */
    onClick?: () => void;
    /** Custom className */
    className?: string;
}

const SIZE_MAP: Record<AvatarSize, { container: string; text: string; status: string }> = {
    xs: { container: "w-6 h-6", text: "text-[10px]", status: "w-1.5 h-1.5 border" },
    sm: { container: "w-8 h-8", text: "text-xs", status: "w-2 h-2 border" },
    md: { container: "w-10 h-10", text: "text-sm", status: "w-2.5 h-2.5 border-[1.5px]" },
    lg: { container: "w-12 h-12", text: "text-base", status: "w-3 h-3 border-2" },
    xl: { container: "w-16 h-16", text: "text-xl", status: "w-3.5 h-3.5 border-2" },
    "2xl": { container: "w-20 h-20", text: "text-2xl", status: "w-4 h-4 border-2" },
};

const SHAPE_MAP: Record<AvatarShape, string> = {
    circle: "rounded-full",
    rounded: "rounded-xl",
};

const STATUS_COLOR: Record<AvatarStatus, string> = {
    online: "bg-emerald-400",
    offline: "bg-zinc-400",
    away: "bg-amber-400",
    busy: "bg-rose-500",
};

/** Tạo initials từ tên (tối đa 2 ký tự) */
function getInitials(name: string): string {
    const parts = name.trim().split(/\s+/);
    if (parts.length === 1) return parts[0].slice(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
}

/** Hash tên → index màu nhất quán */
function getColorIndex(name: string): number {
    let hash = 0;
    for (let i = 0; i < name.length; i++) {
        hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    return Math.abs(hash) % PALETTE.length;
}

const PALETTE = [
    "bg-violet-500 text-violet-50",
    "bg-sky-500 text-sky-50",
    "bg-teal-500 text-teal-50",
    "bg-emerald-500 text-emerald-50",
    "bg-amber-500 text-amber-50",
    "bg-rose-500 text-rose-50",
    "bg-pink-500 text-pink-50",
    "bg-indigo-500 text-indigo-50",
    "bg-orange-500 text-orange-50",
    "bg-cyan-500 text-cyan-50",
];

export function Avatar({
    src,
    name,
    size = "md",
    shape = "circle",
    status,
    alt,
    onClick,
    className = "",
}: AvatarProps) {
    const [imgError, setImgError] = useState(false);

    const initials = useMemo(() => (name ? getInitials(name) : "?"), [name]);
    const colorClass = useMemo(
        () => (name ? PALETTE[getColorIndex(name)] : "bg-zinc-300 text-zinc-600"),
        [name]
    );

    const sizes = SIZE_MAP[size];
    const shapeClass = SHAPE_MAP[shape];
    const isClickable = !!onClick;

    const showImage = src && !imgError;

    return (
        <span
            className={[
                "relative inline-flex shrink-0 select-none",
                sizes.container,
                shapeClass,
                isClickable ? "cursor-pointer" : "",
                className,
            ].join(" ")}
            onClick={onClick}
            role={isClickable ? "button" : undefined}
            tabIndex={isClickable ? 0 : undefined}
        >
            {showImage ? (
                <img
                    src={src}
                    alt={alt ?? name ?? "avatar"}
                    className={["w-full h-full object-cover", shapeClass].join(" ")}
                    onError={() => setImgError(true)}
                />
            ) : (
                <span
                    className={[
                        "w-full h-full flex items-center justify-center font-semibold",
                        shapeClass,
                        colorClass,
                        sizes.text,
                    ].join(" ")}
                    aria-label={name ?? "avatar"}
                >
                    {initials}
                </span>
            )}

            {status && (
                <span
                    className={[
                        "absolute bottom-0 right-0 rounded-full border-white",
                        sizes.status,
                        STATUS_COLOR[status],
                    ].join(" ")}
                    aria-label={status}
                />
            )}
        </span>
    );
}


// ─── AvatarGroup ───────────────────────────────────────────────────────────

interface AvatarGroupProps {
    avatars: Array<Pick<AvatarProps, "src" | "name" | "status">>;
    size?: AvatarSize;
    max?: number;
    shape?: AvatarShape;
}

export function AvatarGroup({ avatars, size = "md", max = 4, shape = "circle" }: AvatarGroupProps) {
    const visible = avatars.slice(0, max);
    const overflow = avatars.length - max;
    const sizes = SIZE_MAP[size];

    return (
        <div className="flex items-center">
            {visible.map((av, i) => (
                <div key={i} className="-ml-2 first:ml-0 ring-2 ring-white rounded-full">
                    <Avatar {...av} size={size} shape={shape} />
                </div>
            ))}
            {overflow > 0 && (
                <div
                    className={[
                        "-ml-2 ring-2 ring-white rounded-full flex items-center justify-center",
                        "bg-zinc-100 text-zinc-600 font-semibold",
                        sizes.container,
                        sizes.text,
                    ].join(" ")}
                >
                    +{overflow}
                </div>
            )}
        </div>
    );
}
