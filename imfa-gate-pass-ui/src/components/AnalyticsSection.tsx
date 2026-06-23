"use client";
import type { AnalyticsData } from "@/lib/types";
import { StatusPieChart } from "./StatusPieChart";
import { GenderPieChart } from "./GenderPieChart";
import { LocationStackedBar } from "./LocationStackedBar";
import { VisitorTrendCharts } from "./VisitorTrendCharts";

interface Props { analytics: AnalyticsData | undefined; isLoading: boolean }

export function AnalyticsSection({ analytics, isLoading }: Props) {
  return (
    <section className="w-full px-8 pb-2">
      <h2 className="font-display text-[15px] font-semibold tracking-[0.06em] text-alloy-300 uppercase mb-3">
        Analytics &amp; Insights
      </h2>
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
