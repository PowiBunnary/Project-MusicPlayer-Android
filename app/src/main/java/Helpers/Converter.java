package Helpers;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Converter {
    public static String ToTimeString(int position) {
        return String.format(Locale.ENGLISH, "%2d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(position),
                TimeUnit.MILLISECONDS.toSeconds(position) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(position)));
    }
}
