import useSWR from "swr";
import { api } from "@/lib/api";

interface DateRange { startDate: string; endDate: string }

export function useAnalytics(range: DateRange) {
  const { data, isLoading } = useSWR(
    ["analytics", range.startDate, range.endDate],
    () => api.getAnalytics(range.startDate, range.endDate),
    { refreshInterval: 60_000 }
  );
  return { analytics: data, isLoading };
}
