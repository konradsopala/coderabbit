import Foundation
import Observation

@Observable
class CalendarModel {
    var selectedDate: Date = .now
    var viewMode: ViewMode = .month

    enum ViewMode: String, CaseIterable, Identifiable {
        case month = "Month"
        case week = "Week"
        case day = "Day"

        var id: String { rawValue }
    }

    func goToPreviousPeriod() {
        switch viewMode {
        case .month:
            selectedDate = CalendarUtils.addMonths(-1, to: selectedDate)
        case .week:
            selectedDate = CalendarUtils.addWeeks(-1, to: selectedDate)
        case .day:
            selectedDate = CalendarUtils.addDays(-1, to: selectedDate)
        }
    }

    func goToNextPeriod() {
        switch viewMode {
        case .month:
            selectedDate = CalendarUtils.addMonths(1, to: selectedDate)
        case .week:
            selectedDate = CalendarUtils.addWeeks(1, to: selectedDate)
        case .day:
            selectedDate = CalendarUtils.addDays(1, to: selectedDate)
        }
    }

    func goToToday() {
        selectedDate = .now
    }

    func navigateToDay(_ date: Date) {
        selectedDate = date
        viewMode = .day
    }
}
