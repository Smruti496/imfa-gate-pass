"use client";
import { useDashboard } from "@/hooks/useDashboard";

interface SiteChartProps {
  onBarClick?: (locationId: string) => void;
}

export function SiteChart({ onBarClick }: SiteChartProps) {
  const { chartData, isLoading } = useDashboard();
  const max = Math.max(1, ...(chartData?.map((d) => d.count) ?? [0]));
  const data = chartData ?? Array(4).fill({ locationName: "…", count: 0 });
  return (
    <section className="w-full px-8 pb-4">
      <div className="bg-panel-800 border border-border-subtle rounded-[10px] p-4 shadow-card">
        <div className="font-display text-xs text-alloy-300 mb-2.5 uppercase tracking-[0.04em]">Entries by site — today</div>
        <div className="flex items-end gap-1 h-[100px]">
          {data.map((d: any, i: number) => {
            const h = d.count === 0 ? 4 : Math.max(8, Math.round((d.count / max) * 84));
            const clickable = onBarClick && !isLoading && d.locationId;
            return (
              <div
                key={i}
                onClick={() => clickable && onBarClick(d.locationId)}
                className={`flex-1 flex flex-col items-center gap-1.5 h-full justify-end rounded-[6px] transition-shadow ${
                  clickable ? "cursor-pointer hover:ring-1 hover:ring-ember-500/40" : ""
                }`}
              >
                <span className="text-[11px] text-alloy-300">{isLoading ? "" : d.count}</span>
                <div className="w-full max-w-[38px] bg-ember-500 rounded-t-[4px] transition-all duration-300" style={{ height: `${h}px` }} />
                <span className="text-[10px] text-alloy-300 text-center leading-[1.2]">
                  {d.locationName?.replace(" Plant","").replace(" Corporate Office"," HO")}
                </span>
              </div>
            );
          })}
        </div>
      </div>
    </section>
  );
}
