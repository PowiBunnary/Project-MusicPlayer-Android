package services;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.powimusicplayer.R;

import java.util.ArrayList;

import DTOs.Song;

public class SongListViewAdapter extends RecyclerView.Adapter<SongListViewAdapter.RecyclerViewHolder> {
    private ArrayList<Song> songs;
    private MediaService mediaService;
    private View.OnClickListener onActivityClickListener;

    public SongListViewAdapter(ArrayList<Song> songs, MediaService mediaService, View.OnClickListener onActivityClickListener) {
        this.songs = songs;
        this.mediaService = mediaService;
        this.onActivityClickListener = onActivityClickListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.song_list_layout, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        holder.title.setText(songs.get(position).getName());
        holder.artistAndDuration.setText(songs.get(position).getArtistAndDurationTimeStr());
        if (songs.get(position).getAlbumArt() != null)
            holder.albumArt.setImageBitmap(songs.get(position).getAlbumArt());
        else
            holder.albumArt.setImageResource(R.drawable.ic_no_image);
        if(position == mediaService.getPosition()) {
            holder.title.setTextColor(Color.parseColor("#42cef4"));
        }
        else holder.title.setTextColor(Color.parseColor("#cc30dd"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setTag(R.id.songListView, holder.getAdapterPosition());
                onActivityClickListener.onClick(v);
                //notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView artistAndDuration;
        ImageView albumArt;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.songTitle);
            artistAndDuration = itemView.findViewById(R.id.artistAndDuration);
            albumArt = itemView.findViewById(R.id.albumArt);
        }
    }
}
