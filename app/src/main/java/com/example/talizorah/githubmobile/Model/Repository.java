package com.example.talizorah.githubmobile.Model;

import java.io.Serializable;

/**
 * Created by talizorah on 16.17.5.
 */
public class Repository implements Serializable {
    private String name;
    private String language;
    private String forks_count;
    private String stargazers_count;

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public String getForks_count() {
        return forks_count;
    }

    public String getStargazers_count() {
        return stargazers_count;
    }
}
