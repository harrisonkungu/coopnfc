import { registerPlugin } from '@capacitor/core';

import type { CoopNFCPlugin } from './definitions';

const CoopNFC = registerPlugin<CoopNFCPlugin>('CoopNFC', {
  web: () => import('./web').then(m => new m.CoopNFCWeb()),
});

export * from './definitions';
export { CoopNFC };
