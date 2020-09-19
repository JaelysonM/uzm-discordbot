package com.uzm.core.audio.listeners;

import com.google.common.collect.Lists;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.events.TrackListener;
import com.uzm.core.audio.track.TrackPlayer;
import com.uzm.core.audio.utilities.YoutubeBridge;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class TrackListeners extends ListenerAdapter {
  @Override
  public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
    Guild guild = event.getGuild();
    if (!TrackPlayer.has(guild)) {
      return;
    }
    TrackListener trackListener = TrackPlayer.getTrackListener(guild);
    trackListener.getTrackQueue().stream()
      .filter(info -> !info.getTrack().equals(TrackPlayer.getAudioPlayer(guild).getPlayingTrack()) && info.getAuthor().getUser().equals(event.getMember().getUser()))
      .forEach(trackListener::removeTrack);

  }

  @Override
  public void onGuildLeave(GuildLeaveEvent event) {
    TrackPlayer.resetPlayer(event.getGuild());
  }

  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {

    if (e.getUser().isBot()) {
      return;
    }
    DiscordBot bot = DiscordBot.get(e.getGuild());
    if (bot == null) {
      return;
    }
    if (bot.getSearch() == null) {
      return;
    }

    Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
    if (message == null) {
      return;
    }
    if (bot.messagecache.isEmpty()) {
      return;
    }

    if (bot.getMessageCache(message) == DiscordBot.TypeSearch.YOUTUBE) {
      if (bot.getSearch() == null) {
        return;
      }
      List<String> emoteList = Lists.newArrayList("1⃣", "2⃣", "3⃣", "4⃣", "5⃣");
      YoutubeBridge.SimpleResult selectResult = null;
      if (!bot.getResultYoutube().isEmpty()) {
        try {
          selectResult = bot.getResultYoutube().get(emoteList.indexOf(e.getReaction().getReactionEmote().getName()));
          bot.destroyCache();
          bot.getResultYoutube().clear();
          message.delete().queueAfter(4, TimeUnit.MILLISECONDS);
          if (selectResult != null) {
            TrackPlayer.loadTrack("https://youtube.com/watch?v=" + selectResult.getCode(), e.getChannel(), Objects.requireNonNull(e.getMember()), false, null);
          }
        } catch (Exception err) {
          bot.destroyCache();
          bot.getResultYoutube().clear();
          message.delete().queueAfter(4, TimeUnit.MILLISECONDS);
        }
      }
    }



  }
}
