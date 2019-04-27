package com.example.powimusicplayer;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.powimusicplayer.databinding.ActivityMainBinding;

import services.MediaService;
import services.SongListViewAdapter;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    MediaPlayer mediaPlayer;
    TextView error;
    ImageButton toggle, next, prev, stop;
    MediaService mediaService;
    boolean mBound = false;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ActivityMainBinding binding;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            mediaService = binder.getService();
            mediaService.setup(binding, mediaPlayer);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //for the seekbar
    private Handler mSeekbarUpdateHandler = new Handler();
    private Runnable mUpdateSeekbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mediaPlayer = new MediaPlayer();

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
        mSeekbarUpdateHandler.post(mUpdateSeekbar);

        toggle = findViewById(R.id.ToggleButton);
        next = findViewById(R.id.NextButton);
        prev = findViewById(R.id.PrevButton);
        stop = findViewById(R.id.StopButton);
        recyclerView = findViewById(R.id.songListView);

        error = findViewById(R.id.errorText);

        //RecyclerView
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Request Permission
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }
        else {
            doTasks();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
        mBound = false;
    }

    @Override
    public void onClick(View v){
        if (!mBound) return;

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
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doTasks();
                }
        }
    }

    private void doTasks() {
        if (!mBound) return;

        //recyclerView's tasks
        adapter = new SongListViewAdapter(mediaService.getSongs(), mediaService);
        recyclerView.setAdapter(adapter);

        //setOnClickListener's tasks
        if(mediaService.getSongs().size() > 0) {
            toggle.setOnClickListener(this);
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            stop.setOnClickListener(this);
        }
        else {
            error.setText("No music found");
        }
    }
}
