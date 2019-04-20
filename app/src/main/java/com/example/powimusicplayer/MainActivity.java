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
import Services.MediaService;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MediaPlayer mediaPlayer;
    ImageButton toggle, next, prev, stop;
    SeekBar songProgress;
    ArrayList<Song> songList;
    int position, maxPos;
    MediaService mediaService;

    //for the seekbar
    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                if (binding.getSongModel() != null) {
                    binding.getSongModel().setCurrentPosition(mediaPlayer);
                    binding.getSongModel().setPlaying(mediaPlayer.isPlaying());
                }
                mSeekbarUpdateHandler.postDelayed(this, 50);
            }
        };
        //mSeekbarUpdateHandler.post(mUpdateSeekbar);

        mediaPlayer = new MediaPlayer();

        mediaService = new MediaService("service", mediaPlayer, binding, mSeekbarUpdateHandler, mUpdateSeekbar);

        songList = new ArrayList<>();
        //setSongList();
        position = 0;

        toggle = findViewById(R.id.ToggleButton);
        next = findViewById(R.id.NextButton);
        prev = findViewById(R.id.PrevButton);
        stop = findViewById(R.id.StopButton);
        songProgress = findViewById(R.id.SongProgress);

        //prepareSong();

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
                mediaService.toggle();
                break;
            case R.id.NextButton:
                mediaService.nextSong();
                break;
            case R.id.PrevButton:
                mediaService.prevSong();
                break;
            case R.id.StopButton:
                mediaService.stopSong();
                break;

        }
    }
}
