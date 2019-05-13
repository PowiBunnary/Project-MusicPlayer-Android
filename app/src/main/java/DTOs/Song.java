package DTOs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;

import Helpers.Converter;

public class Song {
    private static final MediaMetadataRetriever mediaRetriever = new MediaMetadataRetriever();

    private String name;
    private String artist;
    private Bitmap albumArt;
    private String file;
    private int duration;
    private int currentPosition;


    public Song(String file, String fileName) {
        this.file = file;
        mediaRetriever.setDataSource(file);
        this.name = mediaRetriever.extractMetadata((MediaMetadataRetriever.METADATA_KEY_TITLE));
        if (this.name == null) this.name = fileName;
        this.artist = mediaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        byte[] albumArtBytes = mediaRetriever.getEmbeddedPicture();
        if (albumArtBytes != null)
            this.albumArt = BitmapFactory.decodeByteArray(albumArtBytes, 0, albumArtBytes.length);
        if (artist == null) this.artist = "Unknown Artist";
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

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistAndDurationTimeStr() {
        return artist + " - " + Converter.ToTimeString(duration);
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }
}
