import { redirect } from "next/navigation";
import Link from "next/link";
import CalHeader from "@/components/CalHeader";
import { getMonthGrid, monthName, prevMonth, nextMonth, isDayInfo } from "@/lib/calendar";

const DAY_HEADERS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

interface Props {
  params: Promise<{ year: string; month: string }>;
}

export default async function MonthPage({ params }: Props) {
  const { year: yearStr, month: monthStr } = await params;
  const year = parseInt(yearStr);
  const month = parseInt(monthStr);

  if (month < 1) redirect(`/month/${year - 1}/12`);
  if (month > 12) redirect(`/month/${year + 1}/1`);

  const weeks = getMonthGrid(year, month);
  const today = new Date();
  const [prevY, prevM] = prevMonth(year, month);
  const [nextY, nextM] = nextMonth(year, month);

  return (
    <>
      <CalHeader
        prevHref={`/month/${prevY}/${prevM}`}
        nextHref={`/month/${nextY}/${nextM}`}
        title={`${monthName(month)} ${year}`}
      />
      <table className="month-grid">
        <thead>
          <tr>
            {DAY_HEADERS.map((d) => (
              <th key={d}>{d}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {weeks.map((week, wi) => (
            <tr key={wi}>
              {week.map((d) => {
                const isToday = isDayInfo(d, today);
                const classes = [
                  "month-cell",
                  !d.isCurrentMonth ? "other-month" : "",
                  isToday ? "today" : "",
                ].filter(Boolean).join(" ");

                return (
                  <td key={`${d.month}-${d.day}`} className={classes}>
                    <Link
                      href={`/day/${d.year}/${d.month}/${d.day}`}
                      className={`day-num${isToday ? " today-num" : ""}`}
                    >
                      {d.day}
                    </Link>
                  </td>
                );
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
