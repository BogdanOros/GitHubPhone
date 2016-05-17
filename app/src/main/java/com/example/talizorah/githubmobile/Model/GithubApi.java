package com.example.talizorah.githubmobile.Model;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by talizorah on 16.17.5.
 */
public interface GithubApi {

    @GET("/users/{user}")
    Observable<User> user(@Path("user") String user);

    @GET("/users/{user}")
    User getUser(@Path("user") String user);

}
