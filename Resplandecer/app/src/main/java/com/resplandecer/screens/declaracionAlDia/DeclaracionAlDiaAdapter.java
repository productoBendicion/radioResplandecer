package com.resplandecer.screens.declaracionAlDia;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.util.Log;
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

public class DeclaracionAlDiaAdapter extends RecyclerView.Adapter<DeclaracionAlDiaAdapter.AudioViewHolder> {

    private ArrayList<Audio> audioList;
    private Context context;
    private Listener listener;
    private static int selectedSong = 0;
    private static boolean isPlaying;


    public DeclaracionAlDiaAdapter(
            Context context, Listener listener, ArrayList<Audio> audioList) {
        this.audioList = audioList;
        this.context = context;
        this.listener = listener;
        isPlaying = false;
    }

    @NonNull
    @Override
    public AudioViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("Declaraciones", "onCreateViewHolder");

        View view = LayoutInflater.from(context).inflate(R.layout.song_view_holder, viewGroup, false);
        view.setSelected(selectedSong == i);
        final int finalI = i;
        view
                .setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedSong = finalI;
                                listener.onSongTapped(finalI);
//                                Log.d("Declaraciones", "clicked: " + finalI);

                            }
                        }
                );
        return new AudioViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull AudioViewHolder maranathaSongViewHolder, int position) {
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

        Audio declaracion = audioList.get(position);
        maranathaSongViewHolder.songTitle.setText(declaracion.getTitle());
        maranathaSongViewHolder.author.setText(declaracion.getAuthor());

        if (TextUtils.isEmpty(declaracion.getAuthor())) {
            maranathaSongViewHolder.author.setVisibility(View.GONE);
        } else {
            maranathaSongViewHolder.author.setVisibility(View.VISIBLE);
        }
    }

    public void updateDeclaraciones(ArrayList<Audio> audioList) {
        Log.d("Declaraciones", "update");
//        Log.d("Declaraciones", "updateSize: " + audioList.size());


        this.audioList.clear();
        this.audioList.addAll(audioList);
        notifyDataSetChanged();
    }

    public void updateCurrentSong(boolean isPlaying, int currentSong) {
        this.isPlaying = isPlaying;
        selectedSong = currentSong;
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public static class AudioViewHolder extends RecyclerView.ViewHolder {

        public TextView songTitle;
        public TextView author;
        public LinearLayout viewHolderLayout;
        private Listener listener;
        public FloatingActionButton playStopButton;

        public AudioViewHolder(@NonNull View itemView, final Listener listener) {
            super(itemView);
            this.listener = listener;

            songTitle = itemView.findViewById(R.id.song_title);
            author = itemView.findViewById(R.id.song_author);
            viewHolderLayout = itemView.findViewById(R.id.view_holder_layout);
            playStopButton = itemView.findViewById(R.id.play_stop_icon);


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
        }
    }

    public interface Listener {

        void onSongTapped(int selectedSong);
    }
}
