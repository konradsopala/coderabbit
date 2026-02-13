"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import type { DayInfo } from "@/lib/calendar";

interface DayCellProps {
  day: DayInfo;
}

export default function DayCell({ day }: DayCellProps) {
  const [isToday, setIsToday] = useState(false);

  useEffect(() => {
    const now = new Date();
    setIsToday(
      day.year === now.getFullYear() &&
      day.month === now.getMonth() + 1 &&
      day.day === now.getDate()
    );
  }, [day.year, day.month, day.day]);

  const classes = [
    "month-cell",
    !day.isCurrentMonth ? "other-month" : "",
    isToday ? "today" : "",
  ].filter(Boolean).join(" ");

  return (
    <td className={classes}>
      <Link
        href={`/day/${day.year}/${day.month}/${day.day}`}
        className={`day-num${isToday ? " today-num" : ""}`}
      >
        {day.day}
      </Link>
    </td>
  );
}
