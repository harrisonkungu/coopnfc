import Foundation

@objc public class CoopNFC: NSObject {
    @objc public func echo(_ value: String) -> String {
        print(value)
        return value
    }
}
