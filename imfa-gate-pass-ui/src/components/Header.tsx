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
        <div className="bg-white rounded-lg px-2 py-1 flex-shrink-0">
          <img src="/imfa-logo.png" alt="IMFA" className="h-9 w-auto" />
        </div>
        <div className="text-[11.5px] text-alloy-300 tracking-[0.06em]">Indian Metals &amp; Ferro Alloys · Gate Pass Control</div>
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
