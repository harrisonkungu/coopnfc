import { WebPlugin } from '@capacitor/core';

import type { CoopNFCPlugin } from './definitions';

export class CoopNFCWeb extends WebPlugin implements CoopNFCPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
