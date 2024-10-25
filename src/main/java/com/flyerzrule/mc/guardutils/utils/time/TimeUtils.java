package com.flyerzrule.mc.guardutils.utils.time;

import com.flyerzrule.mc.guardutils.utils.time.models.MinSec;

public class TimeUtils {
    public static MinSec getMinutesAndSecondsFromMilli(long time) {
        int minutes = (int) Math.floor(time / 1000 / 60);
        int seconds = (int) Math.floor((time / 1000) % 60);
        return new MinSec(minutes, seconds);
    }

    public static String getFormattedTimeFromSeconds(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;

        return String.format("%d:%d", hours, minutes);
    }
}
