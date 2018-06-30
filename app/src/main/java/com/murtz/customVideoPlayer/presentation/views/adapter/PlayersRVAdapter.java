package com.murtz.customVideoPlayer.presentation.views.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.murtz.customVideoPlayer.R;
import com.murtz.customVideoPlayer.datamodel.PlayersModel;

import java.util.List;

/**
 * Created by Murtuza.Saifee on 30-Jun-18.
 */

public class PlayersRVAdapter extends RecyclerView.Adapter<PlayersRVAdapter.PlayersViewHolder> {

    private List<PlayersModel> listItem;
    private View.OnClickListener clickListener;

    public PlayersRVAdapter(List<PlayersModel> listItem, View.OnClickListener clickListener) {
        this.listItem = listItem;
        this.clickListener = clickListener;
    }

    @Override
    public PlayersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.player_card, parent, false);
        return new PlayersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlayersViewHolder holder, int position) {
        String playerNameRole = listItem.get(position).getName() +
                " (" +
                listItem.get(position).getRole() +
                ")";
        holder.playerName.setText(playerNameRole);
        holder.playerCard.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }

    public class PlayersViewHolder extends RecyclerView.ViewHolder {

        private View playerCard;
        private TextView playerName;

        public PlayersViewHolder(View itemView) {
            super(itemView);
            playerCard = itemView.findViewById(R.id.playerCard);
            playerName = itemView.findViewById(R.id.playerName);
        }
    }
}
