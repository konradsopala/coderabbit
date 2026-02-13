// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "CalendarApp",
    platforms: [
        .iOS(.v17),
        .macOS(.v14)
    ],
    targets: [
        .executableTarget(
            name: "CalendarApp",
            path: "Sources/CalendarApp"
        )
    ]
)
