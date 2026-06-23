export const todayStr = () => new Date().toLocaleDateString('en-CA');
export const nowHHMM = () => {
  const d = new Date();
  return `${String(d.getHours()).padStart(2,"0")}:${String(d.getMinutes()).padStart(2,"0")}`;
};
export const fmtDate = (s: string | null) => {
  if (!s) return "—";
  return new Date(s + "T00:00:00").toLocaleDateString("en-IN", { day:"2-digit", month:"short", year:"numeric" });
};
export const fmtTime = (t: string | null) => {
  if (!t) return "—";
  // Already formatted with AM/PM (legacy data) — return as-is
  if (/AM|PM/i.test(t)) return t.trim();
  const [h, m] = t.split(":").map(Number);
  if (isNaN(h) || isNaN(m)) return t;
  const period = h >= 12 ? "PM" : "AM";
  return `${h % 12 === 0 ? 12 : h % 12}:${String(m).padStart(2,"0")} ${period}`;
};
