import SwiftUI

struct DayCellView: View {
    let dayInfo: DateInfo
    let onTap: () -> Void

    private static let primaryColor = Color(red: 0.1, green: 0.45, blue: 0.91)

    private var isToday: Bool {
        CalendarUtils.isToday(dayInfo.date)
    }

    var body: some View {
        Button(action: onTap) {
            Text("\(dayInfo.day)")
                .font(.subheadline)
                .fontWeight(isToday ? .semibold : .regular)
                .foregroundStyle(foregroundColor)
                .frame(width: 32, height: 32)
                .background(
                    Circle()
                        .fill(isToday ? Self.primaryColor : .clear)
                )
                .frame(maxWidth: .infinity, minHeight: 50, alignment: .top)
                .padding(.top, 6)
        }
        .buttonStyle(.plain)
        .accessibilityLabel(CalendarUtils.accessibilityDateLabel(for: dayInfo.date))
        .accessibilityHint(isToday ? "Today. Tap to view this day." : "Tap to view this day.")
    }

    private var foregroundColor: Color {
        if isToday {
            return .white
        } else if !dayInfo.isCurrentMonth {
            return .gray.opacity(0.5)
        } else {
            return .primary
        }
    }
}
