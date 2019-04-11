package com.example.powimusicplayer;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.powimusicplayer.databinding.ActivityMainBinding;

import java.util.ArrayList;

import Binders.SongModel;
import DTOs.Song;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer;
    ImageButton toggle, next, prev, stop;
    SeekBar songProgress;
    ArrayList<Song> songList;
    int position, maxPos;

    //for the seekbar
    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar;

    //DataBinder
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                if (binding.getSongModel() != null) {
                    binding.getSongModel().setCurrentPosition(mediaPlayer);
                }
                mSeekbarUpdateHandler.postDelayed(this, 50);
            }
        };
        mSeekbarUpdateHandler.post(mUpdateSeekbar);

        songList = new ArrayList<>();
        setSongList();
        position = 0;

        toggle = findViewById(R.id.ToggleButton);
        next = findViewById(R.id.NextButton);
        prev = findViewById(R.id.PrevButton);
        stop = findViewById(R.id.StopButton);
        songProgress = findViewById(R.id.SongProgress);

        prepareSong();

        toggle.setOnClickListener(this);
        next.setOnClickListener(this);
        prev.setOnClickListener(this);
        stop.setOnClickListener(this);

    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id) {
            case R.id.ToggleButton:
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                    toggle.setBackgroundResource(R.drawable.ic_play_button);
                }
                else {
                    playSong();
                    toggle.setBackgroundResource(R.drawable.ic_pause_button);
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
                    prepareSong();
                }
                break;
            case R.id.StopButton:
                mediaPlayer.stop();
                prepareSong();
                toggle.setBackgroundResource(R.drawable.ic_play_button);
                break;

        }
    }

    protected void setSongList(){
        songList.add(new Song("Silent Story", R.raw.silent_story));
        songList.add(new Song("Flamingo", R.raw.flamingo));
        songList.add(new Song("Flower Dance", R.raw.flower_dance));
        songList.add(new Song("Faith", R.raw.faith));
        songList.add(new Song("Give It Up", R.raw.give_it_up));
        maxPos = songList.size() - 1;
    }

    protected void prepareSong(){
        mediaPlayer = MediaPlayer.create(MainActivity.this, songList.get(position).getFile());
        if (binding.getSongModel() == null) {
            binding.setSongModel(new SongModel(songList.get(position), mediaPlayer));
        } else {
            binding.getSongModel().setSong(songList.get(position), mediaPlayer);
        }

    }

    protected void changeNextPos(){
        if (position < maxPos)
            position++;
        else position = 0;
    }

    protected void playSong(){
        //play the song
        mediaPlayer.start();

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
}
