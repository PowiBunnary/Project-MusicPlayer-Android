package Services;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.powimusicplayer.R;

import java.util.ArrayList;

import DTOs.Song;
import Helpers.Converter;

public class SongListViewAdapter extends RecyclerView.Adapter<SongListViewAdapter.RecyclerViewHolder> {
    private ArrayList<Song> songs;
    MediaService mediaService;

    public SongListViewAdapter(ArrayList<Song> songs, MediaService mediaService) {
        this.songs = songs;
        this.mediaService = mediaService;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_list_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, final int position) {
        holder.title.setText(songs.get(position).getName());
        holder.duration.setText(Converter.ToTimeString(songs.get(position).getDuration()));
        if(position == mediaService.getPosition()) {
            holder.title.setTextColor(Color.parseColor("#42cef4"));
        }
        else holder.title.setTextColor(Color.parseColor("#cc30dd"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaService.goToSong(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView duration;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            duration = itemView.findViewById(R.id.duration);
        }
    }
}
