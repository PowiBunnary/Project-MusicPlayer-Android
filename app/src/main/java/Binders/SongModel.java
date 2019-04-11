package Binders;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.MediaPlayer;

import com.example.powimusicplayer.BR;

import DTOs.Song;

public class SongModel extends BaseObservable {

    private Song song;
    private MediaPlayer mediaPlayer;

    public SongModel(Song song, MediaPlayer mediaPlayer) {
        this.song = song;
        song.setDuration(mediaPlayer.getDuration());
        this.mediaPlayer = mediaPlayer;
    }

    public void setSong(Song song, MediaPlayer mediaPlayer) {
        this.song = song;
        this.mediaPlayer = mediaPlayer;
        this.song.setDurationAndPosition(mediaPlayer);
        notifyPropertyChanged(BR.name);
        notifyPropertyChanged(BR.duration);
        notifyPropertyChanged(BR.currentPosition);
    }

    @Bindable
    public String getName() {
        return song.getName();
    }

    public void setName(String name) {
        if (!this.song.getName().equals(name)) {
            song.setName(name);
            notifyPropertyChanged(BR.name);
        }
    }

    @Bindable
    public int getDuration() {
        return song.getDuration();
    }

    public void setDuration(int duration) {
        if (song.getDuration() != duration) {
            this.song.setDuration(duration);
            notifyPropertyChanged(BR.duration);
        }
    }
    public void setDuration(MediaPlayer mediaPlayer) {
        if (song.getDuration() != mediaPlayer.getDuration()) {
            this.song.setDuration(mediaPlayer.getDuration());
            notifyPropertyChanged(BR.duration);
        }
    }

    @Bindable
    public int getCurrentPosition() {
        return song.getCurrentPosition();
    }

    public void setCurrentPosition(int currentPosition) {
        if (song.getCurrentPosition() != currentPosition) {
            song.setCurrentPosition(currentPosition);
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(currentPosition);
            }
            notifyPropertyChanged(BR.currentPosition);
        }
    }

    public void setCurrentPosition(MediaPlayer mediaPlayer) {
        if (song.getCurrentPosition() != mediaPlayer.getCurrentPosition()) {
            song.setCurrentPosition(mediaPlayer.getCurrentPosition());
            notifyPropertyChanged(BR.currentPosition);
        }
    }
}
