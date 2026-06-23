import useSWR from "swr";
import { api } from "@/lib/api";

export function useAnalytics() {
  const { data, isLoading } = useSWR("analytics", api.getAnalytics, { refreshInterval: 60_000 });
  return { analytics: data, isLoading };
}
