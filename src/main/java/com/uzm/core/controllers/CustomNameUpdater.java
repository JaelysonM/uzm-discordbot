package com.uzm.core.controllers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.uzm.core.utilities.FileUtils;
import net.dv8tion.jda.api.entities.Member;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class
CustomNameUpdater {

  private long longId;
  private List<String> names;
  private int frame;

  private String currentName;
  private int ticks;

  public int tick;
  private int currentFrame;
  private boolean random;
  private static LinkedHashMap<Long, CustomNameUpdater> data = new LinkedHashMap<>();


  public int getTicks() {
    return ticks;
  }

  public CustomNameUpdater(boolean random,long longId, int ticks, String... names) {
    this.longId = longId;
    this.random= random;
    this.ticks = ticks;
    this.names = Lists.newArrayList(names);
    this.tick = ticks;
  }



  public String getCurrentName() {
    return currentName;
  }

  public int getFrame() {
    return frame;
  }

  public List<String> getNames() {
    return names;
  }

  public long getLongId() {
    return longId;
  }

  public String pushNames() {
    if (random) {
      Collections.shuffle(names);
      String name = names.get(ThreadLocalRandom.current().nextInt(names.size()));
      currentName = name;
      return name;
    } else {
      String name;
      if (currentFrame < names.size()) {
        name = getNames().get(currentFrame++);
      } else {
        currentFrame = 0;
        name = getNames().get(0);
      }
      return name;
    }

  }

  public void destroy() {
    this.frame = 0;
    this.names.clear();
    this.names = null;
    this.longId = 0L;
  }

  public static void loadApplicableMembers() {
    try (Stream<Path> paths = Files.walk(Paths.get("custom-names"))) {
      paths.filter(Files::isRegularFile).forEach(file -> {
        try {
          JSONObject names = FileUtils.readUsingFileReader(file.toFile());
          JSONArray array = (JSONArray) names.get("names");
          List<String> list = (List<String>) array.stream().map(Object::toString).collect(Collectors.toList());

          create(names.get("random") == null || (Boolean)names.get("random"),Long.parseLong(file.getFileName().toString().replace(".txt", "")), names.get("ticks") == null ? 20 * 30 : (Integer) names.get("ticks"),
            list.toArray(new String[] {}));

        } catch (IOException e) {
          e.printStackTrace();
        }

      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isRandom() {
    return random;
  }

  public static ImmutableList<CustomNameUpdater> list() {
    return ImmutableList.copyOf(data.values());
  }

  public static void clearAll() {
    data.values().forEach(CustomNameUpdater::destroy);
    data.clear();
  }

  public static CustomNameUpdater get(long longId) {
    return data.getOrDefault(longId, null);
  }

  public static CustomNameUpdater get(Member member) {
    return get(member.getIdLong());
  }

  public static void create(boolean random,long longId, int tick, String... names) {
    data.computeIfAbsent(longId, item -> new CustomNameUpdater(random,longId, tick, names));
  }

}
