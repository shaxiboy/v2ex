package com.hjx.v2ex.network;

import android.content.Context;

import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.hjx.v2ex.AppConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

/**
 * Created by shaxiboy on 2017/3/4 0004.
 */

public class RetrofitSingleton {

    private static RetrofitService mService;

    public static RetrofitService getInstance(Context context) {
        if(mService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            if(AppConfig.DEBUG) {
                interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            } else {
                interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            }
            ClearableCookieJar cookieJar =
                    new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(context));
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .cookieJar(cookieJar)
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(RetrofitService.BASE_URL)
                    .addConverterFactory(HTMLConverterFactory.create())
                    .build();
            mService = retrofit.create(RetrofitService.class);
        }
        return mService;
    }

}
