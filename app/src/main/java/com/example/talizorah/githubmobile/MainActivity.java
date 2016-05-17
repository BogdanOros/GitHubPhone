package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.MainThread;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.GithubApi;
import com.example.talizorah.githubmobile.Model.GithubService;
import com.example.talizorah.githubmobile.Model.Repository;
import com.example.talizorah.githubmobile.Model.User;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

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

    private void searchWithConnectionCheck(){
        if(ConnectionChecker.isNetworkAvailable(this)){
            downloadUserData();
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

}
