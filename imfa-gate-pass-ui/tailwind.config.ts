import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        "graphite-900": "#15171B",
        "panel-800":    "#1F2329",
        "panel-700":    "#262B32",
        "ember-500":    "#E8552E",
        "ember-dim":    "rgba(232,85,46,0.14)",
        "steel-400":    "#5C8AA8",
        "steel-dim":    "rgba(92,138,168,0.16)",
        "slag-500":     "#80868F",
        "slag-dim":     "rgba(128,134,143,0.16)",
        "alloy-100":    "#ECEEF0",
        "alloy-300":    "#B7BCC2",
        "border-subtle":"#2B3038",
      },
      fontFamily: {
        display: ["Oswald", "sans-serif"],
        mono:    ["JetBrains Mono", "monospace"],
        sans:    ["Inter", "system-ui", "sans-serif"],
      },
    },
  },
  plugins: [],
};
export default config;
