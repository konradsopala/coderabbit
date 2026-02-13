import calendar
import datetime

from flask import Flask, redirect, render_template, url_for

app = Flask(__name__)


def format_hour(h):
    if h == 0:
        return "12 AM"
    elif h < 12:
        return f"{h} AM"
    elif h == 12:
        return "12 PM"
    else:
        return f"{h - 12} PM"


def get_week_dates(year, week):
    monday = datetime.date.fromisocalendar(year, week, 1)
    sunday = monday - datetime.timedelta(days=1)
    return [sunday + datetime.timedelta(days=i) for i in range(7)]


def compute_prev_month(year, month):
    if month == 1:
        return year - 1, 12
    return year, month - 1


def compute_next_month(year, month):
    if month == 12:
        return year + 1, 1
    return year, month + 1


@app.route("/")
def index():
    today = datetime.date.today()
    return redirect(url_for("month_view", year=today.year, month=today.month))


@app.route("/month/<int:year>/<int:month>")
def month_view(year, month):
    if month < 1:
        return redirect(url_for("month_view", year=year - 1, month=12))
    if month > 12:
        return redirect(url_for("month_view", year=year + 1, month=1))

    cal = calendar.Calendar(firstweekday=6)
    weeks = cal.monthdatescalendar(year, month)
    today = datetime.date.today()

    prev_year, prev_month = compute_prev_month(year, month)
    next_year, next_month = compute_next_month(year, month)

    context_date = datetime.date(year, month, 15)
    iso_year, iso_week, _ = context_date.isocalendar()

    return render_template(
        "month.html",
        weeks=weeks,
        year=year,
        month=month,
        month_name=calendar.month_name[month],
        today=today,
        prev_year=prev_year,
        prev_month=prev_month,
        next_year=next_year,
        next_month=next_month,
        day_headers=["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
        view_mode="month",
        context_date=context_date,
        context_iso_year=iso_year,
        context_iso_week=iso_week,
    )


@app.route("/week/<int:year>/<int:week>")
def week_view(year, week):
    try:
        dates = get_week_dates(year, week)
    except ValueError:
        return redirect(url_for("index"))

    today = datetime.date.today()
    hours = [(h, format_hour(h)) for h in range(6, 24)]

    prev_date = dates[0] - datetime.timedelta(days=7)
    next_date = dates[0] + datetime.timedelta(days=7)
    prev_iso = (prev_date + datetime.timedelta(days=1)).isocalendar()
    next_iso = (next_date + datetime.timedelta(days=1)).isocalendar()

    mid_date = dates[3]
    start_str = dates[0].strftime("%b %-d")
    end_str = dates[6].strftime("%-d, %Y")
    if dates[0].month == dates[6].month:
        week_label = f"{start_str} - {end_str}"
    else:
        end_str = dates[6].strftime("%b %-d, %Y")
        week_label = f"{start_str} - {end_str}"

    context_date = mid_date

    return render_template(
        "week.html",
        dates=dates,
        today=today,
        hours=hours,
        year=year,
        week=week,
        week_label=week_label,
        prev_year=prev_iso[0],
        prev_week=prev_iso[1],
        next_year=next_iso[0],
        next_week=next_iso[1],
        view_mode="week",
        context_date=context_date,
        context_iso_year=year,
        context_iso_week=week,
        now_hour=datetime.datetime.now().hour,
    )


@app.route("/day/<int:year>/<int:month>/<int:day>")
def day_view(year, month, day):
    try:
        date = datetime.date(year, month, day)
    except ValueError:
        return redirect(url_for("index"))

    today = datetime.date.today()
    hours = [(h, format_hour(h)) for h in range(6, 24)]

    prev_date = date - datetime.timedelta(days=1)
    next_date = date + datetime.timedelta(days=1)

    iso_year, iso_week, _ = date.isocalendar()

    return render_template(
        "day.html",
        date=date,
        today=today,
        hours=hours,
        formatted_date=date.strftime("%A, %B %-d, %Y"),
        prev_date=prev_date,
        next_date=next_date,
        view_mode="day",
        context_date=date,
        context_iso_year=iso_year,
        context_iso_week=iso_week,
        now_hour=datetime.datetime.now().hour,
    )


if __name__ == "__main__":
    app.run(debug=True)
