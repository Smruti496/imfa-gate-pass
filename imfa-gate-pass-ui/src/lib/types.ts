export type GatePassStatus = "pending" | "onsite" | "cleared";

export interface GatePass {
  id: string;               // UUID
  passNo: string;
  visitorName: string;
  companyName: string;
  whomToVisit: string;
  photoId: string;          // UID number
  photoIdType: string;
  location: string;
  gate: string;
  visitDate: string;        // "YYYY-MM-DD"
  visitTime: string;        // "HH:MM"
  purpose: string;
  photo: string | null;
  status: GatePassStatus;
  checkInTime: string | null;
  checkOutTime: string | null;
  createdTime: string | null;
}

export interface DashboardStats {
  totalToday: number;
  onsite: number;
  pending: number;
  cleared: number;
}

export interface ChartData {
  locationId: string;
  locationName: string;
  count: number;
}

export interface PageResult<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
}
