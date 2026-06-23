"use client";
import { useState, useCallback, useEffect } from "react";
import { GlowBar } from "@/components/GlowBar";
import { Header } from "@/components/Header";
import { StatsGrid } from "@/components/StatsGrid";
import { SiteChart } from "@/components/SiteChart";
import { AnalyticsSection } from "@/components/AnalyticsSection";
import { FilterControls } from "@/components/FilterControls";
import { GatePassList } from "@/components/GatePassList";
import { NewPassDrawer } from "@/components/NewPassDrawer";
import { DetailDrawer } from "@/components/DetailDrawer";
import { useGatePasses } from "@/hooks/useGatePasses";
import { useDashboard } from "@/hooks/useDashboard";
import { useAnalytics } from "@/hooks/useAnalytics";

export default function DashboardPage() {
  const [query, setQuery]             = useState("");
  const [location, setLocation]       = useState("all");
  const [status, setStatus]           = useState("all");
  const [showAllDates, setShowAll]    = useState(false);
  const [showNewPass, setShowNewPass] = useState(false);
  const [detailId, setDetailId]       = useState<string | null>(null);
  const [theme, setTheme]             = useState<'dark' | 'light'>('light');

  useEffect(() => {
    const saved = localStorage.getItem('theme');
    if (saved === 'dark') {
      setTheme('dark');
      delete document.documentElement.dataset.theme;
    } else {
      document.documentElement.dataset.theme = 'light';
    }
  }, []);

  const toggleTheme = useCallback(() => {
    setTheme(prev => {
      const next = prev === 'dark' ? 'light' : 'dark';
      localStorage.setItem('theme', next);
      if (next === 'light') {
        document.documentElement.dataset.theme = 'light';
      } else {
        delete document.documentElement.dataset.theme;
      }
      return next;
    });
  }, []);

  const filters = { location, status, q: query, showAllDates };
  const { mutate: mutateList }                      = useGatePasses(filters);
  const { mutateStats }                             = useDashboard();
  const { analytics, isLoading: analyticsLoading }  = useAnalytics();

  const refresh = useCallback(() => { mutateList(); mutateStats?.(); }, [mutateList, mutateStats]);

  return (
    <>
      <GlowBar />
      <Header onNewPass={() => setShowNewPass(true)} theme={theme} onToggleTheme={toggleTheme} />
      <StatsGrid />
      <SiteChart />
      <AnalyticsSection analytics={analytics} isLoading={analyticsLoading} />
      <FilterControls
        query={query} location={location} status={status} showAllDates={showAllDates}
        onQuery={setQuery} onLocation={setLocation} onStatus={setStatus} onShowAllDates={setShowAll}
      />
      <main className="w-full px-8 pb-16">
        <GatePassList filters={filters} onOpenDetail={setDetailId} />
      </main>
      {showNewPass && <NewPassDrawer onClose={() => setShowNewPass(false)} onCreated={() => { setShowNewPass(false); refresh(); }} />}
      {detailId && <DetailDrawer passId={detailId} onClose={() => setDetailId(null)} onMutated={refresh} />}
    </>
  );
}
