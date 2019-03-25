package com.example.powimusicplayer;

import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import DTOs.Song;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer;
    Button toggle, next, prev, stop;
    TextView songTitle, songDuration, timer;
    SeekBar songProgress;
    ArrayList<Song> songList;
    int position, maxPos;

    //for the seekbar
    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar = new Runnable() {
        @Override
        public void run() {
            int currentPos = mediaPlayer.getCurrentPosition();

            updateTimer();
            songProgress.setProgress(currentPos);
            mSeekbarUpdateHandler.postDelayed(this, 50);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songList = new ArrayList<Song>();
        setSongList();
        position = 0;

        songTitle = (TextView) findViewById(R.id.CurrentTitle);
        songDuration = (TextView) findViewById(R.id.SongLength);
        timer = (TextView) findViewById(R.id.StartTimer);
        toggle = (Button) findViewById(R.id.ToggleButton);
        next = (Button) findViewById(R.id.NextButton);
        prev = (Button) findViewById(R.id.PrevButton);
        stop = (Button) findViewById(R.id.StopButton);
        songProgress = (SeekBar) findViewById(R.id.SongProgress);

        prepareSong();
        //dunno about this, but it makes the song works with the SeekBar when first initialized
        playSong();
        mediaPlayer.pause();
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
        mediaPlayer.seekTo(0);

        toggle.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        stop.setOnClickListener(this);
        songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int trackingPos = seekBar.getProgress();
                String trackTime = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(trackingPos), TimeUnit.MILLISECONDS.toSeconds(trackingPos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(trackingPos)));
                timer.setText(trackTime);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mediaPlayer.isPlaying())
                    mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar,500);

                mediaPlayer.seekTo(seekBar.getProgress());
                updateTimer();

            }
        });

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.ToggleButton:
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                    toggle.setText("Play");
                }
                else {
                    playSong();
                    toggle.setText("Pause");
                }
                break;
            case R.id.NextButton:
                changeNextPos();
                if (mediaPlayer.isPlaying()){
                    //if song's still playing
                    mediaPlayer.stop();
                    prepareSong();
                    playSong();
                }
                //if nothing's touched yet
                else {
                    songProgress.setProgress(0);
                    prepareSong();
                }
                break;
            case R.id.PrevButton:
                if (position > 0)
                    position--;
                else position = maxPos;
                if (mediaPlayer.isPlaying()){
                    //if song's still playing
                    mediaPlayer.stop();
                    prepareSong();
                    playSong();
                }
                //if nothing's touched yet
                else {
                    songProgress.setProgress(0);
                    prepareSong();
                }
                break;
            case R.id.StopButton:
                    mediaPlayer.stop();
                    prepareSong();
                    toggle.setText("Play");
                    updateTimer();
                    songProgress.setProgress(0);
                break;

        }
    }

    protected void setSongList(){
        songList.add(new Song("Silent Story", R.raw.silent_story));
        songList.add(new Song("Flamingo", R.raw.flamingo));
        songList.add(new Song("Flower Dance", R.raw.flower_dance));
        songList.add(new Song("Faith", R.raw.faith));
        songList.add(new Song("Give It Up", R.raw.give_it_up));
        //Collections.sort(songList,String.CASE_INSENSITIVE_ORDER);
        maxPos = songList.size() - 1;
    }

    protected void prepareSong(){
        String str = "Current playing: ";
        mediaPlayer = MediaPlayer.create(MainActivity.this, songList.get(position).getFile());
        songTitle.setText(str + songList.get(position).getName());

        songProgress.setMax(mediaPlayer.getDuration());
        int duration = mediaPlayer.getDuration();
        //maybe in milliseconds makes the app overload?
        String time = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(duration), TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
        songDuration.setText(time);
    }

    protected void changeNextPos(){
        if (position < maxPos)
            position++;
        else position = 0;
    }

    protected void playSong(){
        //play the song
        mediaPlayer.start();

        mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 500);

        //change to next song if a song is completed
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                changeNextPos();
                prepareSong();
                try {
                    Thread.sleep(1500);
                    playSong();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    protected void updateTimer(){
        int currentPos = mediaPlayer.getCurrentPosition();
        String currentTime = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPos), TimeUnit.MILLISECONDS.toSeconds(currentPos) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPos)));
        timer.setText(currentTime);
    }
}