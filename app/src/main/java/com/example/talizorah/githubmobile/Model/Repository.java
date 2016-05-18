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

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setForks_count(String forks_count) {
        this.forks_count = forks_count;
    }

    public void setStargazers_count(String stargazers_count) {
        this.stargazers_count = stargazers_count;
    }
}
