package com.example.talizorah.githubmobile.Model;

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
}
