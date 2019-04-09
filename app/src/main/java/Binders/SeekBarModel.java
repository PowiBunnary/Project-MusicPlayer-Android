package Binders;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.powimusicplayer.BR;

import java.util.concurrent.TimeUnit;

public class SeekBarModel extends BaseObservable {
    private int currentPosition;
    private int seekBarPos;



    public SeekBarModel(int currentPosition) {
        seekBarPos = this.currentPosition = currentPosition;
    }




    @Bindable
    public String getCurrentPosition() {
        String currentTime = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(currentPosition), TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition)));
        return currentTime;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        notifyPropertyChanged(BR.currentPosition);
    }

    @Bindable
    public int getSeekBarPos() {
        return seekBarPos;
    }

    public void setSeekBarPos(int seekBarPos) {
        this.seekBarPos = seekBarPos;
        notifyPropertyChanged(BR.seekBarPos);
    }
}
