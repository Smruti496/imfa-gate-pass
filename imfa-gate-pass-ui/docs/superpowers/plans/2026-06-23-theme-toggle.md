# Theme Toggle Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a moon/sun icon toggle in the header that switches between dark and light themes, persisted via localStorage.

**Architecture:** Tailwind v4 generates CSS vars from `@theme` and utility classes that reference them via `var()`. A `[data-theme="light"]` block overrides those vars — no component class names change. A `ThemeToggle` client component manages the toggle and icon. An inline script in `<head>` applies the saved theme before paint to prevent flash.

**Tech Stack:** Next.js 16 App Router, React 19, Tailwind CSS v4, TypeScript

## Global Constraints

- Zero new npm dependencies
- Tailwind v4 CSS-first config — no `tailwind.config.js`
- `'use client'` required on any component using `useState`, `useEffect`, or browser APIs
- Default theme: `dark`
- localStorage key: `'theme'`
- `data-theme` attribute lives on `document.documentElement` (`<html>`)

---

### Task 1: Add light theme override block to globals.css

**Files:**
- Modify: `src/app/globals.css`

**Interfaces:**
- Produces: `[data-theme="light"]` block that overrides the CSS custom properties Tailwind v4 auto-generates from `@theme`

**How Tailwind v4 theming works:** When you write `--color-foo: #hex` in `@theme`, Tailwind (a) emits `--color-foo: #hex` on `:root` and (b) generates utility classes like `.bg-foo { background-color: var(--color-foo) }`. Overriding `--color-foo` in `[data-theme="light"]` cascades everywhere — no class changes needed. Do NOT use `var()` inside `@theme` (unsupported).

- [ ] **Step 1: Replace `src/app/globals.css` with this content**

```css
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&family=Oswald:wght@500;600;700&family=JetBrains+Mono:wght@400;500&display=swap');
@import "tailwindcss";

@theme {
  --color-graphite-900: #15171B;
  --color-panel-800:    #1F2329;
  --color-panel-700:    #262B32;
  --color-ember-500:    #E8552E;
  --color-ember-dim:    rgba(232, 85, 46, 0.14);
  --color-steel-400:    #5C8AA8;
  --color-steel-dim:    rgba(92, 138, 168, 0.16);
  --color-slag-500:     #80868F;
  --color-slag-dim:     rgba(128, 134, 143, 0.16);
  --color-alloy-100:    #ECEEF0;
  --color-alloy-300:    #B7BCC2;
  --color-border-subtle:#2B3038;

  --font-display: "Oswald", sans-serif;
  --font-mono:    "JetBrains Mono", monospace;
  --font-sans:    "Inter", system-ui, sans-serif;
}

[data-theme="light"] {
  --color-graphite-900: #F5F6F8;
  --color-panel-800:    #FFFFFF;
  --color-panel-700:    #EEF0F3;
  --color-ember-dim:    rgba(232, 85, 46, 0.12);
  --color-steel-dim:    rgba(92, 138, 168, 0.14);
  --color-slag-500:     #60666E;
  --color-slag-dim:     rgba(96, 102, 110, 0.14);
  --color-alloy-100:    #1A1D21;
  --color-alloy-300:    #4A5058;
  --color-border-subtle:#D8DCE0;
}

@layer base {
  body { @apply bg-graphite-900 text-alloy-100 font-sans; }
  * { box-sizing: border-box; }
}

@keyframes fade-in { from { opacity: 0 } to { opacity: 1 } }
.animate-fade-in { animation: fade-in 0.15s ease; }
```

- [ ] **Step 2: Verify app still renders correctly in dark mode**

Start dev server: `npm run dev`
Open `http://localhost:3000` — should look identical to before (dark theme).

- [ ] **Step 3: Manually test light theme override**

