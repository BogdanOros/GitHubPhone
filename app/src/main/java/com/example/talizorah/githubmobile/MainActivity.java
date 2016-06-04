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
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Model.BitmapDataObject;
import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.GithubApi;
import com.example.talizorah.githubmobile.Model.GithubService;
import com.example.talizorah.githubmobile.Model.Repository;
import com.example.talizorah.githubmobile.Model.User;
import com.example.talizorah.githubmobile.Model.UserHandler;

import java.net.URL;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private GithubApi githubService;
    private Subscription subscription;
    private Activity mActivity;
    private UserHandler userHandler;

    @Bind(R.id.editText)
    EditText username;



    @Bind(R.id.loading_bar)ProgressBar bar;

    @Bind(R.id.username)
    TextView usernameLabel;
    @Bind(R.id.my_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        mActivity = this;
        userHandler = new UserHandler(this);
        githubService = GithubService.createGithubService();
        ButterKnife.bind(this);
        bar.setVisibility(View.INVISIBLE);
        setUpToolbar();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void setUpToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setLogo(R.drawable.ic_toolbar);
        usernameLabel.setText(R.string.app_name);
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

    private Observable<User> getDatabaseAccessObservable(final Cursor cursor){
        return Observable.defer(new Func0<Observable<User>>() {
            @Override
            public Observable<User> call() {
                return Observable.just(userHandler.getUserFromDatabase(cursor));
            }
        });
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
        bar.setVisibility(View.VISIBLE);
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
                        bar.setVisibility(View.INVISIBLE);
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable e) {
                        bar.setVisibility(View.INVISIBLE);
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
        return BitmapFactory.decodeStream(url.openConnection().getInputStream());
    }

    private void noConnectionDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.allert);
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

    // user not found
    private void noUserFindDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.allert);
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

    // user found in database
    private void userDatabaseDialog(final Cursor cursor){
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.allert);
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
