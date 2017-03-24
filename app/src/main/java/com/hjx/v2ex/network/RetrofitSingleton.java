package com.hjx.v2ex.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.net.InetSocketAddress;
import java.net.Proxy;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by shaxiboy on 2017/3/4 0004.
 */

public class RetrofitSingleton {

    private static V2EXService mService;

    public static V2EXService getInstance() {
        if(mService == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
            Retrofit retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(V2EXService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mService = retrofit.create(V2EXService.class);
        }
        return mService;
    }

}
