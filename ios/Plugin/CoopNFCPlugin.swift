import Foundation
import Capacitor

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */
@objc(CoopNFCPlugin)
public class CoopNFCPlugin: CAPPlugin {
    private let implementation = CoopNFC()

    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": implementation.echo(value)
        ])
    }

    @objc func startScan(_ call: CAPPluginCall) {
        guard NFCReaderSession.readingAvailable else {
          call.reject("NFC is not available on this device.")
          return
        }

        let tagReader = NFCISO7816TagReader(call: call)
        tagReader.startSession()
      }
}


class NFCISO7816TagReader: NSObject, NFCTagReaderSessionDelegate {

  // Properties
  var tagSession: NFCTagReaderSession?
  var completion: ((String?, Error?) -> Void)?
  var call: CAPPluginCall?

  // Initiate the tag session
  init(call: CAPPluginCall) {
    self.call = call
  }

  func tagReaderSession(_ session: NFCTagReaderSession, didDetect tags: [NFCTag]) {
    // Handle tag detection and session connection
    // ...
  }

  func tagReaderSession(_ session: NFCTagReaderSession, didInvalidateWithError error: Error) {
    // Handle session invalidation
    // ...
  }

  // Read the ISO-DEP tag
  func readISO7816tag(tag: NFCISO7816Tag) {
    // Read tag data and call back to completion closure
    // ...
  }

  // Start the ISO-DEP tag reading session
  func startSession() {
    tagSession = NFCTagReaderSession(pollingOption: [.iso15693, .iso14443, .iso18092], delegate: self, queue: nil)
    tagSession?.alertMessage = "Hold your iPhone near the ISO-DEP tag."
    tagSession?.begin()
  }

}