import SwiftUI

struct ContentView: View {
    @State private var model = CalendarModel()

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                switch model.viewMode {
                case .month:
                    MonthView(model: model)
                case .week:
                    WeekView(model: model)
                case .day:
                    DayView(model: model)
                }
            }
            .background(Color(white: 0.95))
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Picker("View", selection: $model.viewMode) {
                        ForEach(CalendarModel.ViewMode.allCases) { mode in
                            Text(mode.rawValue).tag(mode)
                        }
                    }
                    .pickerStyle(.segmented)
                    .frame(width: 220)
                }

                #if os(iOS)
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Today") {
                        model.goToToday()
                    }
                    .fontWeight(.medium)
                }
                #else
                ToolbarItem(placement: .automatic) {
                    Button("Today") {
                        model.goToToday()
                    }
                    .fontWeight(.medium)
                }
                #endif
            }
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
        }
    }
}
