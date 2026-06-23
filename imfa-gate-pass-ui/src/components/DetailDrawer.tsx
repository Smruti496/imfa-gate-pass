"use client";
import { useState, useEffect } from "react";
import { api } from "@/lib/api";
import type { GatePass } from "@/lib/types";
import { locById } from "@/lib/constants";
import { StatusStamp } from "./StatusStamp";
import { fmtDate, fmtTime } from "@/lib/utils";

export function DetailDrawer({ passId, onClose, onMutated }: { passId: string; onClose(): void; onMutated(): void }) {
  const [pass, setPass] = useState<GatePass | null>(null);
  const [loading, setLoading] = useState(true);
  const [actioning, setActioning] = useState(false);
  const [actionError, setActionError] = useState("");

  useEffect(() => { api.getPass(passId).then(setPass).finally(() => setLoading(false)); }, [passId]);

  const doAction = async (fn: () => Promise<GatePass | void>) => {
    setActioning(true);
    setActionError("");
    try {
      const result = await fn();
      if (result) { setPass(result as GatePass); onMutated(); }
      else { onMutated(); onClose(); }
    } catch (err: unknown) {
      setActionError(err instanceof Error ? err.message : "Action failed. Please try again.");
    } finally { setActioning(false); }
  };

  if (loading || !pass) return (
    <div className="fixed inset-0 bg-black/60 flex justify-end z-50">
      <div className="w-full max-w-[440px] h-full bg-graphite-900 border-l border-border-subtle flex items-center justify-center text-alloy-300">Loading…</div>
    </div>
  );

  const loc = locById(pass.location);
  const photoSrc = pass.photo
    ? (pass.photo.startsWith("data:") ? pass.photo : `data:image/jpeg;base64,${pass.photo}`)
    : null;
  return (
    <div className="fixed inset-0 bg-black/60 flex justify-end z-50 animate-fade-in"
      onMouseDown={(e) => { if (e.target === e.currentTarget) onClose(); }}>
      <div className="w-full max-w-[440px] h-full bg-graphite-900 border-l border-border-subtle flex flex-col overflow-y-auto">
        <div className="flex items-start justify-between p-5 pb-3 border-b border-border-subtle sticky top-0 bg-graphite-900">
          <div>
            <div className="text-[11px] text-ember-500 font-mono tracking-[0.05em]">{pass.passNo}</div>
            <h2 className="font-display text-[19px] font-semibold mt-0.5">{pass.visitorName}</h2>
          </div>
          <button onClick={onClose} className="border border-border-subtle text-alloy-300 rounded-lg p-1.5 hover:text-alloy-100 hover:border-alloy-300">✕</button>
        </div>
        <div className="p-5 flex flex-col gap-3.5 flex-1">
          <div className="flex items-center gap-3">
            <div className="w-16 h-16 rounded-lg bg-panel-800 border border-border-subtle flex items-center justify-center overflow-hidden flex-shrink-0">
              {photoSrc ? <img src={photoSrc} alt={pass.visitorName} className="w-full h-full object-cover" /> : <span className="text-2xl text-slag-500">👤</span>}
            </div>
            <div>
              <div className="text-[14px] font-medium mb-1.5">{pass.companyName}</div>
              <StatusStamp status={pass.status} />
            </div>
          </div>
          <dl className="grid grid-cols-2 gap-3 gap-x-4">
            {([
              ["Whom to visit", pass.whomToVisit],
              ["UID", `${pass.photoIdType}: ${pass.photoId}`],
              ["Plant / office", `${loc?.name ?? pass.location}\n${loc?.sub ?? ""}`],
              ["Entry gate", pass.gate],
              ["Expected", `${fmtDate(pass.visitDate)} · ${fmtTime(pass.visitTime)}`],
              ["Purpose", pass.purpose],
              ["Checked in", fmtTime(pass.checkInTime)],
              ["Checked out", fmtTime(pass.checkOutTime)],
            ] as [string,string][]).map(([dt, dd]) => (
              <div key={dt}>
                <dt className="text-[11px] text-alloy-300 mb-0.5">{dt}</dt>
                <dd className="text-[13.5px] whitespace-pre-line">{dd}</dd>
              </div>
            ))}
          </dl>
        </div>
        <div className="p-4 border-t border-border-subtle sticky bottom-0 bg-graphite-900 flex gap-2.5 justify-end flex-wrap">
          {actionError && (
            <div className="w-full text-[12px] bg-ember-dim border border-ember-500/40 text-ember-500 px-3 py-2 rounded-lg mb-2">
              {actionError}
            </div>
          )}
          {pass.status === "pending" && <>
            <button disabled={actioning} onClick={() => doAction(() => api.cancelPass(pass.id))}
              className="px-4 py-2 rounded-lg text-[13.5px] font-semibold text-ember-500 border border-ember-500/40 hover:border-ember-500 disabled:opacity-60">
              🗑 Cancel pass
            </button>
            <button disabled={actioning} onClick={() => doAction(() => api.checkIn(pass.id))}
              className="px-4 py-2 rounded-lg text-[13.5px] font-semibold bg-ember-500 text-[#1A0D08] hover:opacity-90 disabled:opacity-60">
              ↩ Check in
            </button>
          </>}
          {pass.status === "onsite" &&
            <button disabled={actioning} onClick={() => doAction(() => api.checkOut(pass.id))}
              className="px-4 py-2 rounded-lg text-[13.5px] font-semibold bg-ember-500 text-[#1A0D08] hover:opacity-90 disabled:opacity-60">
              ↪ Check out
            </button>
          }
          {pass.status === "cleared" &&
            <div className="text-[11px] text-[#7FAE8E] flex items-center gap-1">✔ Visit completed — pass closed.</div>
          }
        </div>
      </div>
    </div>
  );
}
