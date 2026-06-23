import type { GatePass } from "@/lib/types";
import { locById } from "@/lib/constants";
import { StatusStamp } from "./StatusStamp";
import { fmtTime } from "@/lib/utils";

export function GatePassCard({ pass, onClick }: { pass: GatePass; onClick: () => void }) {
  const loc = locById(pass.location);
  return (
    <button onClick={onClick}
      className="w-full text-left bg-panel-800 border border-border-subtle rounded-[10px] flex overflow-hidden hover:border-[#3A4047] active:scale-[0.997] transition-all">
      <div className="bg-panel-700 w-16 flex-shrink-0 flex flex-col items-center justify-center gap-1.5 py-2 px-1">
        <span className="font-mono text-[9.5px] text-alloy-300 [writing-mode:vertical-rl] tracking-[0.05em]">{pass.passNo}</span>
        <svg viewBox="0 0 24 24" className="w-[14px] h-[14px] text-ember-500 opacity-85 fill-none stroke-current stroke-2 stroke-linecap-round stroke-linejoin-round">
          <path d="M8.5 14.5A2.5 2.5 0 0 0 11 17a2.5 2.5 0 0 0 2.5-2.5c0-1.5-1.5-2-1.5-3.7 0 0 2.8 1 2.8 4.2a4.3 4.3 0 0 1-8.6 0c0-2.9 1.7-4.6 2.4-6.1.4 1 .9 1.8.9 3.1Z"/>
        </svg>
      </div>
      <div className="w-px border-l-2 border-dashed border-[#353B43] flex-shrink-0" />
      <div className="p-3 px-4 flex-1 min-w-0">
        <div className="flex items-center justify-between gap-2.5">
          <div className="font-semibold text-[14.5px]">{pass.visitorName}</div>
          <StatusStamp status={pass.status} />
        </div>
        <div className="text-[12.5px] text-alloy-300 mt-0.5">{pass.companyName}</div>
        <div className="flex gap-3.5 flex-wrap mt-2 text-[11.5px] text-alloy-300">
          <span>🏭 {loc?.name ?? pass.location}</span>
          <span>📍 {pass.gate}</span>
          <span>🕐 {fmtTime(pass.visitTime)}</span>
        </div>
      </div>
    </button>
  );
}
