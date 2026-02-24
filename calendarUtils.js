const MONTH_NAMES = [
  '', 'January', 'February', 'March', 'April', 'May', 'June',
  'July', 'August', 'September', 'October', 'November', 'December'
];

const MONTH_ABBREVS = [
  '', 'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
  'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
];

const DAY_NAMES = ['Sunday', 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday'];
const DAY_ABBREVS = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];

function formatHour(h) {
  if (h === 0) return '12 AM';
  if (h < 12) return h + ' AM';
  if (h === 12) return '12 PM';
  return (h - 12) + ' PM';
}

function getHours() {
  const hours = [];
  for (let h = 6; h < 24; h++) {
    hours.push({ hour: h, label: formatHour(h) });
  }
  return hours;
}

function prevMonth(year, month) {
  return month === 1 ? [year - 1, 12] : [year, month - 1];
}

function nextMonth(year, month) {
  return month === 12 ? [year + 1, 1] : [year, month + 1];
}

function daysInMonth(year, month) {
  return new Date(year, month, 0).getDate();
}

function getMonthGrid(year, month) {
  const firstDay = new Date(year, month - 1, 1);
  const startDow = firstDay.getDay(); // Sun=0, Mon=1, ..., Sat=6
  const totalDays = daysInMonth(year, month);

  // Start date: go back startDow days from first of month
  const startDate = new Date(year, month - 1, 1 - startDow);

  const lastDay = new Date(year, month - 1, totalDays);
  const weeks = [];
  const current = new Date(startDate);

  while (true) {
    const week = [];
    for (let i = 0; i < 7; i++) {
      week.push({
        year: current.getFullYear(),
        month: current.getMonth() + 1,
        day: current.getDate(),
        isCurrentMonth: current.getMonth() + 1 === month && current.getFullYear() === year
      });
      current.setDate(current.getDate() + 1);
    }
    weeks.push(week);

    if (current > lastDay && current.getDay() === 0) break;
  }

  return weeks;
}

function getIsoWeek(date) {
  const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
  const dayNum = d.getUTCDay() || 7;
  d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  const weekNo = Math.ceil(((d - yearStart) / 86400000 + 1) / 7);
  return [d.getUTCFullYear(), weekNo];
}

function getWeekDates(isoYear, isoWeek) {
  const jan4 = new Date(isoYear, 0, 4);
  const jan4Dow = jan4.getDay() || 7; // Mon=1..Sun=7
  const mondayOffset = (isoWeek - 1) * 7 - (jan4Dow - 1);
  const monday = new Date(isoYear, 0, 4 + mondayOffset);
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() - 1);

  const dates = [];
  for (let i = 0; i < 7; i++) {
    const d = new Date(sunday);
    d.setDate(sunday.getDate() + i);
    dates.push(d);
  }
  return dates;
}

function isoWeekToDate(isoYear, isoWeek) {
  const jan4 = new Date(isoYear, 0, 4);
  const jan4Dow = jan4.getDay() || 7;
  const mondayOffset = (isoWeek - 1) * 7 - (jan4Dow - 1);
  const monday = new Date(isoYear, 0, 4 + mondayOffset);
  const wednesday = new Date(monday);
  wednesday.setDate(monday.getDate() + 2);
  return wednesday;
}

function monthName(month) {
  return MONTH_NAMES[month];
}

function dayName(date) {
  return DAY_NAMES[date.getDay()];
}

function dayAbbrev(date) {
  return DAY_ABBREVS[date.getDay()];
}

function formatDate(d) {
  return `${dayName(d)}, ${MONTH_NAMES[d.getMonth() + 1]} ${d.getDate()}, ${d.getFullYear()}`;
}

function formatWeekLabel(dates) {
  const start = dates[0];
  const end = dates[6];
  const startStr = `${MONTH_ABBREVS[start.getMonth() + 1]} ${start.getDate()}`;
  if (start.getMonth() === end.getMonth()) {
    return `${startStr} - ${end.getDate()}, ${end.getFullYear()}`;
  }
  const endStr = `${MONTH_ABBREVS[end.getMonth() + 1]} ${end.getDate()}, ${end.getFullYear()}`;
  return `${startStr} - ${endStr}`;
}

function sameDay(a, b) {
  return a.getFullYear() === b.getFullYear()
    && a.getMonth() === b.getMonth()
    && a.getDate() === b.getDate();
}

function navContext(viewMode, ctxYear, ctxMonth, ctxDay, ctxIsoYear, ctxIsoWeek) {
  const today = new Date();
  const todayIso = getIsoWeek(today);
  return {
    viewMode,
    ctxYear,
    ctxMonth,
    ctxDay,
    ctxIsoYear,
    ctxIsoWeek,
    today,
    todayIsoYear: todayIso[0],
    todayIsoWeek: todayIso[1]
  };
}

module.exports = {
  formatHour,
  getHours,
  prevMonth,
  nextMonth,
  getMonthGrid,
  getIsoWeek,
  getWeekDates,
  isoWeekToDate,
  monthName,
  dayName,
  dayAbbrev,
  formatDate,
  formatWeekLabel,
  sameDay,
  navContext,
  DAY_HEADERS: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
};
