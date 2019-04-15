package Binders;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.media.MediaPlayer;
import android.widget.SeekBar;

import com.example.powimusicplayer.BR;

import DTOs.Song;

public class SongModel extends BaseObservable {

    private Song song;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private boolean isTouching;

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

    @Bindable
    public int getCurrentPosition() {
        return song.getCurrentPosition();
    }

    public void setCurrentPosition(int currentPosition) {
        if (song.getCurrentPosition() != currentPosition) {
            song.setCurrentPosition(currentPosition);
            if (!isTouching) mediaPlayer.seekTo(currentPosition);
            notifyPropertyChanged(BR.currentPosition);
        }
    }

    public void setCurrentPosition(MediaPlayer mediaPlayer) {
        if (!isTouching && song.getCurrentPosition() != mediaPlayer.getCurrentPosition()) {
            song.setCurrentPosition(mediaPlayer.getCurrentPosition());
            notifyPropertyChanged(BR.currentPosition);
        }
    }

    public boolean isTouching() {
        return isTouching;
    }
    public void setTouching(boolean touching) {
        isTouching = touching;
    }

    public void onStopTouching(SeekBar seekBar) {
        mediaPlayer.seekTo(seekBar.getProgress());
        isTouching = false;
    }

    @Bindable
    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
        notifyPropertyChanged(BR.playing);
    }
}
