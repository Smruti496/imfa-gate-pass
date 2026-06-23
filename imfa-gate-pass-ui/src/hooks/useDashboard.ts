"use client";
import useSWR from "swr";
import { api } from "@/lib/api";

export function useDashboard() {
  const { data: stats, error: e1, isLoading: l1, mutate: m1 } =
    useSWR("stats", api.getStats, { refreshInterval: 30_000 });
  const { data: chartData, error: e2, isLoading: l2 } =
    useSWR("chart", api.getChartData, { refreshInterval: 30_000 });
  return { stats, chartData, isLoading: l1 || l2, error: e1 || e2, mutateStats: m1 };
}
