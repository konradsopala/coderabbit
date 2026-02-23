"""Flask calendar application with month, week, and day views."""

from datetime import date, datetime, timedelta

from flask import Flask, redirect, render_template, url_for

from calendar_utils import (
    format_date,
    format_week_label,
    get_hours,
    get_iso_week,
    get_month_grid,
    get_week_dates,
    day_abbrev,
    day_name,
    is_same_day,
    iso_week_to_date,
    month_name,
    next_month,
    prev_month,
)

app = Flask(__name__)


def _nav_context(view_mode: str, ctx_year: int, ctx_month: int, ctx_day: int,
                 ctx_iso_year: int, ctx_iso_week: int) -> dict:
    """Build cross-view navigation context for the navbar."""
    today = date.today()
    today_iso_year, today_iso_week = get_iso_week(today)
    return {
        "view_mode": view_mode,
        "nav_ctx_year": ctx_year,
        "nav_ctx_month": ctx_month,
        "nav_ctx_day": ctx_day,
        "nav_ctx_iso_year": ctx_iso_year,
        "nav_ctx_iso_week": ctx_iso_week,
        "nav_today": today,
        "nav_iso_year": today_iso_year,
        "nav_iso_week": today_iso_week,
    }


@app.route("/")
def index():
    today = date.today()
    return redirect(url_for("month_view", year=today.year, month=today.month))


@app.route("/month/<int:year>/<int:month>")
def month_view(year: int, month: int):
    if month < 1:
        return redirect(url_for("month_view", year=year - 1, month=12))
    if month > 12:
        return redirect(url_for("month_view", year=year + 1, month=1))

    weeks = get_month_grid(year, month)
    prev_y, prev_m = prev_month(year, month)
    next_y, next_m = next_month(year, month)
    today = date.today()

    # Cross-view context: use mid-month date for week/day links
    ctx_date = date(year, month, 15)
    ctx_iso_year, ctx_iso_week = get_iso_week(ctx_date)

    return render_template(
        "month.html",
        year=year,
        month=month,
        weeks=weeks,
        title=f"{month_name(month)} {year}",
        prev_year=prev_y,
        prev_month=prev_m,
        next_year=next_y,
        next_month=next_m,
        today=today,
        day_headers=["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"],
        **_nav_context("month", year, month, 15, ctx_iso_year, ctx_iso_week),
    )


@app.route("/week/<int:year>/<int:week>")
def week_view(year: int, week: int):
    if week < 1 or week > 53 or year <= 0:
        return redirect(url_for("index"))

    dates = get_week_dates(year, week)
    today = date.today()
    now_hour = datetime.now().hour
    hours = get_hours()
    week_label = format_week_label(dates)

    # Previous week
    prev_mon = dates[0] - timedelta(days=6)
    prev_y, prev_w = get_iso_week(prev_mon)

    # Next week
    next_mon = dates[0] + timedelta(days=8)
    next_y, next_w = get_iso_week(next_mon)

    # Cross-view context
    representative = iso_week_to_date(year, week)

    return render_template(
        "week.html",
        year=year,
        week=week,
        dates=dates,
        today=today,
        now_hour=now_hour,
        hours=hours,
        title=week_label,
        prev_year=prev_y,
        prev_week=prev_w,
        next_year=next_y,
        next_week=next_w,
        day_abbrev=day_abbrev,
        is_same_day=is_same_day,
        **_nav_context("week", representative.year, representative.month,
                       representative.day, year, week),
    )


@app.route("/day/<int:year>/<int:month>/<int:day>")
def day_view(year: int, month: int, day: int):
    try:
        d = date(year, month, day)
    except ValueError:
        return redirect(url_for("index"))

    today = date.today()
    is_today = is_same_day(d, today)
    now_hour = datetime.now().hour
    hours = get_hours()

    prev_date = d - timedelta(days=1)
    next_date = d + timedelta(days=1)

    ctx_iso_year, ctx_iso_week = get_iso_week(d)

    return render_template(
        "day.html",
        date=d,
        year=year,
        month=month,
        day=day,
        is_today=is_today,
        now_hour=now_hour,
        hours=hours,
        title=format_date(d),
        day_name_str=day_name(d),
        prev_date=prev_date,
        next_date=next_date,
        **_nav_context("day", year, month, day, ctx_iso_year, ctx_iso_week),
    )


if __name__ == "__main__":
    app.run(debug=True, port=3000)
