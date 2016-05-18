package com.example.talizorah.githubmobile.Database;

/**
 * Created by talizorah on 16.18.5.
 */
public class DataProvider {
    // User table
    public static String TABLE = "gitDb";
    public static int DB_USER_VERSION = 7;
    public static String USER_TABLE = "userTb";
    public static String USER_ID = "id";
    public static String USER_LOGIN = "login";
    public static String USER_NAME = "name";
    public static String USER_FOLLOWERS = "followers";
    public static String USER_FOLLOWING = "following";
    public static String USER_EMAIL ="email";
    public static String USER_REPOS = "repos";
    public static String USER_GISTS = "gists";
    public static String HTML_URL = "url";
    public static String USER_IMAGE = "image";
    public static String TIME = "time";

    // Repos table
    public static String REPO_TABLE = "repoTb";
    public static String REPO_ID = "id";
    public static String REPO_NAME = "name";
    public static String REPO_LANG = "lang";
    public static String REPO_FORK = "forks";
    public static String REPO_STAR = "stars";
    public static String REPO_USER = "user_id";
}
