package com.anasdidi.bot.common;

import java.util.UUID;

public class AppUtils {

  public static String generateId() {
    return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
  }

  public static String getFormattedServerUptime(long duration) {
    long d = duration / AppConstants.TimeConversion.DayToMs.value;
    duration %= AppConstants.TimeConversion.DayToMs.value;

    long h = duration / AppConstants.TimeConversion.HourToMs.value;
    duration %= AppConstants.TimeConversion.HourToMs.value;

    long min = duration / AppConstants.TimeConversion.MinToMs.value;
    duration %= AppConstants.TimeConversion.MinToMs.value;

    long sec = duration / AppConstants.TimeConversion.SecToMs.value;
    duration %= AppConstants.TimeConversion.SecToMs.value;

    return String.format("%dd %dh %dmin %dsec", d, h, min, sec);
  }
}
