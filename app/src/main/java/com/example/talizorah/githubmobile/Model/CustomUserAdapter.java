package com.example.talizorah.githubmobile.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.talizorah.githubmobile.Database.DatabaseHelper;
import com.example.talizorah.githubmobile.R;
import com.example.talizorah.githubmobile.UserActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by talizorah on 16.18.5.
 */
public class CustomUserAdapter extends RecyclerView.Adapter<CustomUserAdapter.UserViewHolder> {

    private LayoutInflater inflater;
    private List<SavedUserEntity> users;

    public CustomUserAdapter(Context context, List<SavedUserEntity> items){
        inflater = LayoutInflater.from(context);
        this.users = items;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_item, parent, false);
        UserViewHolder holder = new UserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        SavedUserEntity currentUser = users.get(position);
        holder.username.setText(currentUser.login);
        holder.time.setText(currentUser.time);
        holder.view.setImageResource(R.drawable.ic_user_logo);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @Bind(R.id.saved_user_name)TextView username;
        @Bind(R.id.saved_user_time)TextView time;
        @Bind(R.id.git_image)ImageView view;

        public UserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Activity activity = (Activity)v.getContext();
            Cursor cursor = DatabaseHelper.getDatabaseHelper(activity)
                    .getUserWithLogin(username.getText().toString());
            User user = new UserHandler(activity).getUserFromDatabase(cursor);
            openUserInfo(activity, user);
        }

        private void openUserInfo(Activity activity, User user){
            Intent intent = new Intent(activity, UserActivity.class);
            intent.putExtra("user", user);
            activity.startActivity(intent);
        }
    }
}
