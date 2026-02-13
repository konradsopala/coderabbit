import SwiftUI

struct DayView: View {
    @Bindable var model: CalendarModel

    private static let primaryColor = Color(red: 0.1, green: 0.45, blue: 0.91)

    private var date: Date { model.selectedDate }

    private var isToday: Bool {
        CalendarUtils.isToday(date)
    }

    var body: some View {
        VStack(spacing: 0) {
            CalendarHeader(
                title: CalendarUtils.formatDate(date),
                onPrevious: { model.goToPreviousPeriod() },
                onNext: { model.goToNextPeriod() }
            )

            // Day badge
            VStack(spacing: 4) {
                Text(CalendarUtils.dayName(of: date))
                    .font(.caption)
                    .fontWeight(.medium)
                    .textCase(.uppercase)
                    .tracking(0.5)
                    .foregroundStyle(isToday ? Self.primaryColor : .secondary)

                Text("\(CalendarUtils.day(of: date))")
                    .font(.title)
                    .fontWeight(.medium)
                    .foregroundStyle(isToday ? .white : .primary)
                    .frame(width: 48, height: 48)
                    .background(
                        Circle()
                            .fill(isToday ? Self.primaryColor : .clear)
                    )
            }
            .padding(.bottom, 8)

            // Hour rows — TimelineView updates every minute for current-hour tracking
            TimelineView(.everyMinute) { context in
                let nowHour = Calendar.current.component(.hour, from: context.date)
                ScrollView {
                    VStack(spacing: 0) {
                        ForEach(CalendarUtils.hours) { hourInfo in
                            DayHourRow(
                                hourInfo: hourInfo,
                                isToday: isToday,
                                currentHour: nowHour
                            )
                        }
                    }
                    .background(Color(white: 1.0, opacity: 1.0))
                    .clipShape(RoundedRectangle(cornerRadius: 12))
                    .shadow(color: .black.opacity(0.06), radius: 2, y: 1)
                    .padding(.horizontal)
                }
            }
        }
    }
}

// MARK: - Day Hour Row

private struct DayHourRow: View {
    let hourInfo: HourInfo
    let isToday: Bool
    let currentHour: Int

    private var isCurrentHour: Bool { isToday && hourInfo.hour == currentHour }

    #if os(iOS)
    private var bgColor: Color {
        isCurrentHour ? Color(uiColor: .systemYellow).opacity(0.2) : Color(uiColor: .systemBackground)
    }
    #else
    private var bgColor: Color {
        isCurrentHour ? Color.yellow.opacity(0.2) : Color(nsColor: .windowBackgroundColor)
    }
    #endif

    var body: some View {
        HStack(spacing: 0) {
            Text(hourInfo.label)
                .font(.system(size: 12))
                .foregroundStyle(.secondary)
                .frame(width: 64, alignment: .trailing)
                .padding(.trailing, 12)

            Rectangle()
                .fill(bgColor)
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
        }
    }
}
