package com.example.talizorah.githubmobile.Model;

import android.app.Activity;
import android.database.Cursor;

import com.example.talizorah.githubmobile.Database.DataProvider;
import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Database.DbBitmapUtility;

import java.security.AccessControlContext;
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
                        repository.setName(reposCursor.getString(2));
                        repository.setLanguage(reposCursor.getString(3));
                        repository.setForks_count(reposCursor.getString(4));
                        repository.setStargazers_count(reposCursor.getString(5));
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