In browser devtools console run: `document.documentElement.dataset.theme = 'light'`
Verify background turns light gray (#F5F6F8), text turns dark. Then remove: `delete document.documentElement.dataset.theme`

- [ ] **Step 4: Commit**

```bash
git add src/app/globals.css
git commit -m "feat: add light theme CSS variable overrides to globals.css"
```

---

### Task 2: Add flash-prevention script and hydration suppression to layout

**Files:**
- Modify: `src/app/layout.tsx`

**Interfaces:**
- Produces: `<html>` with `suppressHydrationWarning`; inline `<script>` that reads `localStorage('theme')` and sets `document.documentElement.dataset.theme` before paint

- [ ] **Step 1: Replace `src/app/layout.tsx` with this content**

```tsx
import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "IMFA · Gate Pass Control",
  description: "Indian Metals & Ferro Alloys — Gate Pass Management System",
};

const themeScript = `
(function() {
  try {
    var t = localStorage.getItem('theme');
    if (t === 'light') document.documentElement.dataset.theme = 'light';
  } catch(e) {}
})();
`;

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeScript }} />
      </head>
      <body className="min-h-screen bg-graphite-900 text-alloy-100">{children}</body>
    </html>
  );
}
```

- [ ] **Step 2: Verify no hydration warning in console**

With dev server running, open `http://localhost:3000` and check browser console — no React hydration mismatch warnings.

- [ ] **Step 3: Commit**

```bash
git add src/app/layout.tsx
git commit -m "feat: add flash-prevention theme script to layout"
```

---

### Task 3: Create ThemeToggle component

**Files:**
- Create: `src/components/ThemeToggle.tsx`

**Interfaces:**
- Consumes: `theme: 'dark' | 'light'`, `onToggle: () => void` props
- Produces: named export `ThemeToggle`

- [ ] **Step 1: Create `src/components/ThemeToggle.tsx`**

```tsx
"use client";

interface ThemeToggleProps {
  theme: 'dark' | 'light';
  onToggle: () => void;
}

export function ThemeToggle({ theme, onToggle }: ThemeToggleProps) {
  return (
    <button
      onClick={onToggle}
      aria-label={theme === 'dark' ? 'Switch to light theme' : 'Switch to dark theme'}
      className="w-9 h-9 flex items-center justify-center rounded-lg hover:bg-panel-700 active:scale-[0.95] transition-all text-alloy-300 hover:text-alloy-100"
    >
      {theme === 'dark' ? (
        <svg viewBox="0 0 24 24" className="w-5 h-5 fill-none stroke-current stroke-2" strokeLinecap="round" strokeLinejoin="round">
          <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z" />
        </svg>
      ) : (
        <svg viewBox="0 0 24 24" className="w-5 h-5 fill-none stroke-current stroke-2" strokeLinecap="round" strokeLinejoin="round">
          <circle cx="12" cy="12" r="5" />
          <line x1="12" y1="1" x2="12" y2="3" />
          <line x1="12" y1="21" x2="12" y2="23" />
          <line x1="4.22" y1="4.22" x2="5.64" y2="5.64" />
          <line x1="18.36" y1="18.36" x2="19.78" y2="19.78" />
          <line x1="1" y1="12" x2="3" y2="12" />
          <line x1="21" y1="12" x2="23" y2="12" />
          <line x1="4.22" y1="19.78" x2="5.64" y2="18.36" />
          <line x1="18.36" y1="5.64" x2="19.78" y2="4.22" />
        </svg>
      )}
    </button>
  );
}
```

- [ ] **Step 2: TypeScript check**

```bash
npx tsc --noEmit
```
Expected: no errors.

- [ ] **Step 3: Commit**

```bash
git add src/components/ThemeToggle.tsx
git commit -m "feat: add ThemeToggle component with moon/sun icons"
```

---

### Task 4: Wire theme state in page.tsx and update Header

**Files:**
- Modify: `src/app/page.tsx`
- Modify: `src/components/Header.tsx`

**Interfaces:**
- Consumes: `ThemeToggle` (from Task 3) with props `theme: 'dark' | 'light'`, `onToggle: () => void`
- `Header` consumes: `onNewPass: () => void`, `theme: 'dark' | 'light'`, `onToggleTheme: () => void`

- [ ] **Step 1: Replace `src/app/page.tsx` with this content**

```tsx
"use client";
import { useState, useCallback, useEffect } from "react";
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
  const [theme, setTheme]             = useState<'dark' | 'light'>('dark');

  useEffect(() => {
    const saved = localStorage.getItem('theme');
    if (saved === 'light') setTheme('light');
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
  const { mutate: mutateList }        = useGatePasses(filters);
  const { mutateStats }               = useDashboard();

  const refresh = useCallback(() => { mutateList(); mutateStats?.(); }, [mutateList, mutateStats]);

  return (
    <>
      <GlowBar />
      <Header onNewPass={() => setShowNewPass(true)} theme={theme} onToggleTheme={toggleTheme} />
      <StatsGrid />
      <SiteChart />
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
```

- [ ] **Step 2: Replace `src/components/Header.tsx` with this content**

```tsx
import { ThemeToggle } from "@/components/ThemeToggle";

interface HeaderProps {
  onNewPass: () => void;
  theme: 'dark' | 'light';
  onToggleTheme: () => void;
}

export function Header({ onNewPass, theme, onToggleTheme }: HeaderProps) {
  return (
    <header className="w-full px-8 py-5 pb-3 flex items-start justify-between gap-4 flex-wrap">
      <div className="flex items-center gap-3">
        <div className="w-11 h-11 rounded-lg bg-panel-800 border border-ember-500 flex items-center justify-center flex-shrink-0">
          <svg viewBox="0 0 24 24" className="w-[22px] h-[22px] text-ember-500 fill-none stroke-current stroke-2 stroke-linecap-round stroke-linejoin-round">
            <path d="M8.5 14.5A2.5 2.5 0 0 0 11 17a2.5 2.5 0 0 0 2.5-2.5c0-1.5-1.5-2-1.5-3.7 0 0 2.8 1 2.8 4.2a4.3 4.3 0 0 1-8.6 0c0-2.9 1.7-4.6 2.4-6.1.4 1 .9 1.8.9 3.1Z"/>
          </svg>
        </div>
        <div>
          <div className="font-display text-[17px] font-semibold uppercase tracking-[0.04em]">IMFA</div>
          <div className="text-[11.5px] text-alloy-300 tracking-[0.06em] mt-0.5">Indian Metals &amp; Ferro Alloys · Gate Pass Control</div>
        </div>
      </div>
      <div className="flex items-center gap-2">
        <ThemeToggle theme={theme} onToggle={onToggleTheme} />
        <button onClick={onNewPass}
          className="inline-flex items-center gap-1.5 px-4 py-2 rounded-lg text-[13.5px] font-semibold bg-ember-500 text-[#1A0D08] hover:opacity-90 active:scale-[0.97] transition-all">
          + New gate pass
        </button>
      </div>
    </header>
  );
}
```

- [ ] **Step 3: TypeScript check**

```bash
npx tsc --noEmit
```
Expected: no errors.

- [ ] **Step 4: Smoke test in browser**

Open `http://localhost:3000`. Click moon icon — page goes light, icon becomes sun. Click sun — page goes dark, icon becomes moon. Reload — theme persists.

- [ ] **Step 5: Commit**

```bash
git add src/app/page.tsx src/components/Header.tsx
git commit -m "feat: wire theme toggle state and render ThemeToggle in header"
```
