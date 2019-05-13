package Helpers;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Converter {
    public static String ToTimeString(int millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        return String.format(Locale.ENGLISH, "%2d:%02d",
                minutes,
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                TimeUnit.MINUTES.toSeconds(minutes));
    }
}
