import Foundation

struct DateInfo: Identifiable {
    let date: Date
    let day: Int
    let month: Int
    let year: Int
    let isCurrentMonth: Bool

    var id: String { "\(year)-\(month)-\(day)" }
}

struct HourInfo: Identifiable {
    let hour: Int
    let label: String

    var id: Int { hour }
}

/// Wraps a week (array of DateInfo) with a stable identifier for use in ForEach.
struct WeekRow: Identifiable {
    let days: [DateInfo]

    /// Stable ID derived from the first day in the week.
    var id: String { days.first?.id ?? "" }
}

/// Wraps a Date with a stable, Hashable identifier for use in ForEach.
struct IdentifiableDate: Identifiable {
    let date: Date
    var id: TimeInterval { date.timeIntervalSince1970 }
}

enum CalendarUtils {
    private static let calendar: Calendar = {
        var cal = Calendar(identifier: .gregorian)
        cal.firstWeekday = Calendar.current.firstWeekday
        cal.locale = Locale.current
        return cal
    }()

    // MARK: - Locale-Aware Formatters

    private static let dateFormatter: DateFormatter = {
        let df = DateFormatter()
        df.locale = Locale.current
        df.calendar = calendar
        return df
    }()

    // MARK: - Hour Formatting

    static func formatHour(_ h: Int) -> String {
        if h == 0 { return "12 AM" }
        if h < 12 { return "\(h) AM" }
        if h == 12 { return "12 PM" }
        return "\(h - 12) PM"
    }

    static let hours: [HourInfo] = (0..<24).map { HourInfo(hour: $0, label: formatHour($0)) }

    // MARK: - Date Comparisons

    static func isToday(_ date: Date) -> Bool {
        calendar.isDateInToday(date)
    }

    static func isSameDay(_ a: Date, _ b: Date) -> Bool {
        calendar.isDate(a, inSameDayAs: b)
    }

    // MARK: - Month Grid

    static func monthGrid(year: Int, month: Int) -> [WeekRow] {
        let cal = calendar
        var components = DateComponents()
        components.year = year
        components.month = month
        components.day = 1

        guard let firstDay = cal.date(from: components) else { return [] }
        guard let range = cal.range(of: .day, in: .month, for: firstDay) else { return [] }
        let lastDay = range.count

        // Day of week for the 1st, adjusted for locale's first weekday
        let firstWeekday = cal.component(.weekday, from: firstDay)
        let firstWd = cal.firstWeekday
        let leadingDays = (firstWeekday - firstWd + 7) % 7

        guard let startDate = cal.date(byAdding: .day, value: -leadingDays, to: firstDay) else { return [] }

        let totalDays = leadingDays + lastDay
        let totalCells = totalDays + (7 - totalDays % 7) % 7

        var weeks: [WeekRow] = []
        var currentDate = startDate

        for _ in stride(from: 0, to: totalCells, by: 7) {
            var week: [DateInfo] = []
            for _ in 0..<7 {
                let comps = cal.dateComponents([.year, .month, .day], from: currentDate)
                week.append(DateInfo(
                    date: currentDate,
                    day: comps.day ?? 1,
                    month: comps.month ?? 1,
                    year: comps.year ?? year,
                    isCurrentMonth: comps.month == month && comps.year == year
                ))
                currentDate = cal.date(byAdding: .day, value: 1, to: currentDate) ?? currentDate
            }
            weeks.append(WeekRow(days: week))
        }

        return weeks
    }

    // MARK: - Week Dates

    static func weekDates(for date: Date) -> [IdentifiableDate] {
        let weekday = calendar.component(.weekday, from: date)
        let firstWd = calendar.firstWeekday
        let daysToSubtract = (weekday - firstWd + 7) % 7
        guard let weekStart = calendar.date(byAdding: .day, value: -daysToSubtract, to: date) else { return [] }

        return (0..<7).compactMap { offset in
            guard let d = calendar.date(byAdding: .day, value: offset, to: weekStart) else { return nil }
            return IdentifiableDate(date: d)
        }
    }

