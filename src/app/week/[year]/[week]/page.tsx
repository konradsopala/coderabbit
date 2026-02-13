import { redirect } from "next/navigation";
import Link from "next/link";
import CalHeader from "@/components/CalHeader";
import { getWeekDates, getHours, getISOWeek, isSameDay, dayAbbrev, formatWeekLabel } from "@/lib/calendar";

interface Props {
  params: Promise<{ year: string; week: string }>;
}

export default async function WeekPage({ params }: Props) {
  const { year: yearStr, week: weekStr } = await params;
  const year = parseInt(yearStr);
  const week = parseInt(weekStr);

  let dates: Date[];
  try {
    dates = getWeekDates(year, week);
  } catch {
    redirect("/");
  }

  const today = new Date();
  const nowHour = today.getHours();
  const hours = getHours();
  const weekLabel = formatWeekLabel(dates);

  const prevSun = new Date(dates[0]);
  prevSun.setDate(prevSun.getDate() - 7);
  const prevMon = new Date(prevSun);
  prevMon.setDate(prevSun.getDate() + 1);
  const [prevY, prevW] = getISOWeek(prevMon);

  const nextSun = new Date(dates[0]);
  nextSun.setDate(nextSun.getDate() + 7);
  const nextMon = new Date(nextSun);
  nextMon.setDate(nextSun.getDate() + 1);
  const [nextY, nextW] = getISOWeek(nextMon);

  return (
    <>
      <CalHeader
        prevHref={`/week/${prevY}/${prevW}`}
        nextHref={`/week/${nextY}/${nextW}`}
        title={weekLabel}
      />
      <table className="week-grid">
        <thead>
          <tr>
            <th></th>
            {dates.map((d) => {
              const isToday = isSameDay(d, today);
              return (
                <th key={d.toISOString()} className={isToday ? "today-col" : ""}>
                  <span className="week-day-name">{dayAbbrev(d)}</span>
                  <Link
                    href={`/day/${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()}`}
                    className="week-day-num"
                  >
                    {d.getDate()}
                  </Link>
                </th>
              );
            })}
          </tr>
        </thead>
        <tbody>
          {hours.map(({ hour, label }) => (
            <tr key={hour} className="hour-row">
              <td className="hour-label">{label}</td>
              {dates.map((d) => {
                const isToday = isSameDay(d, today);
                const classes = [
                  "time-slot",
                  isToday ? "today-col" : "",
                  isToday && hour === nowHour ? "current-hour" : "",
                ].filter(Boolean).join(" ");
                return <td key={d.toISOString()} className={classes}></td>;
              })}
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
