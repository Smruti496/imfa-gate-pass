export type GatePassStatus = "pending" | "onsite" | "cleared";

export interface GatePass {
  id: string;               // UUID
  passNo: string;
  visitorName: string;
  companyName: string;
  whomToVisit: string;
  photoId: string;          // UID number
  photoIdType: string;
  gender: string;           // "Male" | "Female" | "Other"
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

export interface StatusCount {
  status: string;
  count: number;
  percentage: number;
}

export interface LocationStatus {
  locationId: string;
  locationName: string;
  pending: number;
  onsite: number;
  cleared: number;
  total: number;
}

export interface GenderCount {
  gender: string;
  count: number;
  percentage: number;
}

export interface TrendPoint {
  label: string;
  count: number;
}

export interface AnalyticsData {
  statusDistribution: StatusCount[];
  locationStatusMatrix: LocationStatus[];
  genderDistribution: GenderCount[];
  hourlyTrend: TrendPoint[];
  monthlyTrend: TrendPoint[];
  quarterlyTrend: TrendPoint[];
  yearlyTrend: TrendPoint[];
}
