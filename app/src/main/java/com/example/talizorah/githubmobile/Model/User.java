package com.example.talizorah.githubmobile.Model;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by talizorah on 16.17.5.
 */
public class User implements Serializable{
    private String login;
    private String followers;
    private String following;
    private String email;
    private String name;
    private String avatar_url;
    private String public_repos;
    private String public_gists;
    private String html_url;
    private BitmapDataObject loadedBitmap;

    public BitmapDataObject getLoadedBitmap() {
        return loadedBitmap;
    }

    public String getUrl() {
        return html_url;
    }

    public void setLoadedBitmap(BitmapDataObject loadedBitmap) {
        this.loadedBitmap = loadedBitmap;
    }

    private List<Repository> repositories;

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public String getPublic_repos() {
        return public_repos;
    }

    public String getPublic_gists() {
        return public_gists;
    }

    public String getLogin() {
        return login;
    }

    public String getFollowers() {
        return followers;
    }

    public String getFollowing() {
        return following;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public void setPublic_repos(String public_repos) {
        this.public_repos = public_repos;
    }

    public void setPublic_gists(String public_gists) {
        this.public_gists = public_gists;
    }

    public void setHtml_url(String html_url) {
        this.html_url = html_url;
    }
}
