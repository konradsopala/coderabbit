import SwiftUI

struct MonthView: View {
    @Bindable var model: CalendarModel

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 0), count: 7)

    private var year: Int { CalendarUtils.year(of: model.selectedDate) }
    private var month: Int { CalendarUtils.month(of: model.selectedDate) }

    private var weeks: [[DateInfo]] {
        CalendarUtils.monthGrid(year: year, month: month)
    }

    var body: some View {
        VStack(spacing: 0) {
            CalendarHeader(
                title: "\(CalendarUtils.monthName(month)) \(year)",
                onPrevious: { model.goToPreviousPeriod() },
                onNext: { model.goToNextPeriod() }
            )

            // Day-of-week headers
            HStack(spacing: 0) {
                ForEach(CalendarUtils.dayHeaders, id: \.self) { header in
                    Text(header)
                        .font(.caption)
                        .fontWeight(.semibold)
                        .foregroundStyle(.secondary)
                        .textCase(.uppercase)
                        .frame(maxWidth: .infinity)
                        .padding(.vertical, 8)
                }
            }

            // Month grid
            VStack(spacing: 0) {
                ForEach(Array(weeks.enumerated()), id: \.offset) { _, week in
                    HStack(spacing: 0) {
                        ForEach(week) { dayInfo in
                            DayCellView(dayInfo: dayInfo) {
                                model.navigateToDay(dayInfo.date)
                            }
                        }
                    }
                    Divider()
                }
            }
            .background(Color.white)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .shadow(color: .black.opacity(0.06), radius: 2, y: 1)
            .padding(.horizontal)

            Spacer()
        }
    }
}
