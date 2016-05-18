package com.example.talizorah.githubmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Model.CustomUserAdapter;
import com.example.talizorah.githubmobile.Model.SavedUserEntity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * Created by talizorah on 16.18.5.
 */
public class SavedUsersActivity extends AppCompatActivity {

    @Bind(R.id.users_list)RecyclerView recyclerView;
    private CustomUserAdapter customUserAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_saved);
        ButterKnife.bind(this);
        customUserAdapter = new CustomUserAdapter(this,
                DatabaseHelper.getDatabaseHelper(this).getUsers());
        recyclerView.setAdapter(customUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
