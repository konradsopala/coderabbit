import { redirect } from "next/navigation";
import CalHeader from "@/components/CalHeader";
import DayCell from "@/components/DayCell";
import { getMonthGrid, monthName, prevMonth, nextMonth } from "@/lib/calendar";

const DAY_HEADERS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];

interface Props {
  params: Promise<{ year: string; month: string }>;
}

export default async function MonthPage({ params }: Props) {
  const { year: yearStr, month: monthStr } = await params;
  const year = parseInt(yearStr);
  const month = parseInt(monthStr);

  if (!Number.isInteger(year) || !Number.isInteger(month)) {
    redirect("/");
  }

  if (month < 1) redirect(`/month/${year - 1}/12`);
  if (month > 12) redirect(`/month/${year + 1}/1`);

  const weeks = getMonthGrid(year, month);
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
              {week.map((d) => (
                <DayCell key={`${d.month}-${d.day}`} day={d} />
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
