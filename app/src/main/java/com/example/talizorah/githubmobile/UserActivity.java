package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.talizorah.githubmobile.Model.CustomRepoList;
import com.example.talizorah.githubmobile.Model.User;

import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by talizorah on 16.17.5.
 */
public class UserActivity extends AppCompatActivity{

    private User user;

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

        ButterKnife.bind(this);
        user = (User)getIntent().getExtras().get("user");
        setUpToolbar();
        setUpUserData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
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


    private void setUpUserData(){

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
}
