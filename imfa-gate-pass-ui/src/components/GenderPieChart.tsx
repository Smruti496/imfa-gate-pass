"use client";
import { PieChart, Pie, Cell, Tooltip, Legend, ResponsiveContainer, type PieLabelRenderProps } from "recharts";
import type { GenderCount } from "@/lib/types";

const GENDER_COLORS: Record<string, string> = {
  Male:    "#3B82F6",
  Female:  "#EC4899",
  Other:   "#8B5CF6",
  Unknown: "#94A3B8",
};

function InnerLabel({ cx, cy, midAngle, innerRadius, outerRadius, percent }: PieLabelRenderProps) {
  const RADIAN = Math.PI / 180;
  const pct = Number(percent) * 100;
  if (pct < 4) return null;
  const r = Number(innerRadius) + (Number(outerRadius) - Number(innerRadius)) * 0.55;
  const x = Number(cx) + r * Math.cos(-Number(midAngle) * RADIAN);
  const y = Number(cy) + r * Math.sin(-Number(midAngle) * RADIAN);
  return (
    <text x={x} y={y} fill="#fff" textAnchor="middle" dominantBaseline="central"
      fontSize={11} fontWeight={700}>
      {pct.toFixed(1)}%
    </text>
  );
}

interface Props { data: GenderCount[] | undefined; isLoading: boolean }

export function GenderPieChart({ data, isLoading }: Props) {
  const chartData = (data ?? []).map((d) => ({
    name: d.gender,
    value: d.count,
    color: GENDER_COLORS[d.gender] ?? "#888",
  }));

  return (
    <div className="bg-panel-800 border border-border-subtle rounded-[10px] p-4 flex flex-col gap-3">
      <h3 className="font-display text-[13px] font-semibold tracking-[0.04em] text-alloy-300 uppercase">Visitor Gender</h3>
      {isLoading ? (
        <div className="h-48 animate-pulse bg-panel-700 rounded-lg" />
      ) : chartData.length === 0 ? (
        <div className="h-48 flex items-center justify-center text-alloy-300 text-sm">No data</div>
      ) : (
        <ResponsiveContainer width="100%" height={200}>
          <PieChart>
            <Pie data={chartData} dataKey="value" nameKey="name" cx="50%" cy="50%"
              outerRadius={75} innerRadius={40}
              labelLine={false}
              label={InnerLabel}>
              {chartData.map((entry) => (
                <Cell key={entry.name} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip
              formatter={(value, name) => [value, name]}
              contentStyle={{ background: "var(--color-panel-800)", border: "1px solid var(--color-border-subtle)", borderRadius: 8, fontSize: 12 }}
              itemStyle={{ color: "var(--color-alloy-100)" }}
              labelFormatter={() => ""}
            />
            <Legend
              formatter={(value) => <span style={{ fontSize: 12, color: "var(--color-alloy-300)" }}>{value}</span>}
            />
          </PieChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
