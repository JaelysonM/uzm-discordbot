package com.uzm.core.configuration;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONConfiguration {

  public static String BOT_TOKEN;
  public static String YOUTUBE_TOKEN;

  public static boolean load() {
    JSONParser parser = new JSONParser();

    try {
      Object obj = parser.parse(new FileReader("config.json"));

      JSONObject jsonObject = (JSONObject) obj;

      if (jsonObject.get("bot-token") != null && jsonObject.get("youtube-token") != null) {
        BOT_TOKEN = (String) jsonObject.get("bot-token");
        YOUTUBE_TOKEN = (String) jsonObject.get("youtube-token");

        return true;
      } else {
        return false;
      }


    } catch (IOException | ParseException e) {
      try {
        FileWriter writer = new FileWriter("config.json");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bot-token", null);
        jsonObject.put("youtube-token", null);

        writer.write(jsonObject.toJSONString());
      } catch (IOException err) {}
      return false;
    }
  }

}
