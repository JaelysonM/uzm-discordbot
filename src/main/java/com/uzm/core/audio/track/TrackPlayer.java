package com.uzm.core.audio.track;

import com.sedmelluq.discord.lavaplayer.filter.PcmFilterFactory;
import com.sedmelluq.discord.lavaplayer.filter.equalizer.EqualizerFactory;
import com.sedmelluq.discord.lavaplayer.format.transcoder.PcmChunkEncoder;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.events.TrackListener;
import com.uzm.core.audio.handlers.AudioPlayerSendHandler;
import com.uzm.core.audio.utilities.TimeManager;
import com.uzm.core.image.ImageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TrackPlayer {
  public static final Map<Guild, Map.Entry<AudioPlayer, TrackListener>> PLAYERS;
  private static final AudioPlayerManager AUDIO_PLAYER_MANAGER;
  private static final EqualizerFactory equalizer = new EqualizerFactory();

  static {
    AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    PLAYERS = new HashMap<>();
    AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);
  }

  public static boolean has(Guild guild) {
    return PLAYERS.containsKey(guild);
  }

  public static AudioPlayer getAudioPlayer(Guild guild) {
    return has(guild) ? PLAYERS.getOrDefault(guild, null).getKey() : createAudioPlayer(guild);
  }

  public static TrackListener getTrackListener(Guild guild) {
    return has(guild) ? PLAYERS.getOrDefault(guild, null).getValue() : null;
  }

  public static AudioPlayer createAudioPlayer(Guild guild) {
    AudioPlayer audioPlayer = AUDIO_PLAYER_MANAGER.createPlayer();
    audioPlayer.setVolume(40);
    TrackListener trackListener = new TrackListener(audioPlayer);
    audioPlayer.addListener(trackListener);
    guild.getAudioManager().setSendingHandler(new AudioPlayerSendHandler(audioPlayer));
    PLAYERS.put(guild, new AbstractMap.SimpleEntry<>(audioPlayer, trackListener));
    return audioPlayer;
  }

  public static void resetPlayer(Guild guild) {
    getAudioPlayer(guild).destroy();
    getTrackListener(guild).clearQueue();
    guild.getAudioManager().closeAudioConnection();
    PLAYERS.remove(guild);
  }
 public static TextChannel CHANNEL;
  public static void loadTrack(String videoUrl, TextChannel channel, Member author, boolean searched, String searchKey) {
    Guild guild = author.getGuild();
    DiscordBot bot = DiscordBot.get(guild);
    AudioPlayer audioPlayer = getAudioPlayer(guild);
    TrackListener trackListener = getTrackListener(guild);
    CHANNEL= channel;
    AUDIO_PLAYER_MANAGER.loadItemOrdered(guild, videoUrl, new AudioLoadResultHandler() {
      @Override
      public void trackLoaded(AudioTrack audioTrack) {

        if (bot.getCurrentMusicChannel() == null) {
          VoiceChannel voiceChannel = guild.getVoiceChannels().stream().filter(result -> result.getName().toLowerCase().contains("música")).findFirst().orElse(null);
          if (voiceChannel == null && author.getVoiceState().getChannel() == null) {
            EmbedBuilder eb1 = new EmbedBuilder();
            eb1.setTimestamp(OffsetDateTime.now());
            eb1.setFooter("Comando executado por " + author.getUser().getAsTag());
            eb1.setAuthor("Canal padrão não encontrado", null, "https://media1.giphy.com/media/H502wefUP7PFrehL4p/giphy.gif");
            eb1.setDescription("Como você não está em um canal de voz, eu tentei me conectar \na um canal padrão, porém não foi encontrado nenhum.");
            channel.sendMessage(eb1.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

          } else {
            bot.setCurrentMusicChannel(author.getVoiceState().getChannel() == null ? voiceChannel : author.getVoiceState().getChannel());
            guild.getAudioManager().openAudioConnection(bot.getCurrentMusicChannel());
          }

        }
        if (trackListener.isPlayingAnything()) {
          trackListener.addTrack(audioTrack, guild, author);
          EmbedBuilder eb = new EmbedBuilder();
          eb.setTimestamp(OffsetDateTime.now());
          eb.setFooter("Música colocada na fila por " + author.getUser().getAsTag());
          if (audioTrack.getSourceManager().getSourceName().equalsIgnoreCase("youtube"))
            eb.setColor(Color.RED);
          eb.setTitle("Nova música adicionada a fila \uD83C\uDFB6");
          if (searched)
          eb.addField("Pesquisa relacionada:", "\""+searchKey+"\"", false );
          eb.setDescription(
            "A música [" + audioTrack.getInfo().title + "](" + videoUrl + ") \nfoi adicionada a **fila de músicas**!\n\nDuração da música: "+ TimeManager.getTime(audioTrack.getDuration()) + "\nPosição atual na fila: **" + (trackListener.getTrackQueue()
              .size() ) + "º**");
          eb.setThumbnail("https://img.youtube.com/vi/" + audioTrack.getIdentifier() + "/0.jpg");
          channel.sendMessage(eb.build()).complete();

        }else {
          trackListener.addTrack(audioTrack, guild, author);
          EmbedBuilder eb = new EmbedBuilder();
          eb.setTimestamp(OffsetDateTime.now());
          eb.setColor(new Color(86, 184, 184));
          eb.setFooter("Música solicitada por " + author.getNickname());
          eb.setAuthor( audioTrack.getInfo().title+" \uD83C\uDFBC",videoUrl, "https://i.imgur.com/0svmymB.gif");
          eb.addField("Autor:", audioTrack.getInfo().author, true);
          eb.addField("Link:", "[Clique aqui](" + videoUrl+")", true);
          if (searched)
            eb.addField("Pesquisa relacionada:", "\""+searchKey+"\"", false );
          eb.setImage("attachment://file.png");

          channel.sendFile(ImageBuilder.buildTrackImage(bot, audioTrack, audioTrack.getIdentifier()), "file.png").embed(eb.build()).queue(message -> {
            message.addReaction("\uD83E\uDD1F").queue();
          });
        }

      }

      @Override
      public void playlistLoaded(AudioPlaylist audioPlaylist) {
        // TODO Do this later

        /*
       long time = System.currentTimeMillis();
        if (playlist.getSelectedTrack() != null) {

          trackLoaded(playlist.getSelectedTrack());

          AudioTrack t = playlist.getSelectedTrack();

          EmbedBuilder eb = new EmbedBuilder();

          eb.setColor(Color.ORANGE);
          eb.setTitle("🎼 Barão - Rádio (Playlist)");
          eb.addField("Tocando agora: ", t.getInfo().title, true);
          eb.addField("Autor: ", t.getInfo().author, true);
          eb.addField("Duração: ", TimeManager.getTime(t.getDuration()), true);
          eb.addField("Da playlist: ", playlist.getName(), true);

          String id = t.getInfo().uri.replace("https://www.youtube.com/watch?v=", "");

          eb.setImage(" https://img.youtube.com/vi/" + id + "/0.jpg");

          channel.sendMessage(eb.build()).queue();


        } else if (playlist.isSearchResult()) {


          trackLoaded(playlist.getTracks().get(0));
          DiscordBot bot = DiscordBot.get(guild);
          GuildVoiceState memberVoiceState = author.getVoiceState();
          if (!memberVoiceState.inVoiceChannel()) {
            bot.setCurrentMusicChannel(guild.getVoiceChannelsByName("🎶 Música #1", true).get(0));
          } else {
            bot.setCurrentMusicChannel(author.getVoiceState().getChannel());
          }
        } else {

          DiscordBot bot = DiscordBot.get(guild);
          GuildVoiceState memberVoiceState = author.getVoiceState();
          if (!memberVoiceState.inVoiceChannel()) {
            bot.setCurrentMusicChannel(guild.getVoiceChannelsByName("🎶 Música #1", true).get(0));
          } else {
            bot.setCurrentMusicChannel(author.getVoiceState().getChannel());
          }



          long d = 0;
          for (int i = 0; i < Math.min(playlist.getTracks().size(), 200); i++) {
            d += playlist.getTracks().get(i).getDuration();

            getTrackManager(guild).queue(playlist.getTracks().get(i), author.getGuild());
          }

          EmbedBuilder eb = new EmbedBuilder();

          eb.setColor(Color.ORANGE);
          eb.setTitle("🎼 Barão - Rádio (Playlist completa)");
          eb.addField("Nome: ", getOrNull(playlist.getName()), true);
          eb.addField("Duração estimada: ", TimeManager.getTime(d), true);
          eb.addField("Tamanho: ", Math.min(playlist.getTracks().size(), 200) + " músicas", true);
          eb.setFooter("Solicitada por " + author.getUser().getName(), author.getUser().getAvatarUrl());
          eb.setThumbnail("https://blog.fonepaw.com/images/youtube-music.jpg");
          channel.sendMessage(eb.build()).queue();
          AudioTrack t = playlist.getTracks().get(0);
          EmbedBuilder eb2 = new EmbedBuilder();

          eb2.setColor(Color.ORANGE);
          eb2.setTitle("🎼 Barão - Rádio (Playlist completa)");
          eb2.addField("Tocando agora: ", t.getInfo().title, true);
          eb2.addField("Autor: ", t.getInfo().author, true);
          eb2.addField("Duração: ", TimeManager.getTime(t.getDuration()), true);
          eb2.addField("Da playlist: ", playlist.getName(), true);

          String id = t.getInfo().uri.replace("https://www.youtube.com/watch?v=", "");

          eb2.setImage(" https://img.youtube.com/vi/" + id + "/0.jpg");
          channel.sendMessage(eb2.build()).queue();



          EmbedBuilder eb3 = new EmbedBuilder();
          eb3.setColor(Color.gray);
          eb3.setTitle("⚠  Barão - Erro (Playlist load)");
          eb3.addField("Atraso de:", TimeManager.getTime((System.currentTimeMillis() - time)) + " (" + (System.currentTimeMillis() - time) + " ms)", true);

          channel.sendMessage(eb3.build()).queue(m -> {
            m.delete().queueAfter(40, TimeUnit.SECONDS);
          });
        }

  */
      }

      @Override
      public void noMatches() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(OffsetDateTime.now());
        eb.setColor(Color.BLACK);
        eb.setFooter("Comando executado por " + author.getNickname());
        eb.setDescription("Não foi encontrado nada a respeito da url `" + videoUrl + "`.");
        channel.sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

      }

      @Override
      public void loadFailed(FriendlyException e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTimestamp(OffsetDateTime.now());
        eb.setColor(Color.RED);
        eb.setFooter("Comando executado por " + author.getNickname());
        eb.setDescription("Ocorreu um erro ao tentar carregar essa música!\n\n**Erro relacionado**: " + e.getLocalizedMessage());
        channel.sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
      }
    });
  }



}
