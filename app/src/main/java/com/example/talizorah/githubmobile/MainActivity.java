package com.example.talizorah.githubmobile;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.GithubApi;
import com.example.talizorah.githubmobile.Model.GithubService;
import com.example.talizorah.githubmobile.Model.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private GithubApi githubService;
    private CompositeSubscription subscriptions;

    @Bind(R.id.editText)
    EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        githubService = GithubService.createGithubService();
        subscriptions = new CompositeSubscription();
        ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.unsubscribe();
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
        subscriptions.add(
                githubService.user(username.getText().toString())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<User>() {
                            @Override
                            public void onCompleted() {
                                Log.v("DOWN", "COMPLETED");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.v("DOWN", "ERROR");
                            }

                            @Override
                            public void onNext(User user) {
                                Log.v("DOWN", user.getName());
                            }
                        }));
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

}
