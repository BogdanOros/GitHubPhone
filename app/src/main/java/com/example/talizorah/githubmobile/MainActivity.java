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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.talizorah.githubmobile.Database.DataProvider;
import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Database.DbBitmapUtility;
import com.example.talizorah.githubmobile.Model.BitmapDataObject;
import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.GithubApi;
import com.example.talizorah.githubmobile.Model.GithubService;
import com.example.talizorah.githubmobile.Model.Repository;
import com.example.talizorah.githubmobile.Model.SavedUserEntity;
import com.example.talizorah.githubmobile.Model.User;
import com.example.talizorah.githubmobile.Model.UserHandler;

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
import rx.functions.Func0;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private GithubApi githubService;
    private Subscription subscription;
    private Activity mActivity;
    private UserHandler userHandler;

    @Bind(R.id.editText)
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mActivity = this;
        userHandler = new UserHandler(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.saved_users:
                openSavedUsers();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void openSavedUsers(){
        Intent intent = new Intent(this, SavedUsersActivity.class);
        startActivity(intent);
    }

    private Observable<User> getDatabaseAccessObservable(Cursor cursor){
        return Observable.just(userHandler.getUserFromDatabase(cursor));
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
        alert.setTitle(R.string.no_internet_tittle);
        alert.setMessage(R.string.no_internet_message);
        alert.setPositiveButton(R.string.no_internet_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                searchWithConnectionCheck();
            }
        });
        alert.setNegativeButton(R.string.no_internet_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void noUserFindDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.no_user_tittle);
        alert.setMessage(R.string.no_user_message);
        alert.setPositiveButton(R.string.no_user_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void userDatabaseDialog(final Cursor cursor){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.db_tittle);
        alert.setMessage(R.string.db_message);
        alert.setPositiveButton(R.string.db_download, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                downloadUserData();
            }
        });
        alert.setNegativeButton(R.string.db_open, new DialogInterface.OnClickListener() {
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
}
