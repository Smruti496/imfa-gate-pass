"use client";
import type { AnalyticsData } from "@/lib/types";
import { StatusPieChart } from "./StatusPieChart";
import { GenderPieChart } from "./GenderPieChart";
import { LocationStackedBar } from "./LocationStackedBar";
import { VisitorTrendCharts } from "./VisitorTrendCharts";

interface Props {
  analytics: AnalyticsData | undefined;
  isLoading: boolean;
  startDate: string;
  endDate: string;
  onStartDate: (v: string) => void;
  onEndDate: (v: string) => void;
}

export function AnalyticsSection({ analytics, isLoading, startDate, endDate, onStartDate, onEndDate }: Props) {
  return (
    <section className="w-full px-8 pb-2">
      <div className="flex items-center justify-between mb-3">
        <h2 className="font-display text-[15px] font-semibold tracking-[0.06em] text-alloy-300 uppercase">
          Analytics &amp; Insights
        </h2>
        <div className="flex items-center gap-2 text-sm text-alloy-400">
          <input
            type="date"
            value={startDate}
            max={endDate}
            onChange={(e) => onStartDate(e.target.value)}
            className="bg-panel-700 border border-panel-600 rounded px-2 py-1 text-alloy-200 text-xs"
          />
          <span className="text-alloy-500">→</span>
          <input
            type="date"
            value={endDate}
            min={startDate}
            onChange={(e) => onEndDate(e.target.value)}
            className="bg-panel-700 border border-panel-600 rounded px-2 py-1 text-alloy-200 text-xs"
          />
        </div>
      </div>
      <div className="flex flex-col gap-4">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <StatusPieChart data={analytics?.statusDistribution} isLoading={isLoading} />
          <GenderPieChart data={analytics?.genderDistribution} isLoading={isLoading} />
        </div>
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <LocationStackedBar data={analytics?.locationStatusMatrix} isLoading={isLoading} />
          <VisitorTrendCharts data={analytics} isLoading={isLoading} />
        </div>
      </div>
    </section>
  );
}
