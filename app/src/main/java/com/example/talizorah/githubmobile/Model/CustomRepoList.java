package com.example.talizorah.githubmobile.Model;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.talizorah.githubmobile.R;

import java.security.AccessControlContext;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by talizorah on 16.17.5.
 */
public class CustomRepoList extends RecyclerView.Adapter<CustomRepoList.RepoViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Repository> repositories;

    public CustomRepoList(Context context, List<Repository> repositoryList){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.repositories = repositoryList;
    }

    @Override
    public RepoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.repo_item, parent, false);
        RepoViewHolder holder = new RepoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RepoViewHolder holder, int position) {
        Repository currentRepo = repositories.get(position);
        holder.getName().setText(currentRepo.getName());
        holder.getLang().setText(currentRepo.getLanguage());
        holder.getForks().setText(currentRepo.getForks_count());
        holder.getStars().setText(currentRepo.getStargazers_count());
    }


    @Override
    public int getItemCount() {
        return repositories.size();
    }

    class RepoViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.repo_name) TextView name;
        @Bind(R.id.repo_language)TextView lang;
        @Bind(R.id.forks_count) TextView forks;
        @Bind(R.id.stars_count) TextView stars;

        public RepoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public TextView getName() {
            return name;
        }

        public TextView getLang() {
            return lang;
        }


        public TextView getForks() {
            return forks;
        }

        public TextView getStars() {
            return stars;
        }

    }
}
