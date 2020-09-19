package com.uzm.core;

import java.util.*;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.uzm.core.audio.events.TrackListener;
import com.uzm.core.audio.track.TrackPlayer;
import com.uzm.core.audio.utilities.YoutubeBridge.SimpleResult;
import com.uzm.core.controllers.CustomNameUpdater;
import com.uzm.core.utilities.Manager;
import com.uzm.core.utilities.Manager.Ranks;

import com.uzm.core.utilities.StringUtils;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;



public class DiscordBot {

  public enum TypeSearch {
    SPOTIFY,
    YOUTUBE
  }


  public VoiceChannel music;
  public Category displayc;
  public Message searchmessage;
  public List<SimpleResult> searchlist;
  public HashMap<Message, TypeSearch> messagecache = Maps.newHashMap();
  public List<TextChannel> channels;
  public TextChannel defaultmusic;
  public Category games;
  public TextChannel shop;

  public List<String> helioNames;


  public static List<String> ranks = new ArrayList<>();

  public HashMap<Member, Ranks> group;


  public Message getSearch() {
    return searchmessage;
  }

  public TextChannel getDefaultMusic() {
    return defaultmusic;
  }


  public TypeSearch getMessageCache(Message m) {
    return messagecache.getOrDefault(m, null);

  }


  public TextChannel getShop() {
    return shop;
  }

  public Category getGames() {
    return games;
  }

  public List<SimpleResult> getResultYoutube() {
    return searchlist;
  }

  public HashMap<Member, Ranks> getUsers() {
    return group;
  }

  public void setSearch(Message c, TypeSearch s) {
    searchmessage = c;
    messagecache.put(c, s);
  }

  public void destroyCache() {
    searchmessage = null;

  }

  public Ranks getGroup(Member m) {
    if (m.getUser().getId().equalsIgnoreCase("280474473856237569")) {
      group.put(m, Ranks.UZM);
      return group.get(m);
    }
    if (group.get(m) != null) {
      return group.get(m);

    } else {
      if (m.getRoles().size() > 0) {
        for (Ranks r : Ranks.values()) {
          if (r.toString().replace("_", " ").equalsIgnoreCase(m.getRoles().get(0).getName().toUpperCase())) {
            group.put(m, r);

          }
        }
      }
      return group.get(m) == null ? Ranks.NOVATO : group.get(m);
    }
  }

  public void setList(List<SimpleResult> c) {
    searchlist = c;
  }



  public VoiceChannel getCurrentMusicChannel() {
    return music;
  }


  public void setCurrentMusicChannel(VoiceChannel c) {
    music = c;
  }

  public Category getDisplayCategory() {
    return displayc;
  }

  public void setDisplayCategory(Category c) {
    displayc = c;
  }


  public List<TextChannel> getChannels() {
    return channels;
  }

  public void actionChannel(TextChannel channel, boolean remove) {
    if (remove) {
      channels.remove(channel);
    } else {
      if (!channels.contains(channel)) {
        channels.add(channel);
      }
    }
  }

  public static List<Guild> getGuildsFrom(List<String> names) {
    List<Guild> players = new ArrayList<>();
    for (String name : names) {
      if ((Core.getJDA().getGuildById(name) != null)) {
        players.add(Core.getJDA().getGuildById(name));
      }
    }
    return players;
  }

  public void newplayer(Member m) {
    if (m.getRoles().size() == 0) {
      getGuild().addRoleToMember(m, Objects.requireNonNull(getGuild().getRoleById("464492538452115457"))).queue();
      getGroup(m);
    }
  }

  public void updatetag(Member m) {

    Ranks group = getGroup(m);
    if (!m.getUser().getId().equalsIgnoreCase("280474473856237569")) {
      if (m.getNickname() == null) {
        getGuild().modifyNickname(m, group.getPrefix() + m.getUser().getName() + group.getSuffix()).complete();

      } else {
        if (!m.getNickname().equalsIgnoreCase(group.getPrefix() + m.getUser().getName() + group.getSuffix())) {
          getGuild().modifyNickname(m, group.getPrefix() + m.getUser().getName() + group.getSuffix()).complete();

        }
      }

    }
  }

  public void updateTag(Member m, String customName) {

    Ranks group = getGroup(m);
    if (!m.getUser().getId().equalsIgnoreCase("280474473856237569")) {
      if (m.getNickname() == null) {
        getGuild().modifyNickname(m, group.getPrefix() + customName + group.getSuffix()).complete();

      } else {
        if (!m.getNickname().equalsIgnoreCase(group.getPrefix() + customName + group.getSuffix())) {
          getGuild().modifyNickname(m, group.getPrefix() + customName + group.getSuffix()).complete();

        }
      }

    }
  }


  public DiscordBot(Guild p) {
    player = p;
    music = null;
    searchmessage = null;
    group = Maps.newHashMap();
    channels = Lists.newArrayList();

    helioNames = Lists.newArrayList();
    for (Ranks r : Ranks.values()) {
      ranks.add(r.toString().toLowerCase());
    }

    for (TextChannel t : p.getTextChannels()) {
      if (t.getName().contains("dj")) {
        defaultmusic = t;
      }
      if (t.getName().contains("loja")) {
        shop = t;
      }

    }
    defaultmusic = p.getTextChannelById("584730386911854606");
    for (Category c : p.getCategories()) {
      if (c.getName().toLowerCase().contains("diversão")) {
        games = c;
      }
    }



  }

  public static Map<Guild, DiscordBot> datas = new HashMap<>();

  private Guild player;

  public void nameTick() {
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        CustomNameUpdater.list().forEach(names -> {
          if (names.tick++ >= names.getTicks()) {
            if (getGuild().getMemberById(names.getLongId()) != null) {
              String customName = StringUtils.capitalize(names.pushNames());
              updateTag(getGuild().getMemberById(names.getLongId()), StringUtils.capitalize(customName));
            }
            names.tick = 0;
          }

        });
      }
    };
    timer.schedule(task, 0, 20);
  }

  public Guild getGuild() {
    return player;
  }

  public static DiscordBot create(Guild p) {
    if (datas.containsKey(p)) {
      return get(p);
    }

    datas.put(p, new DiscordBot(p));

    return get(p);
  }

  public boolean isPlayingMusic() {
    TrackPlayer.getAudioPlayer(getGuild());
    TrackListener listener = TrackPlayer.getTrackListener(getGuild());
    if (getCurrentMusicChannel() == null) {
      return false;
    } else {
      return listener.isPlayingAnything();
    }
  }

  public static void remove(Guild p) {
    datas.remove(p);
  }

  public static DiscordBot get(Guild g) {
    return datas.getOrDefault(g, null);

  }

  public static List<DiscordBot> getDatas() {
    return new ArrayList<DiscordBot>(datas.values());
  }
}
