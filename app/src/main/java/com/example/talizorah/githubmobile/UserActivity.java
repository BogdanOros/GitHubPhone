package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Model.ConnectionChecker;
import com.example.talizorah.githubmobile.Model.CustomRepoList;
import com.example.talizorah.githubmobile.Model.User;
import com.google.android.gms.plus.PlusShare;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by talizorah on 16.17.5.
 */
public class UserActivity extends AppCompatActivity{

    private User user;
    private Activity mContext;
    @Bind(R.id.username) TextView usernameLabel;
    @Bind(R.id.my_toolbar) Toolbar toolbar;
    @Bind(R.id.img) ImageView imageView;
    @Bind(R.id.user_followers) TextView followers;
    @Bind(R.id.user_following) TextView following;
    @Bind(R.id.gists) TextView gists;
    @Bind(R.id.repos) TextView repos;
    @Bind(R.id.user_name) TextView name;
    @Bind(R.id.email) TextView email;
    @Bind(R.id.repos_list) RecyclerView repoView;

    private CustomRepoList customRepoListAdapter;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mContext = this;
        ButterKnife.bind(this);
        user = (User)getIntent().getExtras().get("user");
        setUpToolbar();
        setUpUserData();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.open_browser:
                openInBrowser();
                return true;
            case R.id.save:
                if(ConnectionChecker.isNetworkAvailable(this)){
                    saveIntoDb();
                } else {
                    Toast.makeText(mContext, R.string.saving_alert, Toast.LENGTH_SHORT).show();
                }

                return true;
            case R.id.share:
                publishDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveIntoDb(){
        subscription = getUserSaveObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        Toast.makeText(mContext, R.string.user_save, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(User user) {
                        DatabaseHelper.getDatabaseHelper(mContext).add(user);
                    }
                });
    }

    public Observable<User> getUserSaveObservable(){
        return Observable.just(user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user = null;
        customRepoListAdapter = null;
        ButterKnife.unbind(this);
        if(subscription != null){
            subscription.unsubscribe();
        }
    }

    private void setUpToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_home);
        usernameLabel.setText(user.getLogin());
    }


    private void setUpUserData() {

        String content = getString(R.string.alternative);
        name.append(user.getName() != null ?
                user.getName() : content);
        email.append(user.getEmail() != null ?
                user.getEmail() : content);
        followers.append(user.getFollowers() != null ?
                user.getFollowers() : content);
        following.append(user.getFollowing() != null ?
                user.getFollowing() : content);
        gists.append(user.getPublic_gists() != null ?
                user.getPublic_gists() : content);
        repos.append(user.getPublic_repos() != null ?
                user.getPublic_repos() : content);
        customRepoListAdapter = new CustomRepoList(this, user.getRepositories());

        imageView.setImageBitmap(user.getLoadedBitmap().getCurrentImage());
        repoView.setAdapter(customRepoListAdapter);
        repoView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void openInBrowser(){
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(user.getUrl()));
        startActivity(intent);
    }

    public void googleplusPublish(String text, String link) {
        Intent shareIntent = new PlusShare.Builder(this)
                .setType("text/plain")
                .setText(text)
                .setContentUrl(Uri.parse(link))
                .getIntent();
        startActivityForResult(shareIntent, 1001);
    }

    private void publishDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.allert);
        alert.setTitle(R.string.share_tittle);
        alert.setMessage(R.string.share_message);

        final EditText shareBox = new EditText(this);
        shareBox.setHint(R.string.share_hint);
        alert.setView(shareBox);

        alert.setPositiveButton(R.string.share_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                googleplusPublish(shareBox.getText().toString(), user.getUrl());
            }
        });
        alert.setNegativeButton(R.string.share_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alert.show();
    }

}
