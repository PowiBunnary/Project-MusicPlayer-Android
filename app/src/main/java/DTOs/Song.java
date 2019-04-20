package DTOs;

import android.media.MediaPlayer;

public class Song {
    private String name;
    private String file;
    private int duration;
    private int currentPosition;


    public Song(String name, String file) {
        this.name = name;
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
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
