import { redirect } from "next/navigation";
import CalHeader from "@/components/CalHeader";
import { getHours, formatDate, dayName, isSameDay } from "@/lib/calendar";

interface Props {
  params: Promise<{ year: string; month: string; day: string }>;
}

export default async function DayPage({ params }: Props) {
  const { year: yearStr, month: monthStr, day: dayStr } = await params;
  const year = parseInt(yearStr);
  const month = parseInt(monthStr);
  const day = parseInt(dayStr);

  // Validate date
  const date = new Date(year, month - 1, day);
  if (
    date.getFullYear() !== year ||
    date.getMonth() + 1 !== month ||
    date.getDate() !== day
  ) {
    redirect("/");
  }

  const today = new Date();
  const isToday = isSameDay(date, today);
  const nowHour = today.getHours();
  const hours = getHours();

  const prevDate = new Date(year, month - 1, day - 1);
  const nextDate = new Date(year, month - 1, day + 1);

  return (
    <>
      <CalHeader
        prevHref={`/day/${prevDate.getFullYear()}/${prevDate.getMonth() + 1}/${prevDate.getDate()}`}
        nextHref={`/day/${nextDate.getFullYear()}/${nextDate.getMonth() + 1}/${nextDate.getDate()}`}
        title={formatDate(date)}
      />
      <div className={`day-header-badge${isToday ? " is-today" : ""}`}>
        <span className="day-name-label">{dayName(date)}</span>
        <br />
        <span className="day-number-label">{date.getDate()}</span>
      </div>
      <table className="day-grid">
        <tbody>
          {hours.map(({ hour, label }) => {
            const classes = [
              "time-slot",
              isToday && hour === nowHour ? "current-hour" : "",
            ].filter(Boolean).join(" ");
            return (
              <tr key={hour}>
                <td className="hour-label">{label}</td>
                <td className={classes}></td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </>
  );
}
