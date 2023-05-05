export interface CoopNFCPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
  waitNFC() : Promise<void>;
  startNfcListening() : Promise<void>;
  handleOnNewIntent() : Promise<void>;
  startScan(): Promise<ScanResult>;
}
export type ScanResult = IScanResultWithContent | IScanResultWithoutContent;

export interface IScanResultWithContent {
  /**
   * This indicates whether or not the scan resulted in readable content.
   * When stopping the scan with `resolveScan` set to `true`, for example,
   * this parameter is set to `false`, because no actual content was scanned.
   *
   * @since 1.0.0
   */
  hasContent: true;

  /**
   * This holds the content of the nfc if available.
   *
   * @since 1.0.0
   */
  content: string;

  /**
   * This holds the card type detected.
   *
   * @since 1.0.0
   */
  cardType: string;

  /**
   * This holds the card number detected.
   *
   * @since 1.0.0
   */

  cardNumber: string;
 
  /**
   * This holds the expiry date of the card detected.
   *
   * @since 1.0.0
   */


  expiryDate: string;

  
  /**
   * This returns format of scan result.
   *
   * @since 2.1.0
   */
  format: string;
}

export interface IScanResultWithoutContent {
  /**
   * This indicates whether or not the scan resulted in readable content.
   * When stopping the scan with `resolveScan` set to `true`, for example,
   * this parameter is set to `false`, because no actual content was scanned.
   *
   * @since 1.0.0
   */
  hasContent: false;

  /**
   * This holds the content of the nfc if available.
   *
   * @since 1.0.0
   */
  content: undefined;

  /**
   * This returns format of scan result.
   *
   * @since 2.1.0
   */
  format: undefined;
}

export interface TaskInfo {
  ret: Object;
}