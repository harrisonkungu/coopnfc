import Foundation
import Capacitor
import CoreNFC

/**
 * Please read the Capacitor iOS Plugin Development Guide
 * here: https://capacitorjs.com/docs/plugins/ios
 */

//  initialize errors here
 enum NfcReaderError: String, Error {
     case unsupportedPlatform = "NFC functionality is only available on iOS."
     case invalidPayload = "Invalid NFC payload."
     case invalidRecordType = "Invalid NFC record type."
 }

@objc(CoopNFCPlugin)
public class CoopNFCPlugin: CAPPlugin, NFCNDEFReaderSessionDelegate {
    let session = NFCNDEFReaderSession.init(delegate: nil, queue: nil, invalidateAfterFirstRead: true)


    @objc func echo(_ call: CAPPluginCall) {
        let value = call.getString("value") ?? ""
        call.resolve([
            "value": "Hae Gidraf"
        ])
    }


    // This method can be called from your web app to start reading NFC tags
    @objc func startScan(_ call: CAPPluginCall) {
        guard NFCNDEFReaderSession.readingAvailable else {
            call.reject(NfcReaderError.unsupportedPlatform.rawValue)
            return
        }

        session.delegate = self
        session.begin()

        // Store the plugin call object to use later
        // when the NFC session is done reading the tag
        bridge.saveCall(call)
    }

    // When the NFCNDEFReaderSession detects a tag, this method will be called
    public func readerSession(_ session: NFCNDEFReaderSession, didDetectNDEFs messages: [NFCNDEFMessage]) {
        guard let call = bridge.getSavedCall() else {
            session.invalidate(errorMessage:NfcReaderError.invalidPayload.rawValue)
            return
        }

        // Parse the data in the NDEF message
        for message in messages {
            for record in message.records {
                if record.typeNameFormat == NFCTypeNameFormat.nfcWellKnown && record.type == "T3".data(using: .utf8) {
                    var jsonObject =  PluginCallResultData()
                    let payload = record.payload
                    // The credit card data is in the payload of the T3 record
                    // You will need to parse the data based on the Visa protocol
                    // For more information on the Visa protocol, please refer to Visa's documentation

                    let creditCardData = String(data: payload, encoding: .utf8)
                    jsonObject["hasContent"] = true
                    jsonObject["content"] = creditCardData
                    call.resolve(jsonObject)
                }
            }
        }
    }

    // Handle errors
    public func readerSession(_ session: NFCNDEFReaderSession, didInvalidateWithError error: Error) {
        guard let call = bridge.getSavedCall() else { return }
        var jsonObject =  PluginCallResultData()
        jsonObject["hasContent"] = true
        jsonObject["content"] = error.localizedDescription
        call.reject(jsonObject)
        call.reject(error.localizedDescription)
    }
}





// @objc(CoopNFCPlugin)
// public class CoopNFCPlugin: CAPPlugin {
//     private let implementation = CoopNFC()
//
//     @objc func echo(_ call: CAPPluginCall) {
//         let value = call.getString("value") ?? ""
//         call.resolve([
//             "value": implementation.echo(value)
//         ])
//     }
//
//     @objc func startScan(_ call: CAPPluginCall) {
//         guard NFCReaderSession.readingAvailable else {
//           call.reject("NFC is not available on this device.")
//
//
//           jsObject.put("hasContent", true);
//           jsObject.put("content", "NFC Disabled");
//           jsObject.put("cardType", "CARD_ERR");
//           jsObject.put("cardNumber", "CARD_ERR");
//           jsObject.put("expiryDate", "CARD_ERR");
//           PluginCall call = getSavedCall();
//           call.reject(jsObject);
//           return
//         }
//
//         let tagReader = NFCISO7816TagReader(call: call)
//         tagReader.startSession()
//       }
// }
//
//
// class NFCISO7816TagReader: NSObject, NFCTagReaderSessionDelegate {
//
//   // Properties
//   var tagSession: NFCTagReaderSession?
//   var completion: ((String?, Error?) -> Void)?
//   var call: CAPPluginCall?
//
//   // Initiate the tag session
//   init(call: CAPPluginCall) {
//     self.call = call
//   }
//
//   func tagReaderSession(_ session: NFCTagReaderSession, didDetect tags: [NFCTag]) {
//     // Handle tag detection and session connection
//     // ...
//   }
//
//   func tagReaderSession(_ session: NFCTagReaderSession, didInvalidateWithError error: Error) {
//     // Handle session invalidation
//     // ...
//   }
//
//   // Read the ISO-DEP tag
//   func readISO7816tag(tag: NFCISO7816Tag) {
//     // Read tag data and call back to completion closure
//     // ...
//   }
//
//   // Start the ISO-DEP tag reading session
//   func startSession() {
//     tagSession = NFCTagReaderSession(pollingOption: [.iso15693, .iso14443, .iso18092], delegate: self, queue: nil)
//     tagSession?.alertMessage = "Hold your iPhone near the ISO-DEP tag."
//     tagSession?.begin()
//   }
//
// }