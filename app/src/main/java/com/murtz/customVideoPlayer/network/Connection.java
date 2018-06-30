package com.murtz.customVideoPlayer.network;

import android.content.Context;

import com.murtz.customVideoPlayer.BuildConfig;
import com.murtz.customVideoPlayer.datamodel.LineupsModel;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Murtuza.Saifee on 29-Jun-18.
 */

public class Connection {

    /**
     * Connection instance
     */
    private static Connection connection;

    private Connection() {
    }

    /**
     * Creates a singleton instance of the Connection interface
     *
     * @return
     */
    public static Connection getConnectionInstance() {
        if (connection == null) {
            connection = new Connection();
        }
        return connection;
    }

    public Retrofit getRetrofitClient() {
        Retrofit.Builder builder = null;
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(getInterceptor())
                .addInterceptor(getLogginInterceptor())
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        builder = new Retrofit.Builder().client(client);
        builder.baseUrl(BuildConfig.BASE_URL);
        builder.addConverterFactory(GsonConverterFactory.create());
        return builder.build();
    }

    private HttpLoggingInterceptor getLogginInterceptor() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        if (BuildConfig.FLAVOR.equals("production") && BuildConfig.BUILD_TYPE.equalsIgnoreCase("release"))
            logging.setLevel(HttpLoggingInterceptor.Level.NONE);
        else
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return logging;
    }

    private Interceptor getInterceptor() {
        return new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request.Builder builder = original.newBuilder().method(original.method(), original.body());
                Request request = builder.build();
                return chain.proceed(request);
            }
        };
    }

    public Call<LineupsModel> getLineups(Callback<LineupsModel> callback) {
        ApiInterface apiInterface = getRetrofitClient().create(ApiInterface.class);
        Call<LineupsModel> webserviceCall = apiInterface.lineups();
        webserviceCall.enqueue(callback);
        return webserviceCall;
    }

}
