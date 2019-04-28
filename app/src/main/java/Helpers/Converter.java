package Helpers;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Converter {
    public static String ToTimeString(int millis) {
        return String.format(Locale.ENGLISH, "%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
