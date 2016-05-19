package com.example.talizorah.githubmobile;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.Model.CustomUserAdapter;
import com.example.talizorah.githubmobile.Model.SavedUserEntity;


import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by talizorah on 16.18.5.
 */
public class SavedUsersActivity extends AppCompatActivity {

    @Bind(R.id.users_list)RecyclerView recyclerView;
    private CustomUserAdapter customUserAdapter;

    @Bind(R.id.username)
    TextView usernameLabel;
    @Bind(R.id.my_toolbar)
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_saved);
        ButterKnife.bind(this);

        setUpToolbar();

        customUserAdapter = new CustomUserAdapter(this,
                DatabaseHelper.getDatabaseHelper(this).getUsers());
        recyclerView.setAdapter(customUserAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        ButterKnife.unbind(this);
        customUserAdapter = null;
    }

    private void setUpToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationIcon(R.drawable.ic_home);
        usernameLabel.setText(R.string.saved_users);
    }
}
