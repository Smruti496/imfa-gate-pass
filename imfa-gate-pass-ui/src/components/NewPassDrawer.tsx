"use client";
import { useState } from "react";
import { api } from "@/lib/api";
import { LOCATIONS, PURPOSES, ID_TYPES } from "@/lib/constants";
import { todayStr, nowHHMM } from "@/lib/utils";

export function NewPassDrawer({ onClose, onCreated }: { onClose(): void; onCreated(): void }) {
  const [form, setForm] = useState({
    visitorName: "", companyName: "", whomToVisit: "",
    photoId: "", photoIdType: ID_TYPES[0],
    location: LOCATIONS[0].id, gate: LOCATIONS[0].gates[0],
    visitDate: todayStr(), visitTime: nowHHMM(),
    purpose: PURPOSES[0], photo: null as string | null,
  });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const gates = LOCATIONS.find((l) => l.id === form.location)?.gates ?? [];
  const set = (k: string, v: unknown) => setForm((f) => ({ ...f, [k]: v }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!form.visitorName || !form.companyName || !form.whomToVisit || !form.photoId) {
      setError("Visitor name, company, whom to visit and ID document are required."); return;
    }
    setError(""); setSubmitting(true);
    try { await api.createPass(form); onCreated(); }
    catch (err: unknown) { setError(err instanceof Error ? err.message : "Failed to create pass."); }
    finally { setSubmitting(false); }
  };

  const handlePhoto = async (file: File) => {
    const dataUrl = await compressImage(file);
    set("photo", dataUrl);
  };

  const handleIdDoc = async (file: File) => {
    const dataUrl = await compressImage(file);
    set("photoId", dataUrl);
  };

  const inp = "w-full bg-panel-800 border border-border-subtle rounded-lg px-3 py-2 text-[13.5px] text-alloy-100 outline-none focus:ring-1 focus:ring-steel-400";
  const sel = inp + " appearance-none pr-8 cursor-pointer";

  return (
    <div className="fixed inset-0 bg-black/60 flex justify-end z-50 animate-fade-in"
      onMouseDown={(e) => { if (e.target === e.currentTarget) onClose(); }}>
      <form onSubmit={handleSubmit}
        className="w-full max-w-[440px] h-full bg-graphite-900 border-l border-border-subtle flex flex-col overflow-y-auto">
        <div className="flex items-start justify-between p-5 pb-3 border-b border-border-subtle sticky top-0 bg-graphite-900 z-10">
          <div>
            <div className="text-[11px] text-ember-500 font-mono tracking-[0.05em]">New entry · Security Copy</div>
            <h2 className="font-display text-[19px] font-semibold mt-0.5">Issue Gate Pass</h2>
          </div>
          <button type="button" onClick={onClose} className="border border-border-subtle text-alloy-300 rounded-lg p-1.5 hover:text-alloy-100 hover:border-alloy-300">✕</button>
        </div>
        <div className="p-5 flex flex-col gap-3.5 flex-1">
          <Field label="Visitor name"><input className={inp} placeholder="E.g. Mr. Rajesh Kumar" autoFocus value={form.visitorName} onChange={(e) => set("visitorName", e.target.value)} /></Field>
          <Field label="Company / organisation"><input className={inp} placeholder="E.g. Shree Engineering Works" value={form.companyName} onChange={(e) => set("companyName", e.target.value)} /></Field>
          <Field label="Whom to visit"><input className={inp} placeholder="Host name and department" value={form.whomToVisit} onChange={(e) => set("whomToVisit", e.target.value)} /></Field>
          <div className="flex gap-3">
            <Field label="UID type">
              <div className="relative">
                <select className={sel} value={form.photoIdType} onChange={(e) => set("photoIdType", e.target.value)}>
                  {ID_TYPES.map((t) => <option key={t} value={t}>{t}</option>)}
                </select>
                <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-alloy-300 pointer-events-none text-xs">▾</span>
              </div>
            </Field>
            <Field label="ID document">
              <label className={`cursor-pointer flex items-center gap-1.5 ${inp} ${form.photoId ? "border-steel-400 text-steel-400" : ""} hover:border-alloy-300`}>
                {form.photoId ? "✓ Document uploaded" : "📎 Upload ID document"}
                <input type="file" accept="image/*" className="hidden" onChange={(e) => { const f = e.target.files?.[0]; if (f) handleIdDoc(f); }} />
              </label>
            </Field>
          </div>
          <div className="flex gap-3">
            <Field label="Plant / office">
              <div className="relative">
                <select className={sel} value={form.location} onChange={(e) => { set("location", e.target.value); set("gate", LOCATIONS.find(l=>l.id===e.target.value)?.gates[0] ?? ""); }}>
                  {LOCATIONS.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
                </select>
                <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-alloy-300 pointer-events-none text-xs">▾</span>
              </div>
            </Field>
            <Field label="Entry gate">
              <div className="relative">
                <select className={sel} value={form.gate} onChange={(e) => set("gate", e.target.value)}>
                  {gates.map((g) => <option key={g} value={g}>{g}</option>)}
                </select>
                <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-alloy-300 pointer-events-none text-xs">▾</span>
              </div>
            </Field>
          </div>
          <div className="flex gap-3">
            <Field label="Expected date"><input type="date" className={inp} value={form.visitDate} onChange={(e) => set("visitDate", e.target.value)} /></Field>
            <Field label="Expected time"><input type="time" className={inp} value={form.visitTime} onChange={(e) => set("visitTime", e.target.value)} /></Field>
          </div>
          <Field label="Purpose of visit">
            <div className="relative">
              <select className={sel} value={form.purpose} onChange={(e) => set("purpose", e.target.value)}>
                {PURPOSES.map((p) => <option key={p} value={p}>{p}</option>)}
              </select>
              <span className="absolute right-2.5 top-1/2 -translate-y-1/2 text-alloy-300 pointer-events-none text-xs">▾</span>
            </div>
          </Field>
          <div className="flex flex-col gap-1.5">
            <span className="text-xs text-alloy-300">Recent photo</span>
            <div className="flex items-center gap-3">
              <div className="w-14 h-14 rounded-lg bg-panel-800 border border-border-subtle flex items-center justify-center overflow-hidden flex-shrink-0">
                {form.photo ? <img src={form.photo} alt="preview" className="w-full h-full object-cover" /> : <span className="text-slag-500 text-xl">👤</span>}
              </div>
              <label className="cursor-pointer inline-flex items-center gap-1.5 px-3 py-2 rounded-lg text-[13.5px] font-semibold border border-border-subtle text-alloy-100 hover:border-alloy-300">
                📷 Take / upload photo
                <input type="file" accept="image/*" capture="environment" className="hidden" onChange={(e) => { const f = e.target.files?.[0]; if (f) handlePhoto(f); }} />
              </label>
            </div>
            <span className="text-[11px] text-alloy-300">Auto-compressed to stay under 200 KB.</span>
          </div>
          {error && <div className="text-[12.5px] bg-ember-dim border border-ember-500/40 text-ember-500 px-3 py-2 rounded-lg">{error}</div>}
        </div>
        <div className="p-4 border-t border-border-subtle sticky bottom-0 bg-graphite-900 flex gap-2.5 justify-end">
          <button type="button" onClick={onClose} className="px-4 py-2 rounded-lg text-[13.5px] font-semibold border border-border-subtle text-alloy-100 hover:border-alloy-300">Cancel</button>
          <button type="submit" disabled={submitting} className="px-4 py-2 rounded-lg text-[13.5px] font-semibold bg-ember-500 text-[#1A0D08] hover:opacity-90 disabled:opacity-60">
            {submitting ? "Saving…" : "+ Issue pass"}
          </button>
        </div>
      </form>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return <label className="flex flex-col gap-1.5 flex-1"><span className="text-xs text-alloy-300">{label}</span>{children}</label>;
}

async function compressImage(file: File, maxBytes = 200 * 1024): Promise<string> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onerror = () => reject(new Error("Could not read file"));
    reader.onload = (e) => {
      const img = new Image();
      img.onerror = () => reject(new Error("Could not decode image"));
      img.onload = () => {
        let { width, height } = img;
        const maxDim = 480;
        if (width > maxDim || height > maxDim) {
          const scale = Math.min(maxDim / width, maxDim / height);
          width = Math.round(width * scale); height = Math.round(height * scale);
        }
        const canvas = document.createElement("canvas");
        canvas.width = width; canvas.height = height;
        canvas.getContext("2d")!.drawImage(img, 0, 0, width, height);
        const sizeOf = (s: string) => Math.round((s.length * 3) / 4);
        let q = 0.85, dataUrl = canvas.toDataURL("image/jpeg", q), tries = 0;
        while (sizeOf(dataUrl) > maxBytes && q > 0.3 && tries < 8) {
          q -= 0.1; dataUrl = canvas.toDataURL("image/jpeg", q); tries++;
        }
        resolve(dataUrl);
      };
      img.src = e.target!.result as string;
    };
    reader.readAsDataURL(file);
  });
}
