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
    if (t !== 'dark') document.documentElement.dataset.theme = 'light';
  } catch(e) {
    document.documentElement.dataset.theme = 'light';
  }
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
