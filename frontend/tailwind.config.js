/**
 * EduAdmin — tailwind.config.js
 * Design System: Modern Editorial × Humanist
 *
 * PHONG CÁCH: Học thuật chuyên nghiệp — xanh dương là màu chủ đạo.
 * Tông màu nhất quán với môi trường giáo dục:
 *   Blue    → primary action, navigation, trạng thái "đang học"
 *   Emerald → thành công, đạt, hoàn thành, điểm cao
 *   Amber   → cảnh báo, hạn chót, cần cải thiện
 *   Red     → nguy hiểm, không đạt, trượt, lỗi
 *   Violet  → xuất sắc, học bổng, đặc biệt
 *   Sky     → info, thông báo hệ thống, hướng dẫn
 *   Slate   → neutral UI, text, border, background
 *
 * DARK MODE: class strategy — thêm class "dark" lên <html>
 *
 * FONT STACK (phải import trong globals.css):
 *   Lora              → serif, display & heading
 *   Plus Jakarta Sans → sans, body & UI
 *   JetBrains Mono    → mono, code & data
 *
 * @type {import('tailwindcss').Config}
 */

module.exports = {
  content: [
    "./src/**/*.{js,ts,jsx,tsx,html}",
    "./app/**/*.{js,ts,jsx,tsx}",
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],

  darkMode: "class",

  theme: {
    screens: {
      xs: "480px",
      sm: "640px",
      md: "768px",
      lg: "1024px",
      xl: "1280px",
      "2xl": "1440px",
    },

    extend: {
      colors: {
        // ── Paper / Background ───────────────────────────────
        paper: {
          DEFAULT: "#F8FAFC", // nền trang chính — slate-50 (sáng, học thuật)
          dark: "#0F172A",    // dark mode — slate-900
        },

        // ── Surface (cards, panels) ──────────────────────────
        surface: {
          DEFAULT: "#FFFFFF",
          2: "#F1F5F9",        // hover row, thead bg — slate-100
          3: "#E2E8F0",        // sunken / disabled bg — slate-200
          "dark-1": "#1E293B", // card dark — slate-800
          "dark-2": "#263345", // hover row dark
          "dark-3": "#2D3F55", // sunken dark
        },

        // ── Ink / Text ───────────────────────────────────────
        ink: {
          1: "#0F172A", // primary text — slate-900 (lạnh, học thuật)
          2: "#475569", // secondary text — slate-600
          3: "#94A3B8", // tertiary / placeholder — slate-400
          4: "#CBD5E1", // disabled / decorative — slate-300
          d1: "#F1F5F9",
          d2: "#94A3B8",
          d3: "#475569",
          d4: "#1E293B",
        },

        // ── Rule / Border ────────────────────────────────────
        rule: {
          DEFAULT: "rgba(15,23,42,0.08)",
          md: "rgba(15,23,42,0.13)",
          dark: "rgba(241,245,249,0.07)",
          "dark-md": "rgba(241,245,249,0.12)",
        },

        // ── Primary Blue — màu chủ đạo EduAdmin ─────────────
        // Dùng cho: nút primary, active nav, link, focus ring
        primary: {
          50:     "#EFF6FF",
          100:    "#DBEAFE",
          200:    "#BFDBFE",
          300:    "#93C5FD",
          400:    "#60A5FA",
          500:    "#3B82F6",
          DEFAULT: "#2563EB", // Blue-600 ← accent chính
          600:    "#2563EB",
          700:    "#1D4ED8",
          800:    "#1E40AF",
          900:    "#1E3A8A",
          fill:   "#EFF6FF",
          text:   "#1E40AF",
          border: "#BFDBFE",
          "dark-fill":   "#1E293B",
          "dark-text":   "#60A5FA",
          "dark-border": "#1E3A8A",
        },

        // ── Emerald — thành công, đạt, hoàn thành ───────────
        // Dùng cho: điểm cao, nộp đúng hạn, trạng thái active
        success: {
          fill:   "#ECFDF5",
          text:   "#065F46",
          border: "#A7F3D0",
          DEFAULT: "#059669",
          "dark-fill":   "#022C22",
          "dark-text":   "#6EE7B7",
          "dark-border": "#065F46",
        },

        // ── Amber — cảnh báo, hạn chót, cần cải thiện ───────
        // Dùng cho: sắp hết hạn, điểm trung bình, pending
        warning: {
          fill:   "#FFFBEB",
          text:   "#92400E",
          border: "#FDE68A",
          DEFAULT: "#D97706",
          "dark-fill":   "#27180A",
          "dark-text":   "#FCD34D",
          "dark-border": "#92400E",
        },

        // ── Red — nguy hiểm, không đạt, lỗi ─────────────────
        // Dùng cho: trượt môn, quá hạn, xóa dữ liệu
        danger: {
          fill:   "#FEF2F2",
          text:   "#991B1B",
          border: "#FECACA",
          DEFAULT: "#DC2626",
          "dark-fill":   "#2A1010",
          "dark-text":   "#FCA5A5",
          "dark-border": "#7F1D1D",
        },

        // ── Violet — xuất sắc, học bổng, đặc biệt ───────────
        // Dùng cho: thành tích nổi bật, huy chương, admin role
        honor: {
          fill:   "#F5F3FF",
          text:   "#4C1D95",
          border: "#DDD6FE",
          DEFAULT: "#7C3AED",
          "dark-fill":   "#1C1035",
          "dark-text":   "#C4B5FD",
          "dark-border": "#4C1D95",
        },

        // ── Sky — info, thông báo hệ thống ───────────────────
        // Dùng cho: tooltip, hướng dẫn, thông báo trung tính
        info: {
          fill:   "#F0F9FF",
          text:   "#0C4A6E",
          border: "#BAE6FD",
          DEFAULT: "#0284C7",
          "dark-fill":   "#082F49",
          "dark-text":   "#7DD3FC",
          "dark-border": "#0C4A6E",
        },

        // ── Warm (alias cho primary — backward compat) ───────
        // Giữ lại để không break code cũ, trỏ sang blue
        warm: {
          50:    "#EFF6FF",
          100:   "#DBEAFE",
          200:   "#BFDBFE",
          400:   "#2563EB",
          600:   "#1D4ED8",
          800:   "#1E40AF",
          900:   "#1E3A8A",
          fill:   "#EFF6FF",
          text:   "#1E40AF",
          border: "#BFDBFE",
          "dark-fill":   "#1E293B",
          "dark-text":   "#60A5FA",
          "dark-border": "#1E3A8A",
        },

        // ── ink-blue / ink-green / ink-amber / ink-red / ink-sage
        // (backward compat — ánh xạ sang semantic mới)
        "ink-blue": {
          fill:   "#EFF6FF",
          text:   "#1E40AF",
          border: "#BFDBFE",
          "dark-fill":   "#1E293B",
          "dark-text":   "#60A5FA",
          "dark-border": "#1E3A8A",
        },
        "ink-green": {
          fill:   "#ECFDF5",
          text:   "#065F46",
          border: "#A7F3D0",
          "dark-fill":   "#022C22",
          "dark-text":   "#6EE7B7",
          "dark-border": "#065F46",
        },
        "ink-amber": {
          fill:   "#FFFBEB",
          text:   "#92400E",
          border: "#FDE68A",
          "dark-fill":   "#27180A",
          "dark-text":   "#FCD34D",
          "dark-border": "#92400E",
        },
        "ink-red": {
          fill:   "#FEF2F2",
          text:   "#991B1B",
          border: "#FECACA",
          "dark-fill":   "#2A1010",
          "dark-text":   "#FCA5A5",
          "dark-border": "#7F1D1D",
        },
        "ink-sage": {
          fill:   "#F5F3FF",
          text:   "#4C1D95",
          border: "#DDD6FE",
          "dark-fill":   "#1C1035",
          "dark-text":   "#C4B5FD",
          "dark-border": "#4C1D95",
        },

        // ── Sidebar ───────────────────────────────────────────
        sidebar: {
          bg:           "#F8FAFC", // slate-50
          text:         "#475569", // slate-600
          "text-active":"#1D4ED8", // blue-700
          active:       "#E0EAFB", // blue-50 tinted
          border:       "#CBD5E1", // slate-300
          accent:       "#2563EB", // blue-600
          "dark-bg":    "#0F172A",
          "dark-text":  "#64748B",
          "dark-active":"#1E293B",
          "dark-accent":"#60A5FA",
        },
      },

      // ───────────────────────────────────────────────────────
      // TYPOGRAPHY
      // ───────────────────────────────────────────────────────
      fontFamily: {
        // Thay thế font serif (có chân) bằng font sans (không chân)
        sans:    ["'Plus Jakarta Sans'", "'Helvetica Neue'", "sans-serif"],
        serif:   ["'Plus Jakarta Sans'", "'Helvetica Neue'", "sans-serif"], // Đổi dòng này thành sans
        mono:    ["'JetBrains Mono'", "'Fira Code'", "monospace"],
        display: ["'Plus Jakarta Sans'", "'Helvetica Neue'", "sans-serif"], // Đổi dòng này thành sans
      },

      fontSize: {
        "2xs":       ["0.65rem", { lineHeight: "1rem" }],
        xs:          ["0.75rem", { lineHeight: "1.125rem" }],
        sm:          ["0.875rem", { lineHeight: "1.25rem" }],
        base:        ["1rem", { lineHeight: "1.5rem" }],
        md:          ["1.0625rem", { lineHeight: "1.6rem" }],
        lg:          ["1.125rem", { lineHeight: "1.75rem" }],
        xl:          ["1.25rem", { lineHeight: "1.75rem" }],
        "2xl":       ["1.5rem", { lineHeight: "2rem" }],
        "3xl":       ["1.875rem", { lineHeight: "2.25rem" }],
        "4xl":       ["2.25rem", { lineHeight: "2.5rem" }],
        "5xl":       ["3rem", { lineHeight: "1" }],
        "display-sm":["2.25rem", { lineHeight: "2.5rem", letterSpacing: "-0.03em" }],
        "display-md":["3rem", { lineHeight: "1", letterSpacing: "-0.035em" }],
        "display-lg":["3.75rem", { lineHeight: "1", letterSpacing: "-0.04em" }],
      },

      fontWeight: {
        light:    "300",
        regular:  "400",
        medium:   "500",
        semibold: "600",
        bold:     "700",
      },

      lineHeight: {
        tightest: "1.1",
        tight:    "1.25",
        snug:     "1.4",
        normal:   "1.6",
        relaxed:  "1.65",
        loose:    "1.8",
      },

      letterSpacing: {
        tightest: "-0.04em",
        tighter:  "-0.03em",
        tight:    "-0.02em",
        snug:     "-0.01em",
        normal:   "0em",
        wide:     "0.03em",
        wider:    "0.07em",
        widest:   "0.1em",
        label:    "0.1em",
      },

      // ───────────────────────────────────────────────────────
      // SPACING
      // ───────────────────────────────────────────────────────
      spacing: {
        px:   "1px",
        0:    "0px",
        0.5:  "2px",
        1:    "4px",
        1.5:  "6px",
        2:    "8px",
        2.5:  "10px",
        3:    "12px",
        3.5:  "14px",
        4:    "16px",
        5:    "20px",
        6:    "24px",
        7:    "28px",
        8:    "32px",
        9:    "36px",
        10:   "40px",
        11:   "44px",
        12:   "48px",
        14:   "56px",
        16:   "64px",
        18:   "72px",
        20:   "80px",
        24:   "96px",
        28:   "112px",
        32:   "128px",
        "sidebar-w": "208px",
        "topbar-h":  "52px",
        "panel-px":  "16px",
        "panel-py":  "14px",
        "card-gap":  "10px",
      },

      // ───────────────────────────────────────────────────────
      // BORDER RADIUS
      // ───────────────────────────────────────────────────────
      borderRadius: {
        none:    "0px",
        xs:      "2px",
        sm:      "4px",
        DEFAULT: "8px",
        md:      "8px",
        lg:      "10px",
        xl:      "14px",
        "2xl":   "20px",
        "3xl":   "28px",
        full:    "9999px",
      },

      // ───────────────────────────────────────────────────────
      // BOX SHADOW
      // ───────────────────────────────────────────────────────
      boxShadow: {
        none: "none",
        xs:   "0 1px 2px rgba(15,23,42,0.06), 0 0 0 1px rgba(15,23,42,0.04)",
        sm:   "0 1px 4px rgba(15,23,42,0.08), 0 0 0 1px rgba(15,23,42,0.04)",
        md:   "0 4px 12px rgba(15,23,42,0.10), 0 1px 3px rgba(15,23,42,0.06)",
        lg:   "0 8px 24px rgba(15,23,42,0.12), 0 2px 6px rgba(15,23,42,0.06)",
        xl:   "0 16px 40px rgba(15,23,42,0.14), 0 4px 10px rgba(15,23,42,0.07)",
        "focus-blue": "0 0 0 3px rgba(37,99,235,0.20)",
        "focus-warm": "0 0 0 3px rgba(37,99,235,0.20)",
        "focus-red":  "0 0 0 3px rgba(220,38,38,0.18)",
        "dark-xs": "0 1px 2px rgba(0,0,0,0.35), 0 0 0 1px rgba(241,245,249,0.04)",
        "dark-sm": "0 1px 4px rgba(0,0,0,0.45), 0 0 0 1px rgba(241,245,249,0.04)",
        "dark-md": "0 4px 12px rgba(0,0,0,0.55), 0 1px 3px rgba(0,0,0,0.30)",
        "dark-lg": "0 8px 24px rgba(0,0,0,0.65), 0 2px 6px rgba(0,0,0,0.35)",
      },

      // ───────────────────────────────────────────────────────
      // TRANSITION
      // ───────────────────────────────────────────────────────
      transitionDuration: {
        75:   "75ms",
        100:  "100ms",
        fast: "120ms",
        base: "200ms",
        slow: "300ms",
        lazy: "400ms",
      },

      transitionTimingFunction: {
        smooth: "cubic-bezier(0.4, 0, 0.2, 1)",
        bounce: "cubic-bezier(0.34, 1.56, 0.64, 1)",
        in:     "cubic-bezier(0.4, 0, 1, 1)",
        out:    "cubic-bezier(0, 0, 0.2, 1)",
      },

      // ───────────────────────────────────────────────────────
      // Z-INDEX
      // ───────────────────────────────────────────────────────
      zIndex: {
        base:     "0",
        raised:   "10",
        dropdown: "200",
        sticky:   "300",
        overlay:  "400",
        modal:    "500",
        toast:    "600",
        tooltip:  "700",
      },

      // ───────────────────────────────────────────────────────
      // ANIMATIONS
      // ───────────────────────────────────────────────────────
      keyframes: {
        "fade-up": {
          from: { opacity: "0", transform: "translateY(8px)" },
          to:   { opacity: "1", transform: "translateY(0)" },
        },
        "fade-in": {
          from: { opacity: "0" },
          to:   { opacity: "1" },
        },
        "scale-in": {
          from: { opacity: "0", transform: "scale(0.96)" },
          to:   { opacity: "1", transform: "scale(1)" },
        },
        "slide-right": {
          from: { opacity: "0", transform: "translateX(-12px)" },
          to:   { opacity: "1", transform: "translateX(0)" },
        },
        shimmer: {
          from: { backgroundPosition: "-200% 0" },
          to:   { backgroundPosition: "200% 0" },
        },
        "pulse-dot": {
          "0%, 100%": { opacity: "1" },
          "50%":       { opacity: "0.35" },
        },
      },

      animation: {
        "fade-up":    "fade-up 0.2s cubic-bezier(0.4,0,0.2,1) forwards",
        "fade-in":    "fade-in 0.15s ease forwards",
        "scale-in":   "scale-in 0.15s cubic-bezier(0.34,1.56,0.64,1) forwards",
        "slide-right":"slide-right 0.2s ease forwards",
        shimmer:      "shimmer 1.8s linear infinite",
        "pulse-dot":  "pulse-dot 2s ease-in-out infinite",
      },

      backgroundImage: {
        "shimmer-light": "linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.6) 50%, transparent 100%)",
        "shimmer-dark":  "linear-gradient(90deg, transparent 0%, rgba(255,255,255,0.06) 50%, transparent 100%)",
      },
    },
  },

  // ─────────────────────────────────────────────────────────
  // PLUGINS
  // ─────────────────────────────────────────────────────────
  plugins: [
    function ({ addBase, addComponents, addUtilities, theme }) {

      addBase({
        "*, *::before, *::after": { boxSizing: "border-box" },
        html: { scrollBehavior: "smooth" },
        "::selection": {
          background: theme("colors.primary.fill"),
          color:      theme("colors.primary.text"),
        },
        ":focus-visible": {
          outline:      `2px solid ${theme("colors.primary.DEFAULT")}`,
          outlineOffset:"2px",
          borderRadius: theme("borderRadius.xs"),
        },
        "::-webkit-scrollbar":       { width: "5px", height: "5px" },
        "::-webkit-scrollbar-track": { background: theme("colors.surface.2") },
        "::-webkit-scrollbar-thumb": {
          background:    theme("colors.ink.4"),
          borderRadius:  theme("borderRadius.full"),
        },
      });

      addComponents({
        // BUTTON BASE
        ".btn": {
          display:       "inline-flex",
          alignItems:    "center",
          justifyContent:"center",
          gap:           "6px",
          fontFamily:    "var(--font-sans)",
          fontSize:      theme("fontSize.base[0]"),
          fontWeight:    theme("fontWeight.medium"),
          letterSpacing: "0.01em",
          lineHeight:    "1",
          padding:       "8px 16px",
          borderRadius:  theme("borderRadius.DEFAULT"),
          border:        "1px solid transparent",
          cursor:        "pointer",
          transition:    "all 120ms ease",
          userSelect:    "none",
          whiteSpace:    "nowrap",
        },
        ".btn-primary": {
          background:  theme("colors.primary.DEFAULT"),
          color:       "#fff",
          borderColor: theme("colors.primary.DEFAULT"),
          "&:hover":  { background: theme("colors.primary.700") },
          "&:active": { transform: "scale(0.98)" },
        },
        ".btn-secondary": {
          background:  theme("colors.surface.DEFAULT"),
          color:       theme("colors.ink.1"),
          borderColor: theme("colors.rule.DEFAULT"),
          "&:hover":  { background: theme("colors.surface.2") },
          "&:active": { transform: "scale(0.98)" },
        },
        ".btn-ghost": {
          background: "transparent",
          color:      theme("colors.ink.2"),
          "&:hover":  {
            background: theme("colors.surface.3"),
            color:      theme("colors.ink.1"),
          },
          "&:active": { transform: "scale(0.98)" },
        },
        ".btn-warm": {
          background:  theme("colors.primary.DEFAULT"),
          color:       "#fff",
          borderColor: theme("colors.primary.DEFAULT"),
          "&:hover":  { background: theme("colors.primary.700") },
          "&:active": { transform: "scale(0.98)" },
        },
        ".btn-danger": {
          background:  theme("colors.danger.fill"),
          color:       theme("colors.danger.text"),
          borderColor: theme("colors.danger.border"),
          "&:hover":  { opacity: "0.85" },
          "&:active": { transform: "scale(0.98)" },
        },
        ".btn-sm": {
          padding:      "5px 12px",
          fontSize:     theme("fontSize.xs[0]"),
          borderRadius: theme("borderRadius.sm"),
        },
        ".btn-lg": {
          padding:      "11px 22px",
          fontSize:     theme("fontSize.lg[0]"),
          borderRadius: theme("borderRadius.lg"),
        },
        ".btn-icon": { padding: "8px" },

        // PILL / BADGE
        ".pill": {
          display:       "inline-flex",
          alignItems:    "center",
          gap:           "4px",
          padding:       "3px 9px",
          borderRadius:  theme("borderRadius.full"),
          fontSize:      theme("fontSize.xs[0]"),
          fontWeight:    theme("fontWeight.semibold"),
          letterSpacing: theme("letterSpacing.wide"),
          fontFamily:    "var(--font-sans)",
          border:        "1px solid transparent",
        },
        ".pill-dot": {
          width:        "5px",
          height:       "5px",
          borderRadius: theme("borderRadius.full"),
          background:   "currentColor",
          flexShrink:   "0",
        },
        // pill-blue → đang học / primary state
        ".pill-blue": {
          background:  theme("colors.primary.fill"),
          color:       theme("colors.primary.text"),
          borderColor: theme("colors.primary.border"),
        },
        // pill-green → đạt / thành công
        ".pill-green": {
          background:  theme("colors.success.fill"),
          color:       theme("colors.success.text"),
          borderColor: theme("colors.success.border"),
        },
        // pill-amber → cảnh báo / hạn chót
        ".pill-amber": {
          background:  theme("colors.warning.fill"),
          color:       theme("colors.warning.text"),
          borderColor: theme("colors.warning.border"),
        },
        // pill-red → không đạt / nguy hiểm
        ".pill-red": {
          background:  theme("colors.danger.fill"),
          color:       theme("colors.danger.text"),
          borderColor: theme("colors.danger.border"),
        },
        // pill-sage → xuất sắc (dùng violet)
        ".pill-sage": {
          background:  theme("colors.honor.fill"),
          color:       theme("colors.honor.text"),
          borderColor: theme("colors.honor.border"),
        },
        // pill-warm → alias cho pill-blue (backward compat)
        ".pill-warm": {
          background:  theme("colors.primary.fill"),
          color:       theme("colors.primary.text"),
          borderColor: theme("colors.primary.border"),
        },
        // pill-violet → xuất sắc / học bổng
        ".pill-violet": {
          background:  theme("colors.honor.fill"),
          color:       theme("colors.honor.text"),
          borderColor: theme("colors.honor.border"),
        },
        // pill-info → thông báo hệ thống
        ".pill-info": {
          background:  theme("colors.info.fill"),
          color:       theme("colors.info.text"),
          borderColor: theme("colors.info.border"),
        },

        // INPUT
        ".input-field": {
          display:    "flex",
          alignItems: "center",
          gap:        "8px",
          background: theme("colors.surface.DEFAULT"),
          border:     `1px solid ${theme("colors.rule.md")}`,
          borderRadius: theme("borderRadius.DEFAULT"),
          padding:    "0 12px",
          transition: "border 120ms ease, box-shadow 120ms ease",
          "&:focus-within": {
            borderColor: theme("colors.primary.DEFAULT"),
            boxShadow:   theme("boxShadow.focus-blue"),
          },
        },
        ".input-field input, .input-field textarea, .input-field select": {
          width:      "100%",
          minWidth:   "0",
          border:     "none",
          outline:    "none",
          background: "transparent",
          fontFamily: "var(--font-sans)",
          fontSize:   theme("fontSize.base[0]"),
          color:      theme("colors.ink.1"),
          padding:    "9px 0",
          "&::placeholder": { color: theme("colors.ink.4") },
        },
        '.input-field input[type="datetime-local"]': {
          paddingRight: "12px",
        },
        ".input-error .input-field": {
          borderColor: theme("colors.danger.DEFAULT"),
          "&:focus-within": { boxShadow: theme("boxShadow.focus-red") },
        },
        ".input-label": {
          fontSize:      theme("fontSize.2xs[0]"),
          fontWeight:    theme("fontWeight.semibold"),
          letterSpacing: theme("letterSpacing.label"),
          textTransform: "uppercase",
          color:         theme("colors.ink.3"),
        },
        ".input-helper": {
          fontSize: theme("fontSize.xs[0]"),
          color:    theme("colors.ink.3"),
        },
        ".input-error-msg": {
          fontSize: theme("fontSize.xs[0]"),
          color:    theme("colors.danger.text"),
        },

        // STAT CARD
        ".stat-card": {
          background:   theme("colors.surface.DEFAULT"),
          border:       `1px solid ${theme("colors.rule.DEFAULT")}`,
          borderRadius: theme("borderRadius.lg"),
          padding:      "16px 16px 14px",
        },
        ".stat-label": {
          fontSize:      theme("fontSize.2xs[0]"),
          fontWeight:    theme("fontWeight.semibold"),
          letterSpacing: theme("letterSpacing.label"),
          textTransform: "uppercase",
          color:         theme("colors.ink.3"),
          marginBottom:  "10px",
        },
        ".stat-value": {
          fontFamily:    "var(--font-serif)",
          fontSize:      "28px",
          fontWeight:    theme("fontWeight.semibold"),
          color:         theme("colors.ink.1"),
          letterSpacing: theme("letterSpacing.tightest"),
          lineHeight:    "1",
          marginBottom:  "8px",
        },
        ".stat-unit": {
          fontSize:   "14px",
          opacity:    "0.45",
          fontFamily: "var(--font-sans)",
          fontWeight: theme("fontWeight.regular"),
        },

        // CARD / PANEL
        ".card": {
          background:   theme("colors.surface.DEFAULT"),
          border:       `1px solid ${theme("colors.rule.DEFAULT")}`,
          borderRadius: theme("borderRadius.lg"),
          overflow:     "hidden",
        },
        ".card-header": {
          display:        "flex",
          alignItems:     "flex-start",
          justifyContent: "space-between",
          padding:        "14px 16px 12px",
          borderBottom:   `1px solid ${theme("colors.rule.DEFAULT")}`,
        },
        ".card-title": {
          fontFamily:    "var(--font-serif)",
          fontSize:      theme("fontSize.base[0]"),
          fontWeight:    theme("fontWeight.semibold"),
          color:         theme("colors.ink.1"),
          letterSpacing: theme("letterSpacing.tight"),
        },
        ".card-subtitle": {
          fontSize:    theme("fontSize.xs[0]"),
          color:       theme("colors.ink.3"),
          marginTop:   "2px",
        },
        ".card-body":   { padding: "16px" },
        ".card-footer": {
          display:        "flex",
          alignItems:     "center",
          justifyContent: "flex-end",
          gap:            "8px",
          padding:        "12px 16px",
          borderTop:      `1px solid ${theme("colors.rule.DEFAULT")}`,
          background:     theme("colors.surface.2"),
        },

        // DATA TABLE
        ".data-table": { width: "100%", borderCollapse: "collapse" },
        ".data-table thead th": {
          fontSize:      theme("fontSize.2xs[0]"),
          fontWeight:    theme("fontWeight.semibold"),
          letterSpacing: theme("letterSpacing.label"),
          textTransform: "uppercase",
          color:         theme("colors.ink.3"),
          padding:       "9px 16px",
          textAlign:     "left",
          background:    theme("colors.surface.2"),
          borderBottom:  `1px solid ${theme("colors.rule.DEFAULT")}`,
        },
        ".data-table tbody tr": {
          borderBottom: `1px solid ${theme("colors.rule.DEFAULT")}`,
          transition:   "background 100ms ease",
        },
        ".data-table tbody tr:last-child": { borderBottom: "none" },
        ".data-table tbody tr:hover td": {
          background: theme("colors.surface.2"),
        },
        ".data-table tbody td": {
          padding:       "9px 16px",
          fontSize:      theme("fontSize.sm[0]"),
          color:         theme("colors.ink.2"),
          verticalAlign: "middle",
        },
        ".data-table tbody td:first-child": {
          color:      theme("colors.ink.1"),
          fontWeight: theme("fontWeight.medium"),
        },

        // SIDEBAR
        ".sidebar": {
          width:       "208px",
          background:  theme("colors.sidebar.bg"),
          display:     "flex",
          flexDirection:"column",
          flexShrink:  "0",
          borderRight: `1px solid ${theme("colors.sidebar.border")}`,
        },
        ".sidebar-item": {
          display:      "flex",
          alignItems:   "center",
          gap:          "9px",
          padding:      "8px 10px",
          borderRadius: theme("borderRadius.DEFAULT"),
          cursor:       "pointer",
          color:        theme("colors.sidebar.text"),
          fontSize:     theme("fontSize.sm[0]"),
          fontWeight:   theme("fontWeight.regular"),
          marginBottom: "1px",
          transition:   "all 150ms ease",
          "&:hover": {
            background: theme("colors.sidebar.active"),
            color:      theme("colors.sidebar.text-active"),
          },
        },
        ".sidebar-item.active": {
          background:             theme("colors.sidebar.active"),
          color:                  theme("colors.sidebar.text-active"),
          fontWeight:             theme("fontWeight.medium"),
          borderLeft:             `3px solid ${theme("colors.sidebar.accent")}`,
          borderTopLeftRadius:    "0",
          borderBottomLeftRadius: "0",
        },

        // TOGGLE SWITCH
        ".toggle-track": {
          width:        "38px",
          height:       "20px",
          borderRadius: theme("borderRadius.full"),
          background:   theme("colors.ink.4"),
          position:     "relative",
          cursor:       "pointer",
          flexShrink:   "0",
          transition:   "background 200ms ease",
          "&.on":       { background: theme("colors.primary.DEFAULT") },
        },
        ".toggle-thumb": {
          position:     "absolute",
          top:          "2px",
          left:         "2px",
          width:        "16px",
          height:       "16px",
          borderRadius: theme("borderRadius.full"),
          background:   "#fff",
          boxShadow:    "0 1px 3px rgba(0,0,0,0.2)",
          transition:   "left 200ms cubic-bezier(0.34,1.56,0.64,1)",
          ".on &":      { left: "20px" },
        },

        // SKELETON LOADING
        ".skeleton": {
          backgroundSize:  "200% 100%",
          animation:       "shimmer 1.8s linear infinite",
          borderRadius:    theme("borderRadius.DEFAULT"),
          backgroundImage: "linear-gradient(90deg, #F1F5F9 0%, #E2E8F0 50%, #F1F5F9 100%)",
        },

        // DIVIDER
        ".divider": {
          height:     "1px",
          background: "rgba(15,23,42,0.08)",
          border:     "none",
          margin:     "0",
        },

        // EDITORIAL PULL QUOTE
        ".pull-quote": {
          fontFamily:   "var(--font-serif)",
          fontStyle:    "italic",
          fontSize:     theme("fontSize.md[0]"),
          color:        theme("colors.ink.2"),
          lineHeight:   theme("lineHeight.relaxed"),
          borderLeft:   `2px solid ${theme("colors.primary.DEFAULT")}`,
          paddingLeft:  "16px",
          background:   theme("colors.surface.2"),
          borderRadius: "0 8px 8px 0",
          padding:      "14px 16px",
        },

        // GRADE BADGE — đặc trưng cho giáo dục
        ".grade-badge": {
          display:       "inline-flex",
          alignItems:    "center",
          justifyContent:"center",
          width:         "32px",
          height:        "32px",
          borderRadius:  theme("borderRadius.DEFAULT"),
          fontFamily:    "var(--font-mono)",
          fontSize:      theme("fontSize.sm[0]"),
          fontWeight:    theme("fontWeight.bold"),
          letterSpacing: theme("letterSpacing.tight"),
        },
        ".grade-a":  { background: "#ECFDF5", color: "#065F46" },
        ".grade-b":  { background: "#EFF6FF", color: "#1E40AF" },
        ".grade-c":  { background: "#FFFBEB", color: "#92400E" },
        ".grade-d":  { background: "#FEF2F2", color: "#991B1B" },
        ".grade-f":  { background: "#FEF2F2", color: "#7F1D1D", fontWeight: "700" },
        ".grade-xa": { background: "#F5F3FF", color: "#4C1D95" }, // xuất sắc
      });

      addUtilities({
        ".bg-paper":    { background: "var(--bg-paper, #F8FAFC)" },
        ".bg-surface":  { background: "var(--bg-surface, #FFFFFF)" },
        ".bg-surface-2":{ background: "var(--bg-surface-2, #F1F5F9)" },
        ".bg-surface-3":{ background: "var(--bg-surface-3, #E2E8F0)" },

        ".text-ink-1": { color: "var(--ink-1, #0F172A)" },
        ".text-ink-2": { color: "var(--ink-2, #475569)" },
        ".text-ink-3": { color: "var(--ink-3, #94A3B8)" },

        ".border-rule":    { borderColor: "rgba(15,23,42,0.08)" },
        ".border-rule-md": { borderColor: "rgba(15,23,42,0.13)" },

        ".text-display": {
          fontFamily:    "var(--font-serif, 'Lora', serif)",
          fontWeight:    "600",
          letterSpacing: "-0.03em",
          lineHeight:    "1.2",
        },
        ".text-label": {
          fontSize:      "0.6rem",
          fontWeight:    "600",
          letterSpacing: "0.1em",
          textTransform: "uppercase",
          color:         "var(--ink-3, #94A3B8)",
        },
        ".text-mono": {
          fontFamily: "var(--font-mono, 'JetBrains Mono', monospace)",
          fontSize:   "0.75rem",
        },

        ".scrollbar-thin": {
          "scrollbar-width": "thin",
          "&::-webkit-scrollbar":       { width: "5px", height: "5px" },
          "&::-webkit-scrollbar-thumb": {
            background:    "#CBD5E1",
            borderRadius:  "9999px",
          },
        },
        ".scrollbar-none": {
          "scrollbar-width": "none",
          "&::-webkit-scrollbar": { display: "none" },
        },

        ".transition-fast": { transition: "all 120ms ease" },
        ".transition-base": { transition: "all 200ms ease" },
        ".transition-slow": { transition: "all 300ms cubic-bezier(0.4,0,0.2,1)" },

        ".safe-bottom": { paddingBottom: "env(safe-area-inset-bottom)" },
        ".safe-top":    { paddingTop:    "env(safe-area-inset-top)" },
      });
    },
  ],
};