package com.example.powimusicplayer;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.example.powimusicplayer.databinding.ActivityMainBinding;

import Binders.SongModel;
import services.MediaService;
import services.SongListViewAdapter;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener, UpdateCallback {
    TextView error;
    ImageButton toggle, next, prev, stop;
    MediaService mediaService;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    RecyclerView.LayoutManager layoutManager;
    ActivityMainBinding binding;
    boolean isBound = false;

    NotificationManager notificationManager;
    Notification musicNotification;
    public static final String CHANNEL_ID = "MUSIC_CHANNEL";
    public static final int MUSIC_ID = 0;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MediaService.LocalBinder binder = (MediaService.LocalBinder) service;
            mediaService = binder.getService();
            mediaService.setCallback(MainActivity.this);
            isBound = true;
            if (hasStoragePermission()) doTasks();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };

    private void createNotification(String songName, String songAuthor, int toggleIconId) {
        Intent backIntent = new Intent(this, MediaService.class);
        backIntent.setAction("BACK");
        PendingIntent pIntent = PendingIntent.getService(
                this,
                MUSIC_ID,
                backIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent toggleIntent = new Intent(this, MediaService.class);
        toggleIntent.setAction("TOGGLE");
        PendingIntent tIntent = PendingIntent.getService(
                this,
                MUSIC_ID,
                toggleIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Intent nextIntent = new Intent(this, MediaService.class);
        nextIntent.setAction("NEXT");
        PendingIntent nIntent = PendingIntent.getService(
                this,
                MUSIC_ID,
                nextIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews view = new RemoteViews(getPackageName(), R.layout.custom_notification);
        view.setTextViewText(R.id.notification_song_title, songName);
        view.setTextViewText(R.id.notification_song_author, songAuthor);
        view.setImageViewResource(R.id.notification_toggle, toggleIconId);
        view.setOnClickPendingIntent(R.id.notification_back, pIntent);
        view.setOnClickPendingIntent(R.id.notification_toggle, tIntent);
        view.setOnClickPendingIntent(R.id.notification_next, nIntent);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        musicNotification = builder
                            .setSmallIcon(R.drawable.logo)
                            .setCustomContentView(view)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setShowWhen(false)
                            .setOngoing(toggleIconId == R.drawable.ic_pause_button)
                            .build();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		if (isAboveOreoApi()) {
            notificationManager.createNotificationChannel(getNotificationChannel());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

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
        if(!hasStoragePermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        syncSongModel();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "MUSIC_CHANNEL", importance);
        return notificationChannel;
    }

    private boolean isAboveOreoApi() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O;
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(MUSIC_ID);
        unbindService(connection);
    }

    @Override
    public void onClick(View v){
        if (!isBound) return;

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
            case R.id.songListView:
                int pos = (int) v.getTag(R.id.songListView);
                mediaService.goToSong(pos);
                break;
            default:
                throw new RuntimeException("Unhandled case");
        }
        updateModel();
        updateNotification();
        updateRecycler();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doTasks();
                } else {
                    error.setText("Can't read storage");
                }
        }
    }

    // Call this function only once
    private void syncSongModel() {
        final Handler mSeekbarUpdateHandler = new Handler();
        Runnable mUpdateSeekbar = new Runnable() {
            @Override
            public void run() {
                if (binding.getSongModel() != null) {
                    binding.getSongModel().setCurrentPosition(mediaService.getMediaPlayer());
                    binding.getSongModel().setPlaying(mediaService.getMediaPlayer().isPlaying());
                }
                mSeekbarUpdateHandler.postDelayed(this, 50);
            }
        };
        mSeekbarUpdateHandler.post(mUpdateSeekbar);
    }

    private void doTasks() {
        if (!isBound) return;

        //mediaService's tasks
        mediaService.scanSongFromStorage();

        //recyclerView's tasks
        adapter = new SongListViewAdapter(mediaService.getSongs(), mediaService, this);
        recyclerView.setAdapter(adapter);

        //setOnClickListener's tasks
        if(mediaService.getSongs().size() > 0) {
            toggle.setOnClickListener(this);
            next.setOnClickListener(this);
            prev.setOnClickListener(this);
            stop.setOnClickListener(this);
            error.setText("");
        }
        else {
            error.setText("No music found");
        }
    }

    @Override
    public void updateModel() {
        if (binding.getSongModel() != null) {
            binding.getSongModel().setSong(mediaService.getCurrentSong(), mediaService.getMediaPlayer());
        } else {
            binding.setSongModel(new SongModel(mediaService.getCurrentSong(), mediaService.getMediaPlayer()));
        }
    }

    @Override
    public void updateRecycler() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void updateNotification() {
        createNotification(
                mediaService.getCurrentSong().getName(),
                mediaService.getCurrentSong().getName(),
                mediaService.getMediaPlayer().isPlaying() ?
                        R.drawable.ic_pause_button :
                        R.drawable.ic_play_button);
        notificationManager.notify(MUSIC_ID, musicNotification);
    }
}
