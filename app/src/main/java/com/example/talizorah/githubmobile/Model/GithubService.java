package com.example.talizorah.githubmobile.Model;

/**
 * Created by talizorah on 16.17.5.
 */

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubService {
    private GithubService(){}

    public static GithubApi createGithubService(){
        Retrofit.Builder builder = new Retrofit.Builder()
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com");

        return builder.build().create(GithubApi.class);
    }
}
