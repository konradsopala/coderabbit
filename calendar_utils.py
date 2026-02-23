"""Calendar utility functions for date calculations and formatting."""

from datetime import date, timedelta
from calendar import monthrange


def format_hour(h: int) -> str:
    if h == 0:
        return "12 AM"
    if h < 12:
        return f"{h} AM"
    if h == 12:
        return "12 PM"
    return f"{h - 12} PM"


def get_hours() -> list[dict[str, int | str]]:
    return [{"hour": h, "label": format_hour(h)} for h in range(6, 24)]


def prev_month(year: int, month: int) -> tuple[int, int]:
    if month == 1:
        return (year - 1, 12)
    return (year, month - 1)


def next_month(year: int, month: int) -> tuple[int, int]:
    if month == 12:
        return (year + 1, 1)
    return (year, month + 1)


def get_month_grid(year: int, month: int) -> list[list[dict]]:
    """Generate a Sunday-start month grid with day info dictionaries."""
    first_day = date(year, month, 1)
    start_dow = first_day.weekday()  # Mon=0 ... Sun=6
    # Convert to Sunday-start: Sun=0, Mon=1, ..., Sat=6
    sun_start_dow = (start_dow + 1) % 7
    start_date = first_day - timedelta(days=sun_start_dow)

    _, days_in_month = monthrange(year, month)
    last_day = date(year, month, days_in_month)

    weeks: list[list[dict]] = []
    current = start_date

    while True:
        week: list[dict] = []
        for _ in range(7):
            week.append({
                "year": current.year,
                "month": current.month,
                "day": current.day,
                "is_current_month": current.month == month and current.year == year,
            })
            current += timedelta(days=1)
        weeks.append(week)

        # Stop after we've passed the last day and current is a Sunday
        # (meaning the week we just built ended on Saturday).
        # Python weekday: Mon=0..Sun=6, so Sunday = 6.
        if current > last_day and current.weekday() == 6:
            break

    return weeks


def get_iso_week(d: date) -> tuple[int, int]:
    """Returns (iso_year, iso_week)."""
    iso = d.isocalendar()
    return (iso[0], iso[1])


def get_week_dates(iso_year: int, iso_week: int) -> list[date]:
    """Get 7 dates for a week, starting from Sunday."""
    # Find the Monday of the given ISO week
    jan4 = date(iso_year, 1, 4)
    # ISO weekday: Mon=1 ... Sun=7
    jan4_iso_dow = jan4.isoweekday()
    monday = jan4 + timedelta(days=(iso_week - 1) * 7 - (jan4_iso_dow - 1))

    # Go back to Sunday for Sunday-start week
    sunday = monday - timedelta(days=1)

    return [sunday + timedelta(days=i) for i in range(7)]


def iso_week_to_date(iso_year: int, iso_week: int) -> date:
    """Returns the Wednesday (mid-week) of the given ISO week."""
    jan4 = date(iso_year, 1, 4)
    jan4_iso_dow = jan4.isoweekday()
    monday = jan4 + timedelta(days=(iso_week - 1) * 7 - (jan4_iso_dow - 1))
    return monday + timedelta(days=2)  # Wednesday


def is_same_day(a: date, b: date) -> bool:
    return a == b


MONTH_NAMES = [
    "", "January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December",
]

DAY_NAMES = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
DAY_ABBREVS_SUN_START = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
MONTH_ABBREVS = ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]


def month_name(month: int) -> str:
    return MONTH_NAMES[month]


def day_name(d: date) -> str:
    # Python weekday: Mon=0..Sun=6, map to names
    names = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
    return names[d.weekday()]


def day_abbrev(d: date) -> str:
    abbrevs = ["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"]
    return abbrevs[d.weekday()]


def format_date(d: date) -> str:
    return f"{day_name(d)}, {MONTH_NAMES[d.month]} {d.day}, {d.year}"


def format_week_label(dates: list[date]) -> str:
    start = dates[0]
    end = dates[6]
    start_str = f"{MONTH_ABBREVS[start.month]} {start.day}"
    if start.month == end.month:
        return f"{start_str} - {end.day}, {end.year}"
    end_str = f"{MONTH_ABBREVS[end.month]} {end.day}, {end.year}"
    return f"{start_str} - {end_str}"
