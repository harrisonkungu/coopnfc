export interface CoopNFCPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
