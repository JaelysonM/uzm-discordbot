package com.uzm.core.utilities;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileUtils {

  public static JSONObject readUsingFileReader(File file) throws IOException {
   JSONObject content = new JSONObject();
   JSONArray array = new JSONArray();
    FileReader fr = new FileReader(file);
    BufferedReader br = new BufferedReader(fr);
    String line;
    boolean readingNames= false;
    while((line = br.readLine()) != null){
      String names = new String(line.getBytes(), StandardCharsets.UTF_8);
      if (readingNames) array.add(names);
      if (names.contains("tick:")) content.put("ticks", Integer.parseInt(names.split("tick:")[1].trim()));
      if (names.contains("random:")) content.put("random", Boolean.parseBoolean(names.split("random:")[1].trim()));

      if (names.contains("names:") ) readingNames= true;

    }
    content.put("names", array);
    br.close();
    fr.close();
    return content;
  }
}
