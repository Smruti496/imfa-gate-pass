export const LOCATIONS = [
  { id: "vijayanagar",         name: "Vijayanagar Works",          sub: "Bellary Dist., Karnataka",         gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "salem",               name: "Salem Works",                sub: "Salem Dist., Tamil Nadu",          gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "tarapur",             name: "Tarapur Works",              sub: "Palghar Dist., Maharashtra",       gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "kalmeshwar",          name: "Kalmeshwar Works",           sub: "Nagpur Dist., Maharashtra",        gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "vasind",              name: "Vasind Works",               sub: "Thane Dist., Maharashtra",         gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "jsw-ispat",           name: "JSW Ispat Special Products", sub: "Dolvi, Raigad Dist., Maharashtra", gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "bhushan-power-steel", name: "Bhushan Power & Steel",      sub: "Jharsuguda Dist., Odisha",         gates: ["Main Gate", "Gate 2", "Gate 3"] },
  { id: "dolvi",               name: "Dolvi Works",                sub: "Raigad Dist., Maharashtra",        gates: ["Main Gate", "Gate 2", "Gate 3"] },
] as const;

export const PURPOSES = [
  "Business Meeting","Vendor / Supplier Visit","Equipment Maintenance",
  "Statutory / Govt. Inspection","Contractor Work","Job Interview",
  "Official / VIP Visit","Other",
] as const;

export const STATUS_META = {
  pending: {
    label: "Awaiting",
    stamp: "AWAITING",
    stampClass: "text-steel-400 border-steel-400 bg-steel-dim",
  },
  onsite: {
    label: "On-Site",
    stamp: "ON-SITE",
    stampClass: "text-ember-500 border-ember-500 bg-ember-dim",
  },
  cleared: {
    label: "Cleared",
    stamp: "CLEARED",
    stampClass: "text-slag-500 border-slag-500 bg-slag-dim",
  },
} as const;

export const ID_TYPES = ["Aadhaar", "PAN", "Voter ID", "Passport", "Driving Licence"] as const;

export const GENDERS = ["Male", "Female", "Other"] as const;

export const locById = (id: string | null | undefined) =>
  LOCATIONS.find((l) => l.id === id || id?.toLowerCase().startsWith(l.id));
