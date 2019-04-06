package com.example.powimusicplayer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.widget.Toast;

import java.util.List;

public class MusicPlayerService extends IntentService {

    public static final String BACK_ACTION = "back";
    public static final String NEXT_ACTION = "next";
    public static final String TOGGLE_ACTION = "toggle";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MusicPlayerService(String name) {
        super(name);
    }

    public MusicPlayerService() {
        super("music-player");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        throw new UnsupportedOperationException();
    }
}
