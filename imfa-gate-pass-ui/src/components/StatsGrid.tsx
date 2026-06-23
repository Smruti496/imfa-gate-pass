"use client";
import { useDashboard } from "@/hooks/useDashboard";

const TILES = [
  { key: "totalToday" as const, label: "Today's passes",    accentClass: "bg-[#2A2F36] text-alloy-100" },
  { key: "onsite"     as const, label: "Currently on-site", accentClass: "bg-ember-dim text-ember-500" },
  { key: "pending"    as const, label: "Awaiting arrival",  accentClass: "bg-steel-dim text-steel-400" },
  { key: "cleared"    as const, label: "Cleared today",     accentClass: "bg-slag-dim  text-slag-500"  },
];

export function StatsGrid() {
  const { stats, isLoading } = useDashboard();
  return (
    <section className="max-w-[1080px] mx-auto px-5 pb-4 grid grid-cols-2 md:grid-cols-4 gap-2.5">
      {TILES.map((t) => (
        <div key={t.key} className="bg-panel-800 border border-border-subtle rounded-[10px] p-3 flex items-center gap-2.5">
          <div className={`w-[34px] h-[34px] rounded-lg flex items-center justify-center flex-shrink-0 ${t.accentClass}`}>
            <span className="text-sm">◈</span>
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
