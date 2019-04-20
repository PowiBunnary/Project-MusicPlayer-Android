package Services;

import android.app.IntentService;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.powimusicplayer.databinding.ActivityMainBinding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import Binders.SongModel;
import DTOs.Song;

public class MediaService extends IntentService {
    private MediaPlayer mediaPlayer;
    private ActivityMainBinding binder;
    private int position = 0;
    private boolean isComplete = false;
    private ArrayList<Song> songs;
    private Handler mSeekbarUpdateHandler;
    private Runnable mUpdateSeekbar;

    public MediaService(String name, MediaPlayer mediaPlayer, ActivityMainBinding binder, Handler mSeekbarUpdateHandler, Runnable mUpdateSeekbar) {
        super(name);
        this.mediaPlayer = mediaPlayer;
        this.binder = binder;
        this.mSeekbarUpdateHandler = mSeekbarUpdateHandler;
        this.mUpdateSeekbar = mUpdateSeekbar;
        this.mSeekbarUpdateHandler.post(mUpdateSeekbar);
        songs =  getPlayList(Environment.getExternalStorageDirectory().toString() + "@Download".replace('@','/'));
        /*mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });*/

        prepareSong();
    }

    private ArrayList<Song> getPlayList(String rootPath) {
        ArrayList<Song> fileList = new ArrayList<Song>();
        try {
            File rootFolder = new File(rootPath);
            File[] files = rootFolder.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    if (getPlayList(file.getAbsolutePath()) != null) {
                        fileList.addAll(getPlayList(file.getAbsolutePath())); //Recursive
                    } else {
                        break;
                    }
                } else if (file.getName().endsWith(".mp3")) {
                    Song song = new Song(file.getName(),file.getAbsolutePath());
                    fileList.add(song);
                }
            }
            return fileList;
        } catch (RuntimeException e) {
            throw e;
        }
    }

    private void prepareSong() {
        try {
            mediaPlayer.setDataSource(songs.get(position).getFile());
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (binder.getSongModel() == null) {
            binder.setSongModel(new SongModel(songs.get(position), mediaPlayer));
        } else {
            binder.getSongModel().setSong(songs.get(position), mediaPlayer);
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

    protected void onHandleIntent(Intent var1) {}
}
