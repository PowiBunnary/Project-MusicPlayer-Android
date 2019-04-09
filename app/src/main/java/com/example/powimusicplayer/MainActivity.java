package com.example.powimusicplayer;

import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.powimusicplayer.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import Binders.SeekBarModel;
import Binders.SongNameModel;
import DTOs.Song;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    MediaPlayer mediaPlayer;
    ImageButton toggle, next, prev, stop;
    TextView songTitle, songDuration, timer;
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

        mUpdateSeekbar  = new Runnable() {
            @Override
            public void run() {
                int currentPos = mediaPlayer.getCurrentPosition();
                binding.setSeekModel(new SeekBarModel(currentPos));
                mSeekbarUpdateHandler.postDelayed(this, 50);
            }
        };

        songList = new ArrayList<Song>();
        setSongList();
        position = 0;

        songTitle = (TextView) findViewById(R.id.CurrentTitle);
        songDuration = (TextView) findViewById(R.id.SongLength);
        timer = (TextView) findViewById(R.id.StartTimer);
        toggle = (ImageButton) findViewById(R.id.ToggleButton);
        next = (ImageButton) findViewById(R.id.NextButton);
        prev = (ImageButton) findViewById(R.id.PrevButton);
        stop = (ImageButton) findViewById(R.id.StopButton);
        songProgress = (SeekBar) findViewById(R.id.SongProgress);

        prepareSong();
        //dunno about this, but it makes the song works with the SeekBar when first initialized
        playSong();
        mediaPlayer.pause();
        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
        binding.setSeekModel(new SeekBarModel(0));

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
                binding.setSeekModel(new SeekBarModel(mediaPlayer.getCurrentPosition()));

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
                    binding.setSeekModel(new SeekBarModel(0));
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
                    binding.setSeekModel(new SeekBarModel(0));
                    prepareSong();
                }
                break;
            case R.id.StopButton:
                mediaPlayer.stop();
                prepareSong();
                toggle.setBackgroundResource(R.drawable.ic_play_button);
                binding.setSeekModel(new SeekBarModel(0));
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
        binding.setSongModel(new SongNameModel(songList.get(position).getName(),mediaPlayer.getDuration()));
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
}
