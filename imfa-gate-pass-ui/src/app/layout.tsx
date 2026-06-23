import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "IMFA · Gate Pass Control",
  description: "Indian Metals & Ferro Alloys — Gate Pass Management System",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body className="min-h-screen bg-graphite-900 text-alloy-100">{children}</body>
    </html>
  );
}
