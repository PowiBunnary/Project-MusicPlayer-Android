package Binders;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.example.powimusicplayer.BR;

import java.util.concurrent.TimeUnit;

public class SongNameModel extends BaseObservable {
    private String fileName;
    private int fileDuration;
    private int maxSeekerLength;

    public SongNameModel(String fileName, int fileDuration) {
        this.fileName = fileName;
        maxSeekerLength = this.fileDuration = fileDuration;
    }

    @Bindable
    public String getFileName() {
        return "Current playing: " + fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
        notifyPropertyChanged(BR.fileName);
    }

    @Bindable
    public String getFileDuration() {
        String maxTime = String.format("%2d:%02d", TimeUnit.MILLISECONDS.toMinutes(fileDuration), TimeUnit.MILLISECONDS.toSeconds(fileDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(fileDuration)));
        return maxTime;
    }

    public void setFileDuration(int fileDuration) {
        this.fileDuration = fileDuration;
        notifyPropertyChanged(BR.fileDuration);
    }

    @Bindable
    public int getMaxSeekerLength() {
        return maxSeekerLength;
    }

    public void setMaxSeekerLength(int maxSeekerLength) {
        this.maxSeekerLength = maxSeekerLength;
        notifyPropertyChanged(BR.maxSeekerLength);
    }
}
