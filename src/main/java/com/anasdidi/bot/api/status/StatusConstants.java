package com.anasdidi.bot.api.status;

class StatusConstants {

  enum ServerPing {
    Security("https://api.anasdidi.dev/security/ping"), Bot("https://api.anasdidi.dev/bot/ping"),
    Budget("https://api.anasdidi.dev/budget/ping");

    String value;

    private ServerPing(String value) {
      this.value = value;
    }
  }
}
