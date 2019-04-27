package services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import com.example.powimusicplayer.UpdateCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import DTOs.Song;

public class MediaService extends Service {
    private final MediaPlayer mediaPlayer = new MediaPlayer();
    private int position = 0;
    private ArrayList<Song> songs;
    private final IBinder binder = new LocalBinder();
    UpdateCallback callback;

    public class LocalBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }

    private ArrayList<Song> getPlayList(String rootPath) {
        ArrayList<Song> fileList = new ArrayList<>();
        File rootFolder = new File(rootPath);
        File[] files = rootFolder.listFiles();
        if (files != null)
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath())); //Recursive
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    String name = file.getName();
                    if(name.length() > 4)
                        name = name.substring(0, name.length() - 4);

                    Song song = new Song(name,file.getAbsolutePath());
                    try {
                        mediaPlayer.setDataSource(song.getFile());
                        mediaPlayer.prepare();
                        song.setDuration(mediaPlayer.getDuration());
                        mediaPlayer.reset();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileList.add(song);
                }
            }
        return fileList;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void scanSongFromStorage() {
        songs =  getPlayList(Environment.getExternalStorageDirectory().toString() + "@Download".replace('@','/'));
    }

    private void prepareSong() {
        if (songs.size() == 0) return;
        try {
            mediaPlayer.setDataSource(songs.get(position).getFile());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void playSong(){

        //play the song
        mediaPlayer.start();

        //change to next song if a song is completed
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                try {
                    nextSong();
                    playSong();
                    callback.updateNotification();
                    callback.updateModel();
                    callback.updateRecycler();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    private void changeNextPos(){
        if (position < songs.size() - 1)
            position++;
        else position = 0;
    }

    //Events
    public void toggle() {
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
        else {
            playSong();
        }
    }

    public Song getCurrentSong() {
        return songs.get(position);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public void nextSong() {
        changeNextPos();
        if (mediaPlayer.isPlaying()){
            //if song's still playing
            mediaPlayer.reset();
            prepareSong();
            playSong();
        }
        //if nothing's touched yet
        else {
            mediaPlayer.reset();
            prepareSong();
        }
    }

    public void prevSong() {
        if (position > 0)
            position--;
        else position = songs.size() - 1;
        if (mediaPlayer.isPlaying()) {
            //if song's still playing
            mediaPlayer.reset();
            prepareSong();
            playSong();
        }
        //if nothing's touched yet
        else {
            mediaPlayer.reset();
            prepareSong();
        }
    }

    public void stopSong() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        prepareSong();
    }

    public void goToSong(int pos) {
        mediaPlayer.reset();
        position = pos;
        prepareSong();
        playSong();
    }

    public int getPosition() {
        return position;
    }

    public void setCallback(UpdateCallback callback) {
        this.callback = callback;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "BACK":
                    prevSong();
                    break;
                case "TOGGLE":
                    toggle();
                    break;
                case "NEXT":
                    nextSong();
                    break;
                default:
                    throw new RuntimeException("Unhandled case");
            }
            callback.updateNotification();
            callback.updateModel();
            callback.updateRecycler();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}
