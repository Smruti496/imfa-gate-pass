export const LOCATIONS = [
  { id: "therubali",    name: "Therubali Plant",             sub: "Rayagada Dist., Odisha", gates: ["Main Gate", "Gate 2 – Loading Bay", "Township Gate"] },
  { id: "choudwar",     name: "Choudwar Plant",              sub: "Cuttack Dist., Odisha",  gates: ["Main Gate", "Raw Material Gate", "Admin Gate"] },
  { id: "kalinganagar", name: "Kalinganagar Plant",          sub: "Jajpur Dist., Odisha",   gates: ["Main Security Gate", "Gate 2"] },
  { id: "bhubaneswar",  name: "Bhubaneswar Corporate Office",sub: "Registered Office",      gates: ["Reception", "Basement Entry"] },
] as const;

export const PURPOSES = [
  "Business Meeting","Vendor / Supplier Visit","Equipment Maintenance",
  "Statutory / Govt. Inspection","Contractor Work","Job Interview",
  "Official / VIP Visit","Other",
] as const;

export const STATUS_META = {
  pending: {
    label: "Pending",
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
