package com.anasdidi.bot.common;

import io.vertx.core.json.JsonObject;

public class TelegramVO {

  private final JsonObject json;

  public TelegramVO(JsonObject json) {
    this.json = json;
  }

  public int getMessageFromId() {
    return json.getJsonObject("message").getJsonObject("from").getInteger("id");
  }

  public String getMessageFromFirstname() {
    return json.getJsonObject("message").getJsonObject("from").getString("first_name");
  }
}
