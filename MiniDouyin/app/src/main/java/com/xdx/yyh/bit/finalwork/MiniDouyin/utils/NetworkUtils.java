package com.xdx.yyh.bit.finalwork.MiniDouyin.utils;


import com.xdx.yyh.bit.finalwork.MiniDouyin.bean.FeedResponse;
import com.xdx.yyh.bit.finalwork.MiniDouyin.newtork.IMiniDouyinService;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author Xavier.S
 * @date 2019.01.15 13:27
 */
public class NetworkUtils {

    public static String getResponseWithHttpURLConnection(String url) {
        String result = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {
            URL netUrl = new URL(url);
            urlConnection = (HttpURLConnection) netUrl.openConnection();
            urlConnection.setRequestMethod("GET");
            in = new BufferedInputStream(urlConnection.getInputStream());
            result = readStream(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private static String readStream(final InputStream inputStream) {
        final Scanner scanner = new Scanner(inputStream);
        scanner.useDelimiter("\\A");
        final String data = scanner.next();
        return data;
    }

    private static String readStreamBuffer(InputStream in) {
        StringBuilder result = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String s;
            while ((s = reader.readLine()) != null) {
                result.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static Call<FeedResponse> getResponseWithRetrofitAsync2() {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://douyin.fkynjyq.com/api/video/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        IMiniDouyinService iMiniDouyinService = retrofit.create(IMiniDouyinService.class);
        return iMiniDouyinService.getVideo("https://douyin.fkynjyq.com/api/video");
    }

}
