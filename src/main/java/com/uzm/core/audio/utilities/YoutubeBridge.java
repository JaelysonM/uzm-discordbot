  
/*
 * Copyright 2017 github.com/kaaz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.uzm.core.audio.utilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.common.collect.Lists;
import com.uzm.core.configuration.JSONConfiguration;
import com.uzm.core.utilities.Manager;

/**
 * Helper class to search for tracks on youtube
 */
public class YoutubeBridge {
  private final YouTube youtube;
  private final YouTube.Search.List search;
  private final ConcurrentHashMap<String, SimpleResult> cache = new ConcurrentHashMap<>();
  private final Queue<String> keyQueue;
  private volatile boolean hasValidKey = true;
  public static String[] GOOGLE_API_KEY = {JSONConfiguration.YOUTUBE_TOKEN};

  public YoutubeBridge() {
    keyQueue = new LinkedList<>();
    Collections.addAll(keyQueue, GOOGLE_API_KEY);
    youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), (HttpRequest request) -> {
    }).setApplicationName("BaraoBot-youtube-search").build();
    YouTube.Search.List tmp = null;
    try {
      tmp = youtube.search().list(Lists.newArrayList("id,snippet"));
      tmp.setOrder("relevance");
      tmp.setVideoCategoryId("10");

    } catch (IOException ex) {
      System.err.println("Failed to initialize search: " + ex.toString());
    }

    search = tmp;
    if (search != null) {
      search.setType(Lists.newArrayList("video"));
      search.setFields("items(id/kind,id/videoId,snippet/title)");
    }
    setupNextKey();
  }

  public boolean hasValidKey() {
    return hasValidKey;
  }

  public synchronized void addYoutubeKey(String key) {
    keyQueue.add(key);
    hasValidKey = true;
  }

  private synchronized boolean setupNextKey() {
    if (keyQueue.size() > 0) {
      String key = keyQueue.poll();
      if (key != null) {
        search.setKey(key);
        hasValidKey = true;
        return true;
      }
    }
    hasValidKey = false;
    return false;
  }

  public SimpleResult getResults(String query) {
    String queryName = query.trim().toLowerCase();
    if (cache.containsKey(queryName)) {
      return cache.get(queryName);
    }
    List<SimpleResult> results = getResults(query, 1);
    if (results != null && !results.isEmpty()) {
      cache.put(queryName, results.get(0));
      return results.get(0);
    }
    return null;
  }

  public List<SimpleResult> getPlayListItems(String playlistCode) {
    List<SimpleResult> playlist = new ArrayList<>();
    try {
      YouTube.PlaylistItems.List playlistRequest = youtube.playlistItems().list(Lists.newArrayList("id,contentDetails,snippet"));
      playlistRequest.setPlaylistId(playlistCode);
      playlistRequest.setKey(search.getKey());
      playlistRequest.setFields("items(contentDetails/videoId,snippet/title,snippet/publishedAt),nextPageToken,pageInfo");
      String nextToken = "";
      do {
        playlistRequest.setPageToken(nextToken);
        PlaylistItemListResponse playlistItemResult = playlistRequest.execute();
        playlist.addAll(
          playlistItemResult.getItems().stream().map(playlistItem -> new SimpleResult(playlistItem.getContentDetails().getVideoId(), playlistItem.getSnippet().getTitle()))
            .collect(Collectors.toList()));
        nextToken = playlistItemResult.getNextPageToken();
      } while (nextToken != null);

    } catch (IOException e) {
      e.printStackTrace();
    }
    return playlist;
  }

  public List<SimpleResult> getResults(String query, int numresults) {
    List<SimpleResult> urls = new ArrayList<>();
    search.setQ(query);
    search.setMaxResults((long) numresults);
    SearchListResponse searchResponse;
    try {
      searchResponse = search.execute();
      List<SearchResult> searchResultList = searchResponse.getItems();
      searchResultList.forEach((sr) ->



        urls.add(new SimpleResult(sr.getId().getVideoId(), sr.getSnippet().getTitle())));
    } catch (GoogleJsonResponseException e) {
      if (e.getMessage().contains("quotaExceeded") || e.getMessage().contains("keyInvalid")) {
        if (setupNextKey()) {
          return getResults(query, numresults);
        }
      } else {
        System.err.println("YTSearch failure: " + e.getDetails().getCode() + " / " + e.getDetails().getMessage());
      }
    } catch (IOException ex) {
      System.err.println("YTSearch failure: " + ex.toString());
      return null;
    }
    return urls;
  }

  public void resetCache() {
    cache.clear();
  }

  public class SimpleResult {
    private final String code;
    private final String title;

    public SimpleResult(String code, String title) {
      this.code = code;
      this.title = title;
    }

    public String getTitle() {
      return title;
    }

    public String getCode() {
      return code;
    }
  }
}