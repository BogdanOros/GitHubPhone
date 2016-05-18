package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Model.CustomRepoList;
import com.example.talizorah.githubmobile.Model.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
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
                saveIntoDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveIntoDb(){
        getUserSaveObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {

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
        ButterKnife.unbind(this);
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
}
