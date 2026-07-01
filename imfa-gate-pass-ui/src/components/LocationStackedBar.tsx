"use client";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import type { LocationStatus } from "@/lib/types";

function abbrev(name: string) {
  return name.replace("JSW Ispat Special Products", "JSW Ispat");
}

interface Props { data: LocationStatus[] | undefined; isLoading: boolean }

export function LocationStackedBar({ data, isLoading }: Props) {
  const chartData = (data ?? []).map((d) => ({
    name: abbrev(d.locationName),
    Awaiting: d.pending,
    "On-Site": d.onsite,
    Cleared: d.cleared,
  }));

  return (
    <div className="bg-panel-800 border border-border-subtle rounded-[10px] p-4 flex flex-col gap-3">
      <h3 className="font-display text-[13px] font-semibold tracking-[0.04em] text-alloy-300 uppercase">Location-wise Pass Status</h3>
      {isLoading ? (
        <div className="h-52 animate-pulse bg-panel-700 rounded-lg" />
      ) : (
        <ResponsiveContainer width="100%" height={220}>
          <BarChart data={chartData} margin={{ top: 4, right: 8, left: -16, bottom: 0 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border-subtle)" vertical={false} />
            <XAxis dataKey="name" interval={0} tick={{ fontSize: 10, fill: "var(--color-alloy-300)" }} axisLine={false} tickLine={false} />
            <YAxis tick={{ fontSize: 11, fill: "var(--color-alloy-300)" }} axisLine={false} tickLine={false} allowDecimals={false} />
            <Tooltip
              contentStyle={{ background: "var(--color-panel-800)", border: "1px solid var(--color-border-subtle)", borderRadius: 8, fontSize: 12 }}
              labelStyle={{ color: "var(--color-alloy-100)", fontWeight: 600 }}
              cursor={{ fill: "var(--color-panel-700)" }}
            />
            <Legend formatter={(value) => <span style={{ fontSize: 12, color: "var(--color-alloy-300)" }}>{value}</span>} />
            <Bar dataKey="Awaiting" stackId="loc" fill="#F59E0B" radius={[0, 0, 0, 0]} />
            <Bar dataKey="On-Site"  stackId="loc" fill="#E8552E" radius={[0, 0, 0, 0]} />
            <Bar dataKey="Cleared"  stackId="loc" fill="#22C55E" radius={[4, 4, 0, 0]} />
          </BarChart>
        </ResponsiveContainer>
      )}
    </div>
  );
}
