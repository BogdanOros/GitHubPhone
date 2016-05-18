package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.talizorah.githubmobile.Database.DataProvider;
import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Database.DbBitmapUtility;
import com.example.talizorah.githubmobile.Model.BitmapDataObject;
import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.GithubApi;
import com.example.talizorah.githubmobile.Model.GithubService;
import com.example.talizorah.githubmobile.Model.Repository;
import com.example.talizorah.githubmobile.Model.User;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private GithubApi githubService;
    private Subscription subscription;
    private Activity mActivity;

    @Bind(R.id.editText)
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        githubService = GithubService.createGithubService();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(subscription != null)
            subscription.unsubscribe();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.search)
    public void searchUser(){
        searchWithConnectionCheck();
    }


    private Observable<User> getDatabaseAccessObservable(Cursor cursor){
        return Observable.just(getUserFromDatabase(cursor));
    }

    private Cursor checkSavedUser(){
        return DatabaseHelper.getDatabaseHelper(this).getUserWithLogin(username.getText().toString());
    }

    private void searchWithConnectionCheck(){
        if(ConnectionChecker.isNetworkAvailable(this)){
            Cursor cursor = checkSavedUser();
            if(cursor.getCount() > 0){
                userDatabaseDialog(cursor);
            }
            else {
                downloadUserData();
            }
        }
        else {
            noConnectionDialog();
        }
    }


    private void downloadUserData() {
        subscription = Observable.combineLatest(githubService.user(username.getText().toString()),
                githubService.repos(username.getText().toString()), new Func2<User, List<Repository>, User>()
                {
                    @Override
                    public User call(User user, List<Repository> repositories) {
                        user.setRepositories(repositories);
                        if(user.getAvatar_url() != null){
                            try {
                                BitmapDataObject dataObject =
                                        new BitmapDataObject(getBitmap(user.getAvatar_url()));
                                user.setLoadedBitmap(dataObject);
                            }
                            catch (Exception ex){
                                ex.printStackTrace();
                            }
                        }
                        return user;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    Intent intent;

                    @Override
                    public void onCompleted() {
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        noUserFindDialog();
                    }

                    @Override
                    public void onNext(User user) {
                        intent = new Intent(mActivity, UserActivity.class);
                        intent.putExtra("user", user);
                    }
                });
    }

    private Bitmap getBitmap(String urlStr) throws Exception{
        URL url = new URL(urlStr);
        Bitmap map =  BitmapFactory.decodeStream(url.openConnection().getInputStream());
        return map;
    }

    private void noConnectionDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tittle");
        alert.setMessage("message");
        alert.setPositiveButton("Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                searchWithConnectionCheck();
            }
        });
        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void noUserFindDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tittle");
        alert.setMessage("no user");
        alert.setPositiveButton("Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void userDatabaseDialog(final Cursor cursor){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tittle");
        alert.setMessage("no user");
        alert.setPositiveButton("Download", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                downloadUserData();
            }
        });
        alert.setNegativeButton("Use saved info", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getDatabaseAccessObservable(cursor)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<User>() {
                            Intent intent;

                            @Override
                            public void onCompleted() {
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(User user) {
                                intent = new Intent(mActivity, UserActivity.class);
                                intent.putExtra("user", user);
                            }
                        });
            }
        });
        alert.show();
    }


    private User getUserFromDatabase(Cursor cursor){
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
                Cursor reposCursor = DatabaseHelper.getDatabaseHelper(this).getRepos(id);
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