    // MARK: - Date Components

    static func year(of date: Date) -> Int {
        calendar.component(.year, from: date)
    }

    static func month(of date: Date) -> Int {
        calendar.component(.month, from: date)
    }

    static func day(of date: Date) -> Int {
        calendar.component(.day, from: date)
    }

    static func currentHour() -> Int {
        calendar.component(.hour, from: Date())
    }

    // MARK: - Locale-Aware Formatting

    /// Localized short weekday names ordered by the calendar's first weekday.
    static var dayHeaders: [String] {
        let symbols = dateFormatter.shortWeekdaySymbols ?? ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
        let firstWd = calendar.firstWeekday // 1=Sun in Gregorian
        // Rotate symbols so they start from firstWeekday
        let offset = firstWd - 1
        return Array(symbols[offset...]) + Array(symbols[..<offset])
    }

    /// Localized standalone month name (e.g., "February" or "Februar").
    static func monthName(_ month: Int) -> String {
        guard month >= 1, month <= 12 else { return "" }
        let symbols = dateFormatter.standaloneMonthSymbols ?? []
        guard month - 1 < symbols.count else { return "" }
        return symbols[month - 1]
    }

    /// Localized full weekday name for a date (e.g., "Tuesday" or "Dienstag").
    static func dayName(of date: Date) -> String {
        let weekday = calendar.component(.weekday, from: date) // 1-based
        let symbols = dateFormatter.weekdaySymbols ?? []
        guard weekday - 1 < symbols.count else { return "" }
        return symbols[weekday - 1]
    }

    /// Localized abbreviated weekday name (e.g., "Tue" or "Di").
    static func dayAbbrev(of date: Date) -> String {
        let weekday = calendar.component(.weekday, from: date)
        let symbols = dateFormatter.shortWeekdaySymbols ?? []
        guard weekday - 1 < symbols.count else { return "" }
        return symbols[weekday - 1]
    }

    /// Localized full date string (e.g., "Tuesday, February 13, 2026").
    static func formatDate(_ date: Date) -> String {
        let df = DateFormatter()
        df.locale = Locale.current
        df.dateStyle = .full
        return df.string(from: date)
    }

    /// Localized full date string for accessibility (e.g., "Tuesday, February 5, 2026").
    static func accessibilityDateLabel(for date: Date) -> String {
        formatDate(date)
    }

    static func formatWeekLabel(dates: [IdentifiableDate]) -> String {
        guard dates.count >= 7 else { return "" }
        let start = dates[0].date
        let end = dates[6].date
        let sm = month(of: start)
        let em = month(of: end)
        let sd = day(of: start)
        let ed = day(of: end)
        let ey = year(of: end)

        let shortMonths = dateFormatter.shortMonthSymbols ?? []
        let startAbbrev = sm - 1 < shortMonths.count ? shortMonths[sm - 1] : ""
        let endAbbrev = em - 1 < shortMonths.count ? shortMonths[em - 1] : ""

        if sm == em {
            return "\(startAbbrev) \(sd) – \(ed), \(ey)"
        }
        return "\(startAbbrev) \(sd) – \(endAbbrev) \(ed), \(ey)"
    }

    // MARK: - Navigation

    static func addMonths(_ count: Int, to date: Date) -> Date {
        calendar.date(byAdding: .month, value: count, to: date) ?? date
    }

    static func addWeeks(_ count: Int, to date: Date) -> Date {
        calendar.date(byAdding: .weekOfYear, value: count, to: date) ?? date
    }

    static func addDays(_ count: Int, to date: Date) -> Date {
        calendar.date(byAdding: .day, value: count, to: date) ?? date
    }

    static func startOfMonth(_ date: Date) -> Date {
        let comps = calendar.dateComponents([.year, .month], from: date)
        return calendar.date(from: comps) ?? date
    }
}
