package com.smart.agriculture.industrial.solutions.packages.utils.services;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashSet;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * This Interceptor add all received Cookies to the app DefaultPreferences.
 * Your implementation on how to save the Cookies on the Preferences may vary.
 */

public class ReceivedCookiesInterceptor implements Interceptor {
    private Context context;

    ReceivedCookiesInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        if (!originalResponse.headers("Set-Cookie").isEmpty()) {
            HashSet<String> cookies = (HashSet<String>) PreferenceManager.getDefaultSharedPreferences(context).getStringSet("PREF_COOKIES", new HashSet<>());
//            CookieManager cookieManager = new CookieManager();
//            CookieHandler.setDefault(cookieManager);

            cookies.addAll(originalResponse.headers("Set-Cookie"));

            SharedPreferences.Editor memes = PreferenceManager.getDefaultSharedPreferences(context).edit();
            memes.putStringSet("PREF_COOKIES", cookies).apply();
            memes.commit();
        }

        return originalResponse;
    }
}