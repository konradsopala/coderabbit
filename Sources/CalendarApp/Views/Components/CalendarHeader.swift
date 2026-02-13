import SwiftUI

struct CalendarHeader: View {
    let title: String
    let onPrevious: () -> Void
    let onNext: () -> Void

    var body: some View {
        HStack {
            Button(action: onPrevious) {
                Image(systemName: "chevron.left")
                    .font(.title3)
                    .foregroundStyle(.secondary)
                    .frame(width: 36, height: 36)
                    .contentShape(Circle())
            }
            .accessibilityLabel("Previous")

            Spacer()

            Text(title)
                .font(.title2)
                .fontWeight(.medium)

            Spacer()

            Button(action: onNext) {
                Image(systemName: "chevron.right")
                    .font(.title3)
                    .foregroundStyle(.secondary)
                    .frame(width: 36, height: 36)
                    .contentShape(Circle())
            }
            .accessibilityLabel("Next")
        }
        .padding(.horizontal)
        .padding(.vertical, 8)
    }
}
