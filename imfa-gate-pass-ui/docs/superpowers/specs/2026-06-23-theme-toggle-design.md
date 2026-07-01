# Theme Toggle — Design Spec
**Date:** 2026-06-23  
**Scope:** Add light/dark theme toggle icon to the IMFA Gate Pass UI header

---

## Goal

Users can switch between dark (current) and light themes via a single icon button in the header. Preference persists across sessions via `localStorage`. No new npm packages.

---

## Architecture

### Color Token Strategy

All existing color tokens in `globals.css` are converted to CSS custom properties. A `[data-theme="light"]` block overrides them with light-palette values. Tailwind's `@theme` reads these vars, so all existing class names (`bg-graphite-900`, `text-alloy-100`, etc.) automatically respond to theme without touching any component files.

**Dark (default):**
| Token | Value |
|---|---|
| `graphite-900` | `#15171B` |
| `panel-800` | `#1F2329` |
| `panel-700` | `#262B32` |
| `alloy-100` | `#ECEEF0` |
| `alloy-300` | `#B7BCC2` |
| `border-subtle` | `#2B3038` |
| `slag-500` | `#80868F` |

**Light overrides:**
| Token | Value |
|---|---|
| `graphite-900` | `#F5F6F8` |
| `panel-800` | `#FFFFFF` |
| `panel-700` | `#EEF0F3` |
| `alloy-100` | `#1A1D21` |
| `alloy-300` | `#4A5058` |
| `border-subtle` | `#D8DCE0` |
| `slag-500` | `#60666E` |

Accent colors (`ember-500`, `steel-400`) are unchanged in both themes.

### Flash Prevention

An inline `<script>` injected before any content in `<head>` reads `localStorage('theme')` and sets `document.documentElement.dataset.theme` synchronously — prevents white flash on dark-preferring users who reload.

---

## Components

### `ThemeToggle.tsx` (new, client component)
- Reads initial theme from `localStorage` on mount (default: `'dark'`)
- Renders crescent moon SVG in dark mode, sun SVG in light mode
- On click: flips theme, writes to `localStorage`, sets `document.documentElement.dataset.theme`
- Icon stroke color: `currentColor` — inherits white in dark mode, dark in light mode via CSS

### `Header.tsx` (modified)
- Accepts `theme: 'dark' | 'light'` and `onToggleTheme: () => void` props
- Renders `<ThemeToggle>` between logo and "+ New gate pass" button

### `layout.tsx` (modified)
- Adds `suppressHydrationWarning` on `<html>` (required — `data-theme` set client-side)
- Injects flash-prevention inline script in `<head>`

### `page.tsx` (modified)
- Manages `theme` state (initialized from `localStorage` or `'dark'`)
- Passes `theme` + `onToggleTheme` down to `<Header>`

---

## Data Flow

```
localStorage ──► page.tsx (theme state)
                    │
                    ▼
               Header.tsx
                    │
                    ▼
           ThemeToggle.tsx ──► document.documentElement.dataset.theme
                                        │
                                        ▼
                              [data-theme="light"] CSS block
                              overrides color token values
```

---

## Files Changed

| File | Change |
|---|---|
| `src/app/globals.css` | Convert tokens to CSS vars; add `[data-theme="light"]` block |
| `src/app/layout.tsx` | Add `suppressHydrationWarning`, flash-prevention script |
| `src/components/ThemeToggle.tsx` | New client component |
| `src/components/Header.tsx` | Accept theme props, render toggle |
| `src/app/page.tsx` | Manage theme state, wire props |

---

## Out of Scope

- Automatic system preference detection (`prefers-color-scheme`) — manual toggle only
- Per-user server-side theme persistence
