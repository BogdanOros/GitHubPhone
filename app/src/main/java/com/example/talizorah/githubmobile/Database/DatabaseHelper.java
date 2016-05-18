package com.example.talizorah.githubmobile.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;

import com.example.talizorah.githubmobile.Model.Repository;
import com.example.talizorah.githubmobile.Model.User;

/**
 * Created by talizorah on 16.18.5.
 */
public class DatabaseHelper {
    private SQLiteOpenHelper sqLiteOpenHelper;
    private static DatabaseHelper databaseHelper;
    private DatabaseHelper(Context context){
        sqLiteOpenHelper = new UserDataOpenHelper(context);
    }
    public static DatabaseHelper getDatabaseHelper(Context context){
        if(databaseHelper == null)
            databaseHelper = new DatabaseHelper(context);
        return databaseHelper;
    }


    class UserDataOpenHelper extends SQLiteOpenHelper{
        public UserDataOpenHelper(Context context){
            super(context, DataProvider.TABLE, null, DataProvider.DB_USER_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            // have no calculations, so only text fields

            db.execSQL("Create table " + DataProvider.REPO_TABLE + " (" +
                    DataProvider.REPO_ID + " integer primary key autoincrement, " +
                            DataProvider.REPO_USER + " integer, " +
                    DataProvider.REPO_NAME + " text, " +
                    DataProvider.REPO_LANG + " text, " +
                    DataProvider.REPO_FORK + " text, " +
                    DataProvider.REPO_STAR + " text)"
            );


            db.execSQL("Create table " + DataProvider.USER_TABLE + " (" +
                    DataProvider.USER_ID + " integer primary key autoincrement, " +
                    DataProvider.USER_LOGIN + " text, " +
                    DataProvider.USER_NAME + " text, " +
                    DataProvider.USER_FOLLOWERS + " text,  " +
                    DataProvider.USER_REPOS + " text, " +
                    DataProvider.USER_EMAIL + " text, " +
                    DataProvider.USER_GISTS + " text, " +
                    DataProvider.HTML_URL + " text, " +
                    DataProvider.USER_IMAGE + " BLOB,  " +
                    DataProvider.USER_FOLLOWING + " text )");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DataProvider.USER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DataProvider.REPO_TABLE);
            onCreate(db);
        }
    }

    public Cursor getRepos(int id){
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        if(db == null)
            return null;
        String query = "select * from " + DataProvider.REPO_TABLE + " where "
                + DataProvider.REPO_USER + " = " + id;
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }

    public Cursor getUserWithLogin(String login){
        SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
        if(db == null)
            return null;
        String query = "select * from " + DataProvider.USER_TABLE + " where "
                + DataProvider.USER_LOGIN + " = " + "'" +  login + "'";
        Cursor cur = db.rawQuery(query, null);
        return cur;
    }
    public int add(User user) {
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        if (db == null) {
            return 0;
        }
        ContentValues row = new ContentValues();
        row.put(DataProvider.USER_LOGIN, user.getLogin());
        row.put(DataProvider.USER_FOLLOWERS, user.getFollowers());
        row.put(DataProvider.USER_REPOS, user.getPublic_repos());
        row.put(DataProvider.USER_EMAIL, user.getEmail());
        row.put(DataProvider.USER_GISTS, user.getPublic_gists());
        row.put(DataProvider.USER_NAME, user.getName());
        row.put(DataProvider.HTML_URL, user.getUrl());
        row.put(DataProvider.USER_FOLLOWING, user.getFollowing());
        row.put(DataProvider.USER_IMAGE, DbBitmapUtility.getBytes(user.getLoadedBitmap().getCurrentImage()));
        int id;
        if(getUserWithLogin(user.getLogin()).getCount() > 0){
            id = db.update(DataProvider.USER_TABLE, row,
                    " " + DataProvider.USER_LOGIN + " = " + "'" + user.getLogin() + "'", null);
            db.delete(DataProvider.REPO_TABLE, " " + DataProvider.REPO_USER + " = " + id, null);
        }
        else{
            id = (int)db.insert(DataProvider.USER_TABLE, null, row);
        }
        for(Repository repository: user.getRepositories()){
            ContentValues tmpRow = new ContentValues();
            tmpRow.put(DataProvider.REPO_NAME, repository.getName());
            tmpRow.put(DataProvider.REPO_LANG, repository.getLanguage());
            tmpRow.put(DataProvider.REPO_FORK, repository.getForks_count());
            tmpRow.put(DataProvider.REPO_STAR, repository.getStargazers_count());
            tmpRow.put(DataProvider.REPO_USER, id);
            db.insert(DataProvider.REPO_TABLE, null, tmpRow);
        }
        db.close();
        return id;
    }
}
