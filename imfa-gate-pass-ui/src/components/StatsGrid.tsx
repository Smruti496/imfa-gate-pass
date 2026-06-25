"use client";
import { useDashboard } from "@/hooks/useDashboard";

type TileKey = "totalToday" | "onsite" | "pending" | "cleared";

const TILES: { key: TileKey; label: string; accentClass: string; icon: React.ReactNode }[] = [
  {
    key: "totalToday", label: "Today's passes", accentClass: "bg-panel-700 text-alloy-300",
    icon: <><circle cx="9" cy="8" r="3.2"/><path d="M2.5 20c0-3.3 3-5.5 6.5-5.5s6.5 2.2 6.5 5.5"/><circle cx="17" cy="9" r="2.6"/><path d="M15.5 14.2c2.6.4 4.5 2.2 4.5 4.8"/></>,
  },
  {
    key: "onsite", label: "Currently on-site", accentClass: "bg-ember-dim text-ember-500",
    icon: <><path d="M14 4h4a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1h-4"/><polyline points="9 8 13 12 9 16"/><line x1="13" y1="12" x2="3" y2="12"/></>,
  },
  {
    key: "pending", label: "Awaiting arrival", accentClass: "bg-steel-dim text-steel-400",
    icon: <><circle cx="12" cy="12" r="9"/><polyline points="12 7 12 12 15.5 14"/></>,
  },
  {
    key: "cleared", label: "Cleared today", accentClass: "bg-slag-dim text-slag-500",
    icon: <><path d="M12 3 4 6v6c0 5 3.5 8 8 9 4.5-1 8-4 8-9V6Z"/><polyline points="9 12 11.5 14.5 15.5 9.5"/></>,
  },
];

interface StatsGridProps {
  onCardClick?: (key: TileKey) => void;
}

export function StatsGrid({ onCardClick }: StatsGridProps) {
  const { stats, isLoading } = useDashboard();
  return (
    <section className="w-full px-8 pb-4 grid grid-cols-2 md:grid-cols-4 gap-2.5">
      {TILES.map((t) => (
        <div
          key={t.key}
          onClick={() => onCardClick?.(t.key)}
          className={`bg-panel-800 border border-border-subtle rounded-[10px] p-3 flex items-center gap-2.5 shadow-card transition-shadow ${
            onCardClick ? "cursor-pointer hover:ring-1 hover:ring-ember-500/40 hover:shadow-md" : ""
          }`}
        >
          <div className={`w-[34px] h-[34px] rounded-lg flex items-center justify-center flex-shrink-0 ${t.accentClass}`}>
            <svg viewBox="0 0 24 24" width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">{t.icon}</svg>
          </div>
          <div>
            <div className="font-display text-[22px] font-semibold leading-none">{isLoading ? "–" : (stats?.[t.key] ?? 0)}</div>
            <div className="text-[11.5px] text-alloy-300 mt-0.5">{t.label}</div>
          </div>
        </div>
      ))}
    </section>
  );
}
