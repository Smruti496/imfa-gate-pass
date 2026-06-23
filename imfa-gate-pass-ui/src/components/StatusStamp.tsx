import { STATUS_META } from "@/lib/constants";
import type { GatePassStatus } from "@/lib/types";

export function StatusStamp({ status }: { status: GatePassStatus }) {
  const meta = STATUS_META[status];
  return (
    <span className={`font-display text-[10.5px] font-semibold tracking-[0.08em] px-2 py-1 rounded border-[1.5px] -rotate-1 whitespace-nowrap inline-block ${meta.stampClass}`}>
      {meta.stamp}
    </span>
  );
}
