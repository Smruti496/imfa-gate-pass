# Analytics / List View Tabs — Design Spec

**Date:** 2026-06-23  
**Status:** Approved

---

## Context

The dashboard (`page.tsx`) currently renders analytics charts and the gate pass list sequentially on one long page. As the app grows, this creates visual noise — analytics and operational list-browsing serve different workflows. Splitting them into two tabs gives each section dedicated space and reduces cognitive load.

---

## Layout

```
┌─ Header (always visible) ────────────────────────────────┐
│  Logo + "New Gate Pass" button + Theme toggle            │
└──────────────────────────────────────────────────────────┘
┌─ StatsGrid (always visible) ─────────────────────────────┐
│  Total | On-Site | Pending | Cleared                     │
└──────────────────────────────────────────────────────────┘
┌─ Tab Bar ────────────────────────────────────────────────┐
│  [Analytics]   [List View]                               │
└──────────────────────────────────────────────────────────┘
┌─ Tab Panel ──────────────────────────────────────────────┐
│  Analytics:  SiteChart + AnalyticsSection                │
│  List View:  FilterControls + GatePassList               │
└──────────────────────────────────────────────────────────┘
```

**StatsGrid stays above tabs** — the 4 summary cards are global context relevant to both views.  
**SiteChart moves into Analytics tab** — it's a chart, belongs with analytical content.

---

## Implementation

### State

Add one `useState` to `page.tsx`:

```ts
const [activeTab, setActiveTab] = useState<"analytics" | "list">("analytics");
```

### Tab Bar

Styled identical to the existing pill tabs in `VisitorTrendCharts.tsx`:

```tsx
<div className="flex gap-2 mb-4">
  {[
    { key: "analytics", label: "Analytics" },
    { key: "list",      label: "List View"  },
  ].map((t) => (
    <button
      key={t.key}
      onClick={() => setActiveTab(t.key as "analytics" | "list")}
      className={`px-4 py-1.5 rounded-full text-sm font-medium transition-colors ${
        activeTab === t.key
          ? "bg-ember-500 text-white"
          : "bg-panel-700 text-alloy-300 hover:text-alloy-100"
      }`}
    >
      {t.label}
    </button>
  ))}
</div>
```

### Conditional Rendering

```tsx
{activeTab === "analytics" && (
  <>
    <SiteChart ... />
    <AnalyticsSection ... />
  </>
)}
{activeTab === "list" && (
  <>
    <FilterControls ... />
    <GatePassList ... />
  </>
)}
```

### File Changed

- `src/app/page.tsx` — only file modified

No new components, no routing changes.

---

## Verification

1. Run `npm run dev` in `imfa-gate-pass-ui/`
2. Confirm StatsGrid visible on both tabs
3. Click Analytics tab → SiteChart + charts visible, no list
4. Click List View tab → filters + gate pass cards visible, no charts
5. NewPassDrawer and DetailDrawer still open correctly from List View tab
6. Theme toggle still works
7. No console errors
