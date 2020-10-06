package com.anasdidi.bot.common;

import java.util.UUID;

public class AppUtils {

  public static String generateId() {
    return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
  }
}
