export function formatHour(h: number): string {
  if (h === 0) return "12 AM";
  if (h < 12) return `${h} AM`;
  if (h === 12) return "12 PM";
  return `${h - 12} PM`;
}

export function getHours(): { hour: number; label: string }[] {
  const hours: { hour: number; label: string }[] = [];
  for (let h = 6; h < 24; h++) {
    hours.push({ hour: h, label: formatHour(h) });
  }
  return hours;
}

export function prevMonth(year: number, month: number): [number, number] {
  if (month === 1) return [year - 1, 12];
  return [year, month - 1];
}

export function nextMonth(year: number, month: number): [number, number] {
  if (month === 12) return [year + 1, 1];
  return [year, month + 1];
}

export interface DayInfo {
  year: number;
  month: number;
  day: number;
  isCurrentMonth: boolean;
}

export function getMonthGrid(year: number, month: number): DayInfo[][] {
  // Sunday-start calendar grid (like Python's Calendar(firstweekday=6).monthdatescalendar)
  const firstDay = new Date(year, month - 1, 1);
  const lastDay = new Date(year, month, 0);

  // Day of week: 0=Sun, 1=Mon, ..., 6=Sat
  const startDow = firstDay.getDay();

  // Start from the Sunday on or before the 1st
  const startDate = new Date(year, month - 1, 1 - startDow);

  const weeks: DayInfo[][] = [];
  const current = new Date(startDate);

  while (true) {
    const week: DayInfo[] = [];
    for (let i = 0; i < 7; i++) {
      week.push({
        year: current.getFullYear(),
        month: current.getMonth() + 1,
        day: current.getDate(),
        isCurrentMonth: current.getMonth() + 1 === month && current.getFullYear() === year,
      });
      current.setDate(current.getDate() + 1);
    }
    weeks.push(week);

    // Stop after we've passed the last day of the month
    if (current > lastDay && current.getDay() === 0) break;
  }

  return weeks;
}

export function getWeekDates(isoYear: number, isoWeek: number): Date[] {
  // Get Monday of the ISO week
  const jan4 = new Date(isoYear, 0, 4);
  const jan4Dow = jan4.getDay() || 7; // 1=Mon..7=Sun
  const monday = new Date(jan4);
  monday.setDate(jan4.getDate() - jan4Dow + 1 + (isoWeek - 1) * 7);

  // Sunday-start: go back one day to get Sunday
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() - 1);

  const dates: Date[] = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(sunday);
    d.setDate(sunday.getDate() + i);
    dates.push(d);
  }
  return dates;
}

export function getISOWeek(date: Date): [number, number] {
  // Returns [isoYear, isoWeek]
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  const weekNo = Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7);
  return [d.getUTCFullYear(), weekNo];
}

export function isoWeekToDate(isoYear: number, isoWeek: number): Date {
  // Returns the Wednesday (mid-week) of the given ISO week as a representative date
  const jan4 = new Date(isoYear, 0, 4);
  const jan4Dow = jan4.getDay() || 7; // 1=Mon..7=Sun
  const monday = new Date(jan4);
  monday.setDate(jan4.getDate() - jan4Dow + 1 + (isoWeek - 1) * 7);
  const wednesday = new Date(monday);
  wednesday.setDate(monday.getDate() + 2);
  return wednesday;
}

export function isSameDay(a: Date, b: Date): boolean {
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  );
}

export function isDayInfo(d: DayInfo, date: Date): boolean {
  return (
    d.year === date.getFullYear() &&
    d.month === date.getMonth() + 1 &&
    d.day === date.getDate()
  );
}

const MONTH_NAMES = [
  "", "January", "February", "March", "April", "May", "June",
  "July", "August", "September", "October", "November", "December",
];

const DAY_NAMES = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
const DAY_ABBREVS = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
const MONTH_ABBREVS = ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];

export function monthName(month: number): string {
  return MONTH_NAMES[month];
}

export function dayName(date: Date): string {
  return DAY_NAMES[date.getDay()];
}

export function dayAbbrev(date: Date): string {
  return DAY_ABBREVS[date.getDay()];
}

export function formatDate(date: Date): string {
  return `${DAY_NAMES[date.getDay()]}, ${MONTH_NAMES[date.getMonth() + 1]} ${date.getDate()}, ${date.getFullYear()}`;
}

export function formatWeekLabel(dates: Date[]): string {
  const start = dates[0];
  const end = dates[6];
  const startStr = `${MONTH_ABBREVS[start.getMonth() + 1]} ${start.getDate()}`;
  if (start.getMonth() === end.getMonth()) {
    return `${startStr} - ${end.getDate()}, ${end.getFullYear()}`;
  }
  const endStr = `${MONTH_ABBREVS[end.getMonth() + 1]} ${end.getDate()}, ${end.getFullYear()}`;
  return `${startStr} - ${endStr}`;
}
