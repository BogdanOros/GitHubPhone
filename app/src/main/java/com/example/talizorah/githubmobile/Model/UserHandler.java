package com.example.talizorah.githubmobile.Model;

import android.app.Activity;
import android.database.Cursor;

import com.example.talizorah.githubmobile.Database.DataProvider;
import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Database.DbBitmapUtility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by talizorah on 16.18.5.
 */
public class UserHandler {

    private Activity activity;

    public UserHandler(Activity activity){
        this.activity = activity;
    }

    // creating User from Cursor
    public User getUserFromDatabase(Cursor cursor){
        User user = new User();
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(DataProvider.USER_ID));
                user.setName(cursor.getString(cursor.getColumnIndex(DataProvider.USER_NAME)));
                user.setLogin(cursor.getString(cursor.getColumnIndex(DataProvider.USER_LOGIN)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(DataProvider.USER_EMAIL)));
                user.setPublic_repos(cursor.getString(cursor.getColumnIndex(DataProvider.USER_REPOS)));
                user.setPublic_gists(cursor.getString(cursor.getColumnIndex(DataProvider.USER_GISTS)));
                user.setFollowers(cursor.getString(cursor.getColumnIndex(DataProvider.USER_FOLLOWERS)));
                user.setFollowing(cursor.getString(cursor.getColumnIndex(DataProvider.USER_FOLLOWING)));
                user.setHtml_url(cursor.getString(cursor.getColumnIndex(DataProvider.HTML_URL)));
                user.setLoadedBitmap(new BitmapDataObject(
                        DbBitmapUtility.getImage(cursor.getBlob(cursor.getColumnIndex(DataProvider.USER_IMAGE)))));

                List<Repository> list = new ArrayList<>();
                Cursor reposCursor = DatabaseHelper.getDatabaseHelper(activity).getRepos(id);

                try{
                    while(reposCursor.moveToNext()){
                        Repository repository = new Repository();
                        repository.setName(reposCursor.getString(
                                reposCursor.getColumnIndex(DataProvider.REPO_NAME))); // 2 = RepoName
                        repository.setLanguage(reposCursor.getString(
                                reposCursor.getColumnIndex(DataProvider.REPO_LANG))); // 3 = RepoLanguage
                        repository.setForks_count(reposCursor.getString(
                                reposCursor.getColumnIndex(DataProvider.REPO_FORK))); // 4 = RepoForks
                        repository.setStargazers_count(reposCursor.getString(
                                reposCursor.getColumnIndex(DataProvider.REPO_STAR))); // 5 = RepoStars
                        list.add(repository);
                    }
                }
                finally {
                    reposCursor.close();
                }

                user.setRepositories(list);

            }
        } finally {
            cursor.close();
        }

        return  user;
    }
}
