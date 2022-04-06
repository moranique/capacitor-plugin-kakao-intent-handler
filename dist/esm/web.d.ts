import { WebPlugin } from '@capacitor/core';
import { KakaoIntentHandlerPlugin } from './definitions';
export declare class KakaoIntentHandlerWeb extends WebPlugin implements KakaoIntentHandlerPlugin {
    constructor();
    echo(options: {
        value: string;
    }): Promise<{
        value: string;
    }>;
}
declare const KakaoIntentHandler: KakaoIntentHandlerWeb;
export { KakaoIntentHandler };
