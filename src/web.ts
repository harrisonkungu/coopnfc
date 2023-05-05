import { WebPlugin } from '@capacitor/core';

import type { CoopNFCPlugin, ScanResult } from './definitions';


export class CoopNFCWeb extends WebPlugin implements CoopNFCPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
  
  private _video: HTMLVideoElement | null = null;
  

  async waitNFC(): Promise<void> {
    console.log("NFC for web not supported");
  }

  async startNfcListening(): Promise<void> {
    console.log("NFC for web not supported");
  }

  async handleOnNewIntent(): Promise<void> {
    console.log("NFC for web not supported");
  }

  async startScan(): Promise<ScanResult> {
    const video = await this._getVideoElement();
    if (video) {
      throw this.unavailable('Missing video element');
    } else {
      throw this.unavailable('Missing video element');
    }
  }


  private async _getVideoElement() {
    return this._video;
  }
  

}
