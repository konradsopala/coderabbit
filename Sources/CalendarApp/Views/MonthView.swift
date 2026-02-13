import SwiftUI

struct MonthView: View {
    @Bindable var model: CalendarModel

    private var year: Int { CalendarUtils.year(of: model.selectedDate) }
    private var month: Int { CalendarUtils.month(of: model.selectedDate) }

    private var weeks: [WeekRow] {
        CalendarUtils.monthGrid(year: year, month: month)
    }

    private let columns = Array(repeating: GridItem(.flexible(), spacing: 0), count: 7)

    var body: some View {
        VStack(spacing: 0) {
            CalendarHeader(
                title: "\(CalendarUtils.monthName(month)) \(year)",
                onPrevious: { model.goToPreviousPeriod() },
                onNext: { model.goToNextPeriod() }
            )

            // Day-of-week headers
            LazyVGrid(columns: columns, spacing: 0) {
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

            // Month grid using LazyVGrid with stable WeekRow IDs
            VStack(spacing: 0) {
                ForEach(weeks) { weekRow in
                    LazyVGrid(columns: columns, spacing: 0) {
                        ForEach(weekRow.days) { dayInfo in
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
