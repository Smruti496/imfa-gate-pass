import type { GatePass } from "@/lib/types";
import { locById } from "@/lib/constants";
import { StatusStamp } from "./StatusStamp";
import { fmtTime } from "@/lib/utils";

export function GatePassCard({ pass, onClick }: { pass: GatePass; onClick: () => void }) {
  const loc = locById(pass.location);
  return (
    <button onClick={onClick}
      className="w-full text-left bg-panel-800 border border-border-subtle rounded-[10px] p-4 flex flex-col gap-2.5 hover:border-alloy-300 active:scale-[0.997] transition-all shadow-card">
      <div className="flex items-center justify-between gap-2">
        <span className="font-mono text-[10px] text-alloy-300 tracking-[0.05em]">{pass.passNo}</span>
        <StatusStamp status={pass.status} />
      </div>
      <div className="border-t border-border-subtle" />
      <div>
        <div className="font-semibold text-[14px] leading-snug truncate">{pass.visitorName}</div>
        <div className="text-[12px] text-alloy-300 truncate mt-0.5">{pass.companyName}</div>
      </div>
      <div className="flex flex-wrap gap-x-3.5 gap-y-1 mt-auto text-[11px] text-alloy-300">
        <span className="flex items-center gap-[5px]">
          <svg viewBox="0 0 24 24" width="11" height="11" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-slag-500 flex-shrink-0"><path d="M3 21V11l5 3.5V11l5 3.5V8l8-4v17H3Z"/></svg>
          <span className="truncate">{loc?.name ?? pass.location}</span>
        </span>
        <span className="flex items-center gap-[5px]">
          <svg viewBox="0 0 24 24" width="11" height="11" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-slag-500 flex-shrink-0"><circle cx="12" cy="12" r="9"/><polyline points="12 7 12 12 15.5 14"/></svg>
          {fmtTime(pass.visitTime)}
        </span>
        <span className="flex items-center gap-[5px]">
          <svg viewBox="0 0 24 24" width="11" height="11" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-slag-500 flex-shrink-0"><path d="M21 10c0 7-9 12-9 12s-9-5-9-12a9 9 0 1 1 18 0Z"/><circle cx="12" cy="10" r="3"/></svg>
          <span className="truncate">{pass.gate}</span>
        </span>
      </div>
    </button>
  );
}
