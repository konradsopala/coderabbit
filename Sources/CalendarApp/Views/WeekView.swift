import SwiftUI

struct WeekView: View {
    @Bindable var model: CalendarModel

    private static let primaryColor = Color(red: 0.1, green: 0.45, blue: 0.91)
    private static let primaryLight = Color(red: 0.91, green: 0.94, blue: 0.99)

    private var dates: [IdentifiableDate] {
        CalendarUtils.weekDates(for: model.selectedDate)
    }

    private var weekLabel: String {
        CalendarUtils.formatWeekLabel(dates: dates)
    }

    var body: some View {
        VStack(spacing: 0) {
            CalendarHeader(
                title: weekLabel,
                onPrevious: { model.goToPreviousPeriod() },
                onNext: { model.goToNextPeriod() }
            )

            // TimelineView updates every minute for current-hour tracking
            TimelineView(.everyMinute) { context in
                let nowHour = Calendar.current.component(.hour, from: context.date)

                ScrollView {
                    VStack(spacing: 0) {
                        // Day column headers
                        weekColumnHeaders

                        Divider()

                        // Hour rows
                        ForEach(CalendarUtils.hours) { hourInfo in
                            WeekHourRow(
                                hourInfo: hourInfo,
                                dates: dates,
                                currentHour: nowHour,
                                primaryLight: Self.primaryLight
                            )
                        }
                    }
                    .background(Color.white)
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .shadow(color: .black.opacity(0.06), radius: 2, y: 1)
                    .padding(.horizontal)
                }
            }
        }
    }

    private var weekColumnHeaders: some View {
        HStack(spacing: 0) {
            Text("")
                .frame(width: 56)

            ForEach(dates) { identDate in
                WeekDayHeader(
                    date: identDate.date,
                    primaryColor: Self.primaryColor,
                    onTap: { model.navigateToDay(identDate.date) }
                )
            }
        }
        .padding(.vertical, 8)
    }
}

// MARK: - Week Day Header

private struct WeekDayHeader: View {
    let date: Date
    let primaryColor: Color
    let onTap: () -> Void

    private var isToday: Bool { CalendarUtils.isToday(date) }

    var body: some View {
        VStack(spacing: 4) {
            Text(CalendarUtils.dayAbbrev(of: date))
                .font(.system(size: 11))
                .fontWeight(.medium)
                .textCase(.uppercase)
                .foregroundStyle(isToday ? primaryColor : .secondary)

            Text("\(CalendarUtils.day(of: date))")
                .font(.system(size: 16))
                .fontWeight(.medium)
                .foregroundStyle(isToday ? .white : .primary)
                .frame(width: 32, height: 32)
                .background(
                    Circle()
                        .fill(isToday ? primaryColor : .clear)
                )
        }
        .frame(maxWidth: .infinity)
        .onTapGesture(perform: onTap)
    }
}

// MARK: - Week Hour Row

private struct WeekHourRow: View {
    let hourInfo: HourInfo
    let dates: [IdentifiableDate]
    let currentHour: Int
    let primaryLight: Color

    var body: some View {
        HStack(spacing: 0) {
            Text(hourInfo.label)
                .font(.system(size: 11))
                .foregroundStyle(.secondary)
                .frame(width: 56, alignment: .trailing)
                .padding(.trailing, 8)

            ForEach(dates) { identDate in
                WeekTimeSlot(
                    date: identDate.date,
                    hour: hourInfo.hour,
                    currentHour: currentHour,
                    primaryLight: primaryLight
                )
            }
        }
    }
}

// MARK: - Week Time Slot

private struct WeekTimeSlot: View {
    let date: Date
    let hour: Int
    let currentHour: Int
    let primaryLight: Color

    private var isToday: Bool { CalendarUtils.isToday(date) }
    private var isCurrentHour: Bool { isToday && hour == currentHour }

    var body: some View {
        Rectangle()
            .fill(isToday ? primaryLight : Color.white)
            .frame(height: 48)
            .overlay(alignment: .top) {
                if isCurrentHour {
                    Rectangle()
                        .fill(Color.red)
                        .frame(height: 2)
                }
            }
            .overlay(alignment: .bottom) {
                Divider()
            }
            .overlay(alignment: .trailing) {
                Divider()
            }
    }
}
