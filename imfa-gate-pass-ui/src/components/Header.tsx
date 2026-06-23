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
