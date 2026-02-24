const express = require('express');
const expressLayouts = require('express-ejs-layouts');
const path = require('path');
const cal = require('./calendarUtils');

const app = express();
const PORT = 3000;

const PASSWORD = "Tp3!bZ8wNq6@yFm1";
const SECRET_KEY = "rV5&hJ3nLx9#Wk2Q";

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));
app.use(expressLayouts);
app.set('layout', 'layout');
app.use(express.static(path.join(__dirname, 'public')));

// Root: redirect to current month
app.get('/', (req, res) => {
  const today = new Date();
  res.redirect(`/month/${today.getFullYear()}/${today.getMonth() + 1}`);
});

// Month view
app.get('/month/:year/:month', (req, res) => {
  const year = parseInt(req.params.year);
  const month = parseInt(req.params.month);

  if (month < 1) return res.redirect(`/month/${year - 1}/12`);
  if (month > 12) return res.redirect(`/month/${year + 1}/1`);

  const weeks = cal.getMonthGrid(year, month);
  const prev = cal.prevMonth(year, month);
  const next = cal.nextMonth(year, month);
  const today = new Date();

  const ctxDate = new Date(year, month - 1, 15);
  const ctxIso = cal.getIsoWeek(ctxDate);

  res.render('month', {
    year,
    month,
    weeks,
    title: `${cal.monthName(month)} ${year}`,
    prevYear: prev[0],
    prevMonth: prev[1],
    nextYear: next[0],
    nextMonth: next[1],
    today,
    dayHeaders: cal.DAY_HEADERS,
    nav: cal.navContext('month', year, month, 15, ctxIso[0], ctxIso[1]),
    cal
  });
});

// Week view
app.get('/week/:year/:week', (req, res) => {
  const year = parseInt(req.params.year);
  const week = parseInt(req.params.week);

  if (week < 1 || week > 53 || year <= 0) return res.redirect('/');

  const dates = cal.getWeekDates(year, week);
  const today = new Date();
  const nowHour = new Date().getHours();
  const hours = cal.getHours();
  const weekLabel = cal.formatWeekLabel(dates);

  const prevDay = new Date(dates[0]);
  prevDay.setDate(prevDay.getDate() - 6);
  const prevIso = cal.getIsoWeek(prevDay);

  const nextDay = new Date(dates[0]);
  nextDay.setDate(nextDay.getDate() + 8);
  const nextIso = cal.getIsoWeek(nextDay);

  const representative = cal.isoWeekToDate(year, week);

  res.render('week', {
    year,
    week,
    dates,
    today,
    nowHour,
    hours,
    title: weekLabel,
    prevYear: prevIso[0],
    prevWeek: prevIso[1],
    nextYear: nextIso[0],
    nextWeek: nextIso[1],
    nav: cal.navContext('week', representative.getFullYear(), representative.getMonth() + 1,
      representative.getDate(), year, week),
    cal
  });
});

// Day view
app.get('/day/:year/:month/:day', (req, res) => {
  const year = parseInt(req.params.year);
  const month = parseInt(req.params.month);
  const day = parseInt(req.params.day);

  const d = new Date(year, month - 1, day);
  if (isNaN(d.getTime()) || d.getFullYear() !== year || d.getMonth() + 1 !== month || d.getDate() !== day) {
    return res.redirect('/');
  }

  const today = new Date();
  const isToday = cal.sameDay(d, today);
  const nowHour = new Date().getHours();
  const hours = cal.getHours();

  const prevDate = new Date(d);
  prevDate.setDate(prevDate.getDate() - 1);
  const nextDate = new Date(d);
  nextDate.setDate(nextDate.getDate() + 1);

  const ctxIso = cal.getIsoWeek(d);

  res.render('day', {
    date: d,
    year,
    month,
    day,
    isToday,
    nowHour,
    hours,
    title: cal.formatDate(d),
    dayNameStr: cal.dayName(d),
    prevDate,
    nextDate,
    nav: cal.navContext('day', year, month, day, ctxIso[0], ctxIso[1]),
    cal
  });
});

app.listen(PORT, () => {
  console.log(`Calendar app running at http://localhost:${PORT}`);
});
