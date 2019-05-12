package com.example.powimusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.AsyncTask;
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
import DTOs.Song;
import services.MediaService;
import services.OnClearFromRecentService;
import services.SongListViewAdapter;

//nox_adb.exe connect 127.0.0.1:62001 -- use Nox emulator instead, remember to turn on Nox first.


public class MainActivity extends AppCompatActivity implements View.OnClickListener, UpdateCallback {
    TextView error;
    ImageButton toggle, next, prev, stop;
    MediaService mediaService;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
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
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                if (hasStoragePermission()) doTasks();
            } else {
                doTasks();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            notificationManager.cancel(MUSIC_ID);
            isBound = false;
        }
    };

    private void createNotification(Song song, int toggleIconId) {
        PendingIntent bIntent = createButtonPendingIntent("BACK");
        PendingIntent tIntent = createButtonPendingIntent("TOGGLE");
        PendingIntent nIntent = createButtonPendingIntent("NEXT");

        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.setAction(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent mIntent = PendingIntent.getActivity(this, 0, mainIntent, 0);

        RemoteViews view = createRemoteView(song, bIntent, tIntent, nIntent, toggleIconId);
        RemoteViews expandView = createExpandRemoteView(song, bIntent, tIntent, nIntent, toggleIconId);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        musicNotification = builder
                            .setSmallIcon(R.drawable.logo)
                            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                            .setCustomContentView(view)
                            .setCustomBigContentView(expandView)
                            .setContentIntent(mIntent)
                            .setShowWhen(false)
                            .setOngoing(toggleIconId == R.drawable.ic_pause_button)
                            .build();
    }

    private PendingIntent createButtonPendingIntent(String action) {
        Intent backIntent = new Intent(this, MediaService.class);
        backIntent.setAction(action);
        return PendingIntent.getService(
                this,
                MUSIC_ID,
                backIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
    }

    private RemoteViews createRemoteView(Song song, PendingIntent backIntent, PendingIntent toggleIntent, PendingIntent nextIntent, int toggleIconId) {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.custom_notification);
        view.setTextViewText(R.id.notification_song_title, song.getName());
        view.setTextViewText(R.id.notification_song_author, song.getArtist());
        view.setImageViewResource(R.id.notification_toggle, toggleIconId);
        view.setOnClickPendingIntent(R.id.notification_back, backIntent);
        view.setOnClickPendingIntent(R.id.notification_toggle, toggleIntent);
        view.setOnClickPendingIntent(R.id.notification_next, nextIntent);
        return view;
    }

    private RemoteViews createExpandRemoteView(Song song, PendingIntent backIntent, PendingIntent toggleIntent, PendingIntent nextIntent, int toggleIconId) {
        RemoteViews expandView = new RemoteViews(getPackageName(), R.layout.custom_notification_expand);
        expandView.setTextViewText(R.id.notification_song_title_expand, song.getName());
        expandView.setTextViewText(R.id.notification_song_artist_expand, song.getArtist());
        expandView.setImageViewResource(R.id.notification_toggle_expand, toggleIconId);
        if (song.getAlbumArt() != null)
            expandView.setImageViewBitmap(R.id.notification_albumArt, song.getAlbumArt());
        expandView.setOnClickPendingIntent(R.id.notification_back_expand, backIntent);
        expandView.setOnClickPendingIntent(R.id.notification_toggle_expand, toggleIntent);
        expandView.setOnClickPendingIntent(R.id.notification_next_expand, nextIntent);
        return expandView;
    }

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
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Request Permission
        if(isAboveMarshmallowApi() && !hasStoragePermission()) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }

        Intent intent = new Intent(this, MediaService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

        syncSongModel();
        startService(new Intent(this, OnClearFromRecentService.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private NotificationChannel getNotificationChannel() {
        int importance = NotificationManager.IMPORTANCE_LOW;
        return new NotificationChannel(CHANNEL_ID, "MUSIC_CHANNEL", importance);
    }

    private boolean isAboveMarshmallowApi() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M;
    }

    private boolean isAboveOreoApi() {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O;
    }

    @RequiresApi(16)
    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(MainActivity.MUSIC_ID);
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

    @SuppressLint("StaticFieldLeak")
    private class ScanOperation extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mediaService.scanSongFromStorage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //recyclerView's tasks
            adapter = new SongListViewAdapter(mediaService.getSongs(), mediaService, MainActivity.this);
            recyclerView.setAdapter(adapter);

            //setOnClickListener's tasks
            if(mediaService.getSongs().size() > 0) {
                toggle.setOnClickListener(MainActivity.this);
                next.setOnClickListener(MainActivity.this);
                prev.setOnClickListener(MainActivity.this);
                stop.setOnClickListener(MainActivity.this);
                error.setText("");
            }
            else {
                error.setText("No music found");
            }
        }
    }

    private void doTasks() {
        if (!isBound) return;

        error.setText("Loading...");

        //Async task
        new ScanOperation().execute();
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
                mediaService.getCurrentSong(),
                mediaService.getMediaPlayer().isPlaying() ?
                        R.drawable.ic_pause_button :
                        R.drawable.ic_play_button);
        notificationManager.notify(MUSIC_ID, musicNotification);
    }
}
