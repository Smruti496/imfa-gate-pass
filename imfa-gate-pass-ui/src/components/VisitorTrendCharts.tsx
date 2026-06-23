"use client";
import { useState } from "react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from "recharts";
import type { AnalyticsData } from "@/lib/types";

type Tab = "hour" | "month" | "quarter" | "year";

const TABS: { key: Tab; label: string }[] = [
  { key: "hour",    label: "Hour" },
  { key: "month",   label: "Month" },
  { key: "quarter", label: "Quarter" },
  { key: "year",    label: "Year" },
];

interface Props { data: AnalyticsData | undefined; isLoading: boolean }

export function VisitorTrendCharts({ data, isLoading }: Props) {
  const [tab, setTab] = useState<Tab>("month");

  const trendData = {
    hour:    (data?.hourlyTrend    ?? []).map((p) => ({ label: p.label, count: p.count })),
    month:   (data?.monthlyTrend   ?? []).map((p) => ({ label: p.label, count: p.count })),
    quarter: (data?.quarterlyTrend ?? []).map((p) => ({ label: p.label, count: p.count })),
    year:    (data?.yearlyTrend    ?? []).map((p) => ({ label: p.label, count: p.count })),
  };

  return (
    <div className="bg-panel-800 border border-border-subtle rounded-[10px] p-4 flex flex-col gap-3">
      <div className="flex items-center justify-between flex-wrap gap-2">
        <h3 className="font-display text-[13px] font-semibold tracking-[0.04em] text-alloy-300 uppercase">Visitor Trends</h3>
        <div className="flex gap-1">
          {TABS.map((t) => (
            <button key={t.key} onClick={() => setTab(t.key)}
              className={`px-3 py-1 rounded-full text-[11px] font-medium transition-colors ${
                tab === t.key
                  ? "bg-ember-500 text-white"
                  : "bg-panel-700 text-alloy-300 hover:text-alloy-100"
              }`}>
              {t.label}
            </button>
          ))}
        </div>
      </div>
      {isLoading ? (
        <div className="h-52 animate-pulse bg-panel-700 rounded-lg" />
      ) : trendData[tab].length === 0 ? (
        <div className="h-52 flex items-center justify-center text-alloy-300 text-sm">No data</div>
      ) : (
        <ResponsiveContainer width="100%" height={220}>
          <LineChart data={trendData[tab]} margin={{ top: 4, right: 8, left: -16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border-subtle)" vertical={false} />
            <XAxis dataKey="label" tick={{ fontSize: 10, fill: "var(--color-alloy-300)" }} axisLine={false} tickLine={false}
              interval="preserveStartEnd" />
            <YAxis tick={{ fontSize: 11, fill: "var(--color-alloy-300)" }} axisLine={false} tickLine={false} allowDecimals={false} />
            <Tooltip
              contentStyle={{ background: "var(--color-panel-800)", border: "1px solid var(--color-border-subtle)", borderRadius: 8, fontSize: 12 }}
              labelStyle={{ color: "var(--color-alloy-100)", fontWeight: 600 }}
              formatter={(value) => [value, "Gate Passes"]}
            />
            <Line type="monotone" dataKey="count" stroke="#E8552E" strokeWidth={2}
              dot={{ r: 3, fill: "#E8552E", strokeWidth: 0 }}
              activeDot={{ r: 5, fill: "#E8552E" }} />
          </LineChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
