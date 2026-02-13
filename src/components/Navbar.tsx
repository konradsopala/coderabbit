"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { getISOWeek, isoWeekToDate } from "@/lib/calendar";

export default function Navbar() {
  const pathname = usePathname();
  const today = new Date();
  const [isoYear, isoWeek] = getISOWeek(today);

  let viewMode = "month";
  if (pathname.startsWith("/week")) viewMode = "week";
  else if (pathname.startsWith("/day")) viewMode = "day";

  // Extract context date from the current URL for view switching
  let contextYear = today.getFullYear();
  let contextMonth = today.getMonth() + 1;
  let contextDay = today.getDate();
  let contextIsoYear = isoYear;
  let contextIsoWeek = isoWeek;

  const parts = pathname.split("/").filter(Boolean);
  if (viewMode === "month" && parts.length >= 3) {
    contextYear = parseInt(parts[1]);
    contextMonth = parseInt(parts[2]);
    contextDay = 15;
    const d = new Date(contextYear, contextMonth - 1, 15);
    [contextIsoYear, contextIsoWeek] = getISOWeek(d);
  } else if (viewMode === "week" && parts.length >= 3) {
    contextIsoYear = parseInt(parts[1]);
    contextIsoWeek = parseInt(parts[2]);
    // Derive calendar date from ISO week for Month/Day link context
    const representative = isoWeekToDate(contextIsoYear, contextIsoWeek);
    contextYear = representative.getFullYear();
    contextMonth = representative.getMonth() + 1;
    contextDay = representative.getDate();
  } else if (viewMode === "day" && parts.length >= 4) {
    contextYear = parseInt(parts[1]);
    contextMonth = parseInt(parts[2]);
    contextDay = parseInt(parts[3]);
    const d = new Date(contextYear, contextMonth - 1, contextDay);
    [contextIsoYear, contextIsoWeek] = getISOWeek(d);
  }

  let todayHref = `/month/${today.getFullYear()}/${today.getMonth() + 1}`;
  if (viewMode === "week") todayHref = `/week/${isoYear}/${isoWeek}`;
  else if (viewMode === "day") todayHref = `/day/${today.getFullYear()}/${today.getMonth() + 1}/${today.getDate()}`;

  return (
    <nav className="navbar">
      <Link href="/" className="nav-title">Calendar</Link>
      <div className="view-switcher">
        <Link
          href={`/month/${contextYear}/${contextMonth}`}
          className={`view-btn ${viewMode === "month" ? "active" : ""}`}
        >
          Month
        </Link>
        <Link
          href={`/week/${contextIsoYear}/${contextIsoWeek}`}
          className={`view-btn ${viewMode === "week" ? "active" : ""}`}
        >
          Week
        </Link>
        <Link
          href={`/day/${contextYear}/${contextMonth}/${contextDay}`}
          className={`view-btn ${viewMode === "day" ? "active" : ""}`}
        >
          Day
        </Link>
      </div>
      <Link href={todayHref} className="today-btn">Today</Link>
    </nav>
  );
}
