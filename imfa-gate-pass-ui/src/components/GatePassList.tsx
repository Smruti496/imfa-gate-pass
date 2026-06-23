"use client";
import type { ListParams } from "@/lib/api";
import { useGatePasses } from "@/hooks/useGatePasses";
import { GatePassCard } from "./GatePassCard";

export function GatePassList({ filters, onOpenDetail }: { filters: ListParams; onOpenDetail: (id: string) => void }) {
  const { passes, isLoading } = useGatePasses(filters);
  if (isLoading) return <div className="text-center text-alloy-300 py-12">Loading…</div>;
  if (passes.length === 0)
    return <div className="text-center text-alloy-300 py-12 border border-dashed border-border-subtle rounded-xl text-[13.5px]">No passes logged for this filter. Issue a new gate pass to get started.</div>;
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-3">
      {passes.map((p) => <GatePassCard key={p.id} pass={p} onClick={() => onOpenDetail(p.id)} />)}
    </div>
  );
}
