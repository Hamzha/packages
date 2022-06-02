package com.smart.agriculture.industrial.solutions.packages.utils.services;

import android.app.Application;

import com.smart.agriculture.industrial.solutions.packages.utils.WebsiteUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class ApiClient {

    private static ApiClient mInstance;
    private Retrofit retrofit;


    private ApiClient(Application application) {

        OkHttpClient client;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.addInterceptor(new AddCookiesInterceptor(application)); // VERY VERY IMPORTANT
        builder.addInterceptor(new ReceivedCookiesInterceptor(application));


        builder.readTimeout(20, TimeUnit.SECONDS);
        builder.writeTimeout(20, TimeUnit.SECONDS);
        client = builder.build();


        retrofit = new Retrofit.Builder()
                .baseUrl(WebsiteUtils.BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized ApiClient getInstance(Application application) {
        if (mInstance == null) {
            mInstance = new ApiClient(application);
        }

        return mInstance;
    }

    public ApiInterface getApi() {
        return retrofit.create(ApiInterface.class);
    }

}
