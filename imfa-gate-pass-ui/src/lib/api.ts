import type { GatePass, DashboardStats, ChartData, PageResult } from "./types";

const BASE = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(`${BASE}${path}`, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) throw new Error(`API ${res.status}: ${await res.text()}`);
  if (res.status === 204) return undefined as T;
  return res.json();
}

export interface ListParams {
  location?: string; status?: string; q?: string;
  showAllDates?: boolean; page?: number; size?: number;
}

export const api = {
  getStats:    () => request<DashboardStats>("/dashboard/stats"),
  getChartData:() => request<ChartData[]>("/dashboard/chart"),
  listPasses: (p: ListParams = {}) => {
    const qs = new URLSearchParams();
    if (p.location && p.location !== "all") qs.set("location", p.location);
    if (p.status && p.status !== "all") qs.set("status", p.status);
    if (p.q) qs.set("q", p.q);
    if (p.showAllDates) qs.set("showAllDates", "true");
    qs.set("page", String(p.page ?? 0));
    qs.set("size", String(p.size ?? 50));
    return request<PageResult<GatePass>>(`/gate-passes?${qs}`);
  },
  getPass:    (id: string) => request<GatePass>(`/gate-passes/${id}`),
  createPass: (data: Partial<GatePass> & Record<string, unknown>) =>
    request<GatePass>("/gate-passes", { method: "POST", body: JSON.stringify(data) }),
  checkIn:    (id: string) => request<GatePass>(`/gate-passes/${id}/checkin`,  { method: "PATCH" }),
  checkOut:   (id: string) => request<GatePass>(`/gate-passes/${id}/checkout`, { method: "PATCH" }),
  cancelPass: (id: string) => request<void>(`/gate-passes/${id}`, { method: "DELETE" }),
};
