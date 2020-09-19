package com.uzm.core.audio.events;

import com.google.common.collect.Lists;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.track.GuildTrack;
import com.uzm.core.audio.enums.QueueState;
import com.uzm.core.audio.track.TrackPlayer;
import com.uzm.core.image.ImageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrackListener extends AudioEventAdapter {

  private Queue<GuildTrack> trackQueue;
  private AudioPlayer audioPlayer;
  private QueueState queueState = QueueState.NORMAL;


  public TrackListener(AudioPlayer audioPlayer) {
    this.audioPlayer = audioPlayer;
    this.trackQueue = new LinkedBlockingQueue<>();
  }

  public void addTrack(AudioTrack track, Guild guild, Member author) {
    lastMember = author;
    trackQueue.add(new GuildTrack(track, guild, author));
    if (!isPlayingAnything())
      getAudioPlayer().playTrack(track);
  }

  public void removeTrack(GuildTrack guildTrack) { trackQueue.remove(guildTrack); }

  @Override
  public void onTrackStart(AudioPlayer audioPlayer, AudioTrack audioTrack) {
    GuildTrack nextTrack = getTrackQueue().element();
    DiscordBot guild = DiscordBot.get(nextTrack.getGuild());

    if (guild.getCurrentMusicChannel() == null)
      audioPlayer.stopTrack();
    else
      nextTrack.getGuild().getAudioManager().openAudioConnection(guild.getCurrentMusicChannel());
  }

  private Member lastMember;

  @Override
  public void onTrackEnd(AudioPlayer audioPlayer, AudioTrack audioTrack, AudioTrackEndReason endReason) {
    DiscordBot guild = DiscordBot.get(getTrackQueue().poll().getGuild());
    switch (getQueueState()) {
      case NORMAL:
        if (getTrackQueue().isEmpty()) {
          Timer timer = new Timer();
          TimerTask task = new TimerTask() {
            @Override
            public void run() {
              if (getTrackQueue().isEmpty()) {
                guild.getGuild().getAudioManager().closeAudioConnection();
                guild.getDefaultMusic().sendMessage(
                  new EmbedBuilder().setTimestamp(OffsetDateTime.now()).setFooter("Ação executada automaticamente por " + guild.getGuild().getSelfMember().getNickname())
                    .setAuthor("Inatividade \uD83D\uDCA4", null, "https://media2.giphy.com/media/l2JhpjWPccQhsAMfu/giphy.gif").setDescription(
                    "O " + guild.getGuild().getSelfMember()
                      .getAsMention() + " foi removido do canal que estava, pois\n durante **2 minutos** nada foi colocado na fila de música. \n\nFazemos isso para manter a estabilidade do nosso\ntão querido Barão :smile:!")
                    .build()).queue();
                guild.setCurrentMusicChannel(null);
              }

            }
          };
          timer.schedule(task, TimeUnit.MINUTES.toMillis(2));
        }else {
          GuildTrack nextTrack = getTrackQueue().element();
          getAudioPlayer().playTrack(nextTrack.getTrack());
          EmbedBuilder eb = new EmbedBuilder();
          eb.setTimestamp(OffsetDateTime.now());
          eb.setColor(new Color(86, 184, 184));
          eb.setFooter("Música solicitada por " + nextTrack.getAuthor().getNickname());
          eb.setAuthor(nextTrack.getTrack().getInfo().title + " \uD83C\uDFBC", "https://youtube.com/watch?=" + nextTrack.getTrack().getIdentifier(),
            "https://i.imgur.com/0svmymB.gif");
           if (getTrackQueue().size() > 1)
            eb.setDescription("⏩ Próxima música a tocar: __**" + getTrackQueue().toArray(new GuildTrack[] {})[1].getTrack().getInfo().title + "**__\n");

          eb.addField("Autor:", nextTrack.getTrack().getInfo().author, true);
          eb.addField("Link:", "[Clique aqui](" + "https://youtube.com/watch?=" + nextTrack.getTrack().getIdentifier() + ")", true);
          eb.setImage("attachment://"+ nextTrack.getTrack().getIdentifier()+".png");
          guild.getDefaultMusic().sendFile(ImageBuilder.buildTrackImage(guild, nextTrack.getTrack(), nextTrack.getTrack().getIdentifier()), ""+ nextTrack.getTrack().getIdentifier()+".png").embed(eb.build()).queue(message -> {
            message.addReaction("\uD83E\uDD1F").queue();
          });
        }
        break;
      case CYCLE:
        if (lastMember != null) {
          GuildTrack copiedTrack = new GuildTrack(audioTrack.makeClone(), guild.getGuild(), lastMember);
          getTrackQueue().add(copiedTrack);
          getAudioPlayer().playTrack(copiedTrack.getTrack());
          EmbedBuilder cycle = new EmbedBuilder();
          cycle.setTimestamp(OffsetDateTime.now());
          cycle.setColor(new Color(86, 184, 184));
          cycle.setFooter("Música solicitada por " + lastMember.getNickname());
          cycle.setAuthor(audioTrack.getInfo().title + " \uD83C\uDFBC", "https://youtube.com/watch?=" + audioTrack.getIdentifier(), "https://i.imgur.com/0svmymB.gif");
          if (!getTrackQueue().isEmpty())
            cycle.setDescription("\uD83D\uDD04 O __ciclo__ está ativado para a **esta música**!\n");
          cycle.addField("Autor:", audioTrack.getInfo().author, true);
          cycle.addField("Link:", "[Clique aqui](" + "https://youtube.com/watch?=" + audioTrack.getIdentifier() + ")", true);
          cycle.setImage("attachment://file.png");

          guild.getDefaultMusic().sendFile(ImageBuilder.buildTrackImage(guild, audioTrack, audioTrack.getIdentifier()), "file.png").embed(cycle.build()).queue(message -> {
            message.addReaction("\uD83E\uDD1F").queue();
          });
        }
        break;
    }


  }


  public void clearQueue() {
    getTrackQueue().clear();
  }

  public void shuffleQueue() {
    List<GuildTrack> toSuffle = new ArrayList<>(getTrackQueue()).subList(1, getTrackQueue().size());
    Collections.shuffle(toSuffle);
    toSuffle.add(0, Lists.newArrayList(getTrackQueue()).get(0));
    getTrackQueue().clear();
    getTrackQueue().addAll(toSuffle);
  }

  public Set<GuildTrack> getInQueueTracks() {
    return new LinkedHashSet<>(getTrackQueue());
  }

  public GuildTrack findByAudioTrack(AudioTrack audioTrack) {
    return getTrackQueue().stream().filter(result -> result.getTrack() == audioTrack).findFirst().orElse(null);
  }

  public boolean isPlayingAnything() {
    return getAudioPlayer().getPlayingTrack() != null;
  }


  public AudioPlayer getAudioPlayer() {
    return audioPlayer;
  }

  public Queue<GuildTrack> getTrackQueue() {
    return trackQueue;
  }

  public QueueState getQueueState() {
    return queueState;
  }

  public void setQueueState(QueueState queueState) {
    this.queueState = queueState;
  }
}
