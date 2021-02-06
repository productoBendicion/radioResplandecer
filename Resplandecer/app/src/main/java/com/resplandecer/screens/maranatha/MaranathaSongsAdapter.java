package com.resplandecer.screens.maranatha;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.resplandecer.R;
import com.resplandecer.mediaPlayer.Audio;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MaranathaSongsAdapter extends RecyclerView.Adapter<MaranathaSongsAdapter.MaranathaSongViewHolder> {

    private ArrayList<Audio> maranathaAudios;
    private Context context;
    private Listener listener;
    private static int selectedSong = 0;
    private static boolean isPlaying;

    public MaranathaSongsAdapter(
            Context context, Listener listener, ArrayList<Audio> maranathaAudios) {
        this.maranathaAudios = maranathaAudios;
        this.context = context;
        this.listener = listener;
        isPlaying = false;
    }

    @NonNull
    @Override
    public MaranathaSongViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_view_holder, viewGroup, false);
        return new MaranathaSongViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MaranathaSongViewHolder maranathaSongViewHolder, int position) {

        if (position == selectedSong) {
            maranathaSongViewHolder.viewHolderLayout.setBackgroundResource(R.color.selected_background);

            if (isPlaying) {
                maranathaSongViewHolder.playStopButton.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimary)));
                maranathaSongViewHolder.playStopButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.home_stop_icon));
            } else {
                maranathaSongViewHolder.playStopButton.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.selected_background)));
                maranathaSongViewHolder.playStopButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_icon));
            }
        } else {
            maranathaSongViewHolder.viewHolderLayout.setBackgroundResource(R.color.white_background);
            maranathaSongViewHolder.playStopButton.setBackgroundTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.selected_background)));
            maranathaSongViewHolder.playStopButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.play_icon));

        }

        Audio audio = maranathaAudios.get(position);
        maranathaSongViewHolder.songTitle.setText(audio.getTitle());
        maranathaSongViewHolder.songAuthor.setText(audio.getAuthor());

        if (TextUtils.isEmpty(audio.getAuthor())) {
            maranathaSongViewHolder.songAuthor.setVisibility(View.GONE);
        } else {
            maranathaSongViewHolder.songAuthor.setVisibility(View.VISIBLE);
        }

    }

    public void updateCurrentSong(boolean isPlaying, int currentSong) {
        this.isPlaying = isPlaying;
        selectedSong = currentSong;
    }

    public void updateSongs(ArrayList<Audio> songs) {
        this.maranathaAudios.clear();
        this.maranathaAudios.addAll(songs);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return maranathaAudios.size();
    }

    public static class MaranathaSongViewHolder extends RecyclerView.ViewHolder {

        public TextView songTitle;
        public TextView songAuthor;
        public LinearLayout viewHolderLayout;
        public FloatingActionButton playStopButton;

        private Listener listener;

        public MaranathaSongViewHolder(@NonNull View itemView, final Listener listener) {
            super(itemView);
            this.listener = listener;

            viewHolderLayout = itemView.findViewById(R.id.view_holder_layout);
            songTitle = itemView.findViewById(R.id.song_title);
            songAuthor = itemView.findViewById(R.id.song_author);
            playStopButton = itemView.findViewById(R.id.play_stop_icon);


            if (getAdapterPosition() == selectedSong) {
                itemView.setBackgroundColor(Color.GRAY);
            }
            itemView
                    .setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSong = getAdapterPosition();
                                    listener.onSongTapped(getAdapterPosition());
                                }
                            }
                    );

            playStopButton
                    .setOnClickListener(
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    selectedSong = getAdapterPosition();
                                    listener.onSongTapped(getAdapterPosition());
                                }
                            }
                    );
        }
    }

    interface Listener {

        void onSongTapped(int selectedSong);
    }
}
