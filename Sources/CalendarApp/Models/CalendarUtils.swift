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

enum CalendarUtils {
    private static let calendar: Calendar = {
        var cal = Calendar(identifier: .gregorian)
        cal.firstWeekday = 1 // Sunday
        return cal
    }()

    // MARK: - Hour Formatting

    static func formatHour(_ h: Int) -> String {
        if h == 0 { return "12 AM" }
        if h < 12 { return "\(h) AM" }
        if h == 12 { return "12 PM" }
        return "\(h - 12) PM"
    }

    static let hours: [HourInfo] = (6..<24).map { HourInfo(hour: $0, label: formatHour($0)) }

    // MARK: - Date Comparisons

    static func isToday(_ date: Date) -> Bool {
        calendar.isDateInToday(date)
    }

    static func isSameDay(_ a: Date, _ b: Date) -> Bool {
        calendar.isDate(a, inSameDayAs: b)
    }

    // MARK: - Month Grid

    static func monthGrid(year: Int, month: Int) -> [[DateInfo]] {
        let cal = calendar
        var components = DateComponents()
        components.year = year
        components.month = month
        components.day = 1

        guard let firstDay = cal.date(from: components) else { return [] }
        guard let range = cal.range(of: .day, in: .month, for: firstDay) else { return [] }
        let lastDay = range.count

        // Day of week for the 1st: 1=Sun, 2=Mon, ..., 7=Sat
        let firstWeekday = cal.component(.weekday, from: firstDay)
        // Number of leading days from previous month (Sunday start)
        let leadingDays = firstWeekday - 1

        // Build start date (Sunday before or on the 1st)
        guard let startDate = cal.date(byAdding: .day, value: -leadingDays, to: firstDay) else { return [] }

        // Total cells: enough to cover all days plus trailing to complete the last week
        let totalDays = leadingDays + lastDay
        let totalCells = totalDays + (7 - totalDays % 7) % 7

        var weeks: [[DateInfo]] = []
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
            weeks.append(week)
        }

        return weeks
    }

    // MARK: - Week Dates

    static func weekDates(for date: Date) -> [Date] {
        // Get Sunday of the week containing the given date
        let weekday = calendar.component(.weekday, from: date) // 1=Sun..7=Sat
        let daysToSubtract = weekday - 1
        guard let sunday = calendar.date(byAdding: .day, value: -daysToSubtract, to: date) else { return [] }

        return (0..<7).compactMap { calendar.date(byAdding: .day, value: $0, to: sunday) }
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

    // MARK: - Formatting

    private static let monthNames = [
        "", "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    ]

    private static let dayNames = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"]
    private static let dayAbbrevs = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"]
    private static let monthAbbrevs = ["", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"]

    static let dayHeaders = dayAbbrevs

    static func monthName(_ month: Int) -> String {
        guard month >= 1 && month <= 12 else { return "" }
        return monthNames[month]
    }

    static func dayName(of date: Date) -> String {
        let weekday = calendar.component(.weekday, from: date) // 1=Sun..7=Sat
        return dayNames[weekday - 1]
    }

    static func dayAbbrev(of date: Date) -> String {
        let weekday = calendar.component(.weekday, from: date)
        return dayAbbrevs[weekday - 1]
    }

    static func formatDate(_ date: Date) -> String {
        let y = year(of: date)
        let m = month(of: date)
        let d = day(of: date)
        let name = dayName(of: date)
        let mName = monthNames[m]
        return "\(name), \(mName) \(d), \(y)"
    }

    static func formatWeekLabel(dates: [Date]) -> String {
        guard dates.count >= 7 else { return "" }
        let start = dates[0]
        let end = dates[6]
        let sm = month(of: start)
        let em = month(of: end)
        let sd = day(of: start)
        let ed = day(of: end)
        let ey = year(of: end)

        if sm == em {
            return "\(monthAbbrevs[sm]) \(sd) – \(ed), \(ey)"
        }
        return "\(monthAbbrevs[sm]) \(sd) – \(monthAbbrevs[em]) \(ed), \(ey)"
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
