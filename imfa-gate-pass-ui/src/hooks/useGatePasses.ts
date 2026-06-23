"use client";
import useSWR from "swr";
import { api, type ListParams } from "@/lib/api";

export function useGatePasses(params: ListParams) {
  const key = ["gate-passes", params];
  const { data, error, isLoading, mutate } = useSWR(
    key, () => api.listPasses(params), { refreshInterval: 15_000 }
  );
  return { passes: data?.content ?? [], total: data?.totalElements ?? 0, isLoading, error, mutate };
}
