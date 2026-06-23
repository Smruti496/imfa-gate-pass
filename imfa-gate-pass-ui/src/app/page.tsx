"use client";
import { useState, useCallback } from "react";
import { GlowBar } from "@/components/GlowBar";
import { Header } from "@/components/Header";
import { StatsGrid } from "@/components/StatsGrid";
import { SiteChart } from "@/components/SiteChart";
import { FilterControls } from "@/components/FilterControls";
import { GatePassList } from "@/components/GatePassList";
import { NewPassDrawer } from "@/components/NewPassDrawer";
import { DetailDrawer } from "@/components/DetailDrawer";
import { useGatePasses } from "@/hooks/useGatePasses";
import { useDashboard } from "@/hooks/useDashboard";

export default function DashboardPage() {
  const [query, setQuery]             = useState("");
  const [location, setLocation]       = useState("all");
  const [status, setStatus]           = useState("all");
  const [showAllDates, setShowAll]    = useState(false);
  const [showNewPass, setShowNewPass] = useState(false);
  const [detailId, setDetailId]       = useState<string | null>(null);

  const filters = { location, status, q: query, showAllDates };
  const { mutate: mutateList }        = useGatePasses(filters);
  const { mutateStats }               = useDashboard();

  const refresh = useCallback(() => { mutateList(); mutateStats?.(); }, [mutateList, mutateStats]);

  return (
    <>
      <GlowBar />
      <Header onNewPass={() => setShowNewPass(true)} />
      <StatsGrid />
      <SiteChart />
      <FilterControls
        query={query} location={location} status={status} showAllDates={showAllDates}
        onQuery={setQuery} onLocation={setLocation} onStatus={setStatus} onShowAllDates={setShowAll}
      />
      <main className="max-w-[1080px] mx-auto px-5 pb-16">
        <GatePassList filters={filters} onOpenDetail={setDetailId} />
      </main>
      {showNewPass && <NewPassDrawer onClose={() => setShowNewPass(false)} onCreated={() => { setShowNewPass(false); refresh(); }} />}
      {detailId && <DetailDrawer passId={detailId} onClose={() => setDetailId(null)} onMutated={refresh} />}
    </>
  );
}
