package com.pantherman594.gitzucccd;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.FriendViewHolder> {

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        ImageView profile;
        TextView name;
        TextView url;

        FriendViewHolder(View itemView) {
            super(itemView);
            cv = itemView.findViewById(R.id.cv);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            url = itemView.findViewById(R.id.url);
        }
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_friends, viewGroup, false);
        return new FriendViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder friendViewHolder, int i) {
        Log.d(">>>>>>", "" + i);
        Friend friend = Friend.getFriend(i);
        if (friend == null) return;
        Log.d("PROFFFFFF", String.format("u:%s, n:%s", friend.getUsername(), friend.getName()));
        friendViewHolder.profile.setImageBitmap(friend.getProfImg());
        friendViewHolder.name.setText(friend.getName());
        friendViewHolder.url.setText(friend.getProfUrl());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return Friend.size();
    }
}

