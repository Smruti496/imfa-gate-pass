"use client";
import { LOCATIONS } from "@/lib/constants";

interface Props {
  query: string; location: string; status: string; showAllDates: boolean;
  onQuery(v: string): void; onLocation(v: string): void;
  onStatus(v: string): void; onShowAllDates(v: boolean): void;
}

function Chevron() {
  return (
    <svg className="absolute right-2.5 top-1/2 -translate-y-1/2 pointer-events-none text-alloy-300" viewBox="0 0 24 24" width="14" height="14" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <polyline points="6 9 12 15 18 9"/>
    </svg>
  );
}

export function FilterControls({ query, location, status, showAllDates, onQuery, onLocation, onStatus, onShowAllDates }: Props) {
  const sel = "w-full appearance-none bg-panel-800 border border-border-subtle rounded-lg px-3 pr-8 py-2 text-[13px] text-alloy-100 cursor-pointer outline-none focus:ring-1 focus:ring-steel-400";
  return (
    <section className="w-full px-8 pb-3 flex gap-2.5 flex-wrap items-center">
      <div className="relative flex-1 min-w-[180px]">
        <input value={query} onChange={(e) => onQuery(e.target.value)}
          placeholder="Search name, company, UID or pass no."
          className="w-full bg-panel-800 border border-border-subtle rounded-lg pl-9 pr-3 py-2 text-[13.5px] text-alloy-100 outline-none focus:ring-1 focus:ring-steel-400" />
        <svg className="absolute left-3 top-1/2 -translate-y-1/2 text-alloy-300 pointer-events-none" viewBox="0 0 24 24" width="15" height="15" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
      </div>
      <div className="relative w-[168px]">
        <select value={location} onChange={(e) => onLocation(e.target.value)} className={sel}>
          <option value="all">All sites</option>
          {LOCATIONS.map((l) => <option key={l.id} value={l.id}>{l.name}</option>)}
        </select>
        <Chevron />
      </div>
      <div className="relative w-[150px]">
        <select value={status} onChange={(e) => onStatus(e.target.value)} className={sel}>
          <option value="all">All statuses</option>
          <option value="pending">Pending</option>
          <option value="onsite">On-Site</option>
          <option value="cleared">Cleared</option>
        </select>
        <Chevron />
      </div>
      <label className="flex items-center gap-1.5 text-[12.5px] text-alloy-300 cursor-pointer select-none">
        <input type="checkbox" checked={showAllDates} onChange={(e) => onShowAllDates(e.target.checked)} className="accent-ember-500" />
        Show all dates
      </label>
    </section>
  );
}
