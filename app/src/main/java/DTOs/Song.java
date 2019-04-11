package DTOs;

import android.media.MediaPlayer;

public class Song {
    private String name;
    private int file;
    private int duration;
    private int currentPosition;


    public Song(String name, int file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFile() {
        return file;
    }

    public void setFile(int file) {
        this.file = file;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
    }

    public void setDurationAndPosition(MediaPlayer mediaPlayer) {
        currentPosition = mediaPlayer.getCurrentPosition();
        duration = mediaPlayer.getDuration();
    }
}
