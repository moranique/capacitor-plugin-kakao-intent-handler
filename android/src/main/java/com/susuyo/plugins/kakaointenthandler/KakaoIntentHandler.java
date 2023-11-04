package com.susuyo.plugins.kakaointenthandler;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.content.Intent;
import android.webkit.URLUtil;

import com.getcapacitor.JSObject;
import com.getcapacitor.NativePlugin;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import java.net.URISyntaxException;

@NativePlugin
public class KakaoIntentHandler extends Plugin {

    static String TAG = "kakaointenthandler";

    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");

        JSObject ret = new JSObject();
        ret.put("value", value);
        call.success(ret);
    }

    @PluginMethod
    public Boolean shouldOverrideLoad(Uri url) {

        String path = url.toString();

        if (!URLUtil.isNetworkUrl(path) && !URLUtil.isJavaScriptUrl(path)) {
            final Uri uri;

            try {
                uri = Uri.parse(path);
            } catch (Exception e) {
                return false;
            }

            Context context = getContext();

            if ("intent".equals(uri.getScheme())) {
                return startSchemeIntent(path);
            } else if (url.getScheme().equals("capacitor")) {
                // capacitor://app.moranique.com
                bridge.getWebView().loadUrl(url.toString().replace("capacitor:", "https:"));
                return true;
            } else if (url.getScheme().equals("tel")) {
                try {
                    Intent intent = Intent.parseUri(url.toString(), Intent.URI_INTENT_SCHEME);
                    getContext().startActivity(intent);
                    return true;
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return false;
                }
            }  else {
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean startSchemeIntent(String url) {
        final Intent schemeIntent;

        try {
            schemeIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            return false;
        }

        Context context = getContext();

        // Fallback URL이 있으면 현재 웹뷰에 로딩
        String fallbackUrl = schemeIntent.getStringExtra("browser_fallback_url");
        if (fallbackUrl != null) {
            bridge.getWebView().loadUrl(fallbackUrl);
            Log.d(TAG, "FALLBACK: $fallbackUrl");
            return true;
        }

        try {
            context.startActivity(schemeIntent);
            Log.d(TAG, "ACTIVITY: ${intent.`package`}");
            return true;
        } catch (ActivityNotFoundException e) {
            final String packageName = schemeIntent.getPackage();
            Log.d(TAG, "Not Package: ${intent.`package`}");
            if (!TextUtils.isEmpty(packageName)) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                return true;
            }
        }

        return false;
    }
}
