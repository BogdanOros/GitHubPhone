package com.example.talizorah.githubmobile.Model;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by talizorah on 16.17.5.
 */
public interface GithubApi {

    @GET("/users/{user}")
    Observable<User> user(@Path("user") String user);


    @GET("users/{user}/repos")
    Observable<List<Repository>> repos(@Path("user") String user);

}
