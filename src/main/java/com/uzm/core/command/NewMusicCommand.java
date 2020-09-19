package com.uzm.core.command;

import com.google.common.base.Joiner;
import com.uzm.core.Core;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.enums.QueueState;
import com.uzm.core.audio.events.TrackListener;
import com.uzm.core.audio.track.TrackPlayer;
import com.uzm.core.audio.utilities.TimeManager;
import com.uzm.core.audio.utilities.YoutubeBridge;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.image.ImageBuilder;
import com.uzm.core.utilities.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import javax.sound.midi.Track;
import java.awt.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewMusicCommand implements CommandInterface {
  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {
    if (!e.getMessage().getTextChannel().getName().toLowerCase().contains("dj")) {
      EmbedBuilder eb = new EmbedBuilder();
      eb.setTimestamp(OffsetDateTime.now());
      eb.setFooter("Comando executado por " + e.getMember().getNickname());
      eb.setAuthor("Canal errado - Músicas!", null, "https://media1.giphy.com/media/H502wefUP7PFrehL4p/giphy.gif");
      eb.setDescription("A execução do comando ``" + args.get(0) + "`` é \n**exclusiva** para o canal " + bot.getDefaultMusic().getAsMention() + ".");
      e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
      return;

    }
    List<String> truthyArgs = new ArrayList<>(args.subList(1, args.size()));
    TrackListener trackListener = TrackPlayer.getTrackListener(e.getGuild());
    // When command arguments contains first an Youtube link...
    if (truthyArgs.size() == 1) {
      String suppostURL = StringUtils.extractYoutubeURL(truthyArgs.get(0));
      if (suppostURL != null) {
        if (bot.getCurrentMusicChannel() != null) {
          if (bot.getCurrentMusicChannel() != e.getMessage().getMember().getVoiceState().getChannel() && bot.isPlayingMusic()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTimestamp(OffsetDateTime.now());
            eb.setFooter("Comando executado por " + e.getMember().getNickname());
            eb.setColor(Color.RED);
            eb.setDescription("Eu já estou sendo utilizado em outro canal.");
            e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            return;
          }
        }
        TrackPlayer.loadTrack("https://youtube.com/watch?v=" + suppostURL, e.getChannel(), Objects.requireNonNull(e.getMember()), false, null);
      } else {
        switch (truthyArgs.get(0).toLowerCase()) {
          case "pause":
          case "pas":
            if (!bot.isPlayingMusic()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há nenhuma música tocando nesse momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            EmbedBuilder pause = new EmbedBuilder();
            pause.setTimestamp(OffsetDateTime.now());
            pause.setFooter("Ação executada por " + e.getMember().getNickname());
            if (!trackListener.getAudioPlayer().isPaused()) {
              pause.setAuthor("Música pausada ⏸", null, "https://i.imgur.com/qK2TuTI.gif");
              pause.setDescription(e.getMessage().getMember().getAsMention() + " pausou a música, para voltar ao normal digite o mesmo comando!");
            } else {
              pause.setAuthor("Música despausada ▶", null, "https://i.imgur.com/qK2TuTI.gif");
              pause.setDescription(e.getMessage().getMember().getAsMention() + " despausou a música, portanto ela voltará a tocar novamente!");
            }


            e.getChannel().sendMessage(pause.build()).queue();
            trackListener.getAudioPlayer().setPaused(!trackListener.getAudioPlayer().isPaused());
            break;

          case "skip":
          case "pular":
          case "skp":
            if (!bot.getGroup(e.getMember()).isStaff()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setColor(Color.RED);
              eb.setDescription("Você não tem permissão para executar esse subcomando.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            if (!bot.isPlayingMusic()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há nenhuma música tocando nesse momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }

            if (!TrackPlayer.getTrackListener(e.getGuild()).getTrackQueue().isEmpty() && TrackPlayer.getTrackListener(e.getGuild()).getTrackQueue().size() > 1) {
              EmbedBuilder loop = new EmbedBuilder();
              loop.setTimestamp(OffsetDateTime.now());
              loop.setFooter("Ação executada por " + e.getMember().getNickname());
              loop.setAuthor("Música pulada \uD83D\uDD04", null, "https://i.imgur.com/DaftiuA.gif");
              loop.setDescription(e.getMessage().getMember().getAsMention() + " pulou a música atual!");
              e.getChannel().sendMessage(loop.build()).queue();
              TrackPlayer.getAudioPlayer(e.getGuild()).stopTrack();

            } else {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há músicas na fila no momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
            }


            break;
          case "stop":
          case "st":
          case "bye":
            if (!bot.isPlayingMusic()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há nenhuma música tocando nesse momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            EmbedBuilder stop = new EmbedBuilder();
            stop.setTimestamp(OffsetDateTime.now());
            stop.setFooter("Ação executada por " + e.getMember().getNickname());
            stop.setAuthor("Músicas paradas!", null, "https://i.imgur.com/o5RTi2H.gif");
            stop.setDescription(e.getMessage().getMember()
              .getAsMention() + " parou o tocador de músicas,\n consequentemente a fila foi limpa!\n\n**Observação**: Se durante __**2 minutos**__ nenhuma música for\ncolocada na fila, o Barão irá desconectar automaticamente\n do canal de voz!");
            e.getChannel().sendMessage(stop.build()).queue();
            TrackPlayer.getTrackListener(e.getGuild()).clearQueue();
            TrackPlayer.getTrackListener(e.getGuild()).getAudioPlayer().stopTrack();
            break;
          case "help":
          case "ajuda":
            EmbedBuilder help = new EmbedBuilder();
            String[] subCommand = {" ⠀`" + Core.getPrefix() + args.get(0) + " <url> Toque ou coloque na fila a música do dito link." + "`",
              " ⠀`" + Core.getPrefix() + args.get(0) + " stop|st|bye Pare de tocar a música e limpe a fila." + "`",
              " ⠀`" + Core.getPrefix() + args.get(0) + " loop|lp Deixe a música atual em looping." + "`", " ⠀`" + Core.getPrefix() + args
              .get(0) + " search|pes|pesquisar|srch <args> Inicie uma pesquisa no youtube com 5 resultados, para posteriormente tocar tal música escolhida." + "`",
              " ⠀`" + Core.getPrefix() + args.get(0) + " play|p <args> Toque ou coloque na fila a música da dita pesquisa." + "`",
              " ⠀`" + Core.getPrefix() + args.get(0) + " volume|vol <%> Altere o volume da música." + "`",
              " ⠀`" + Core.getPrefix() + args.get(0) + " skip|pular|skp Pule a música atual." + "`",};
            help.setTitle("Confira os comandos disponíveis para você!");
            help.setColor(new Color(54, 57, 63));
            help.setDescription(
              "O sistema coletou que você tem permissões privilegiadas, com isso você tem acesso aos comandos de moderação. Confira-os: \n\n" + String.join("\n", subCommand));
            e.getChannel().sendMessage(help.build()).queue(message -> message.delete().queueAfter(30, TimeUnit.SECONDS));
            break;
          case "loop":
          case "lp":

            if (!bot.isPlayingMusic()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há nenhuma música tocando nesse momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            EmbedBuilder loop = new EmbedBuilder();
            loop.setTimestamp(OffsetDateTime.now());
            loop.setFooter("Ação executada por " + e.getMember().getNickname());
            if (trackListener.getQueueState() == QueueState.NORMAL) {
              loop.setAuthor("Ciclo ativado \uD83D\uDD04", null, "https://i.imgur.com/Zipebd2.gif");
              loop.setDescription(e.getMessage().getMember().getAsMention() + " ligou o ciclo, agora a atual música irá ficar se repetindo até que ele seja desativado!");
            } else {
              loop.setAuthor("Ciclo desativado \uD83D\uDD04", null, "https://i.imgur.com/Zipebd2.gif");
              loop.setDescription(e.getMessage().getMember().getAsMention() + " desligou o ciclo, agora a fila irá seguir a ordem normal colocada!");
            }


            e.getChannel().sendMessage(loop.build()).queue();
            trackListener.setQueueState(trackListener.getQueueState() == QueueState.NORMAL ? QueueState.CYCLE : QueueState.NORMAL);
            break;
          default:
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTimestamp(OffsetDateTime.now());
            eb.setFooter("Comando executado por " + e.getMember().getNickname());
            eb.setAuthor("URL inválida!", null, "https://media3.giphy.com/media/8L0Pky6C83SzkzU55a/giphy.gif");
            eb.setDescription("``" + truthyArgs.get(0) + "`` não se trata de uma URL\nválida do **YouTube** tente outra.\n\n__**Observação**__: Tente não usar links encurtados!");
            e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));

            break;
        }

      }
    } else {
      if (truthyArgs.size() >= 1) {
        switch (truthyArgs.get(0).toLowerCase()) {
          case "search":
          case "pes":
          case "pesquisar":
          case "srch":
            if (bot.getSearch() != null) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setColor(Color.RED);
              eb.setDescription("Já está sendo feita uma pesquisa no momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            StringBuilder choices = new StringBuilder("\n");
            String[] emoteList = {"1⃣", "2⃣", "3⃣", "4⃣", "5⃣"};
            String search = Joiner.on(" ").join(truthyArgs.subList(1, truthyArgs.size()));


            List<YoutubeBridge.SimpleResult> results = Core.getYoutubeBridge().getResults(search, 5);
            bot.setList(results);

            for (int x = 0; x < results.size(); x++) {
              choices.append("\n ").append(emoteList[x]).append(" | [").append(results.get(x).getTitle()).append("](").append("https://youtube.com/watch?v=")
                .append(results.get(x).getCode()).append(")\n");
            }
            if (results.isEmpty()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription(("Não foi encontrado nada a respeito de **\"" + search + "\"** na pesquisa do Youtube."));
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            EmbedBuilder searchEb = new EmbedBuilder();
            searchEb.setAuthor("Resultados para a pesquisa \"" + search + "\"", null, "https://i.imgur.com/4OTxl5C.gif");
            String id = bot.getResultYoutube().get(0).getCode();
            searchEb.setThumbnail("https://img.youtube.com/vi/" + id + "/0.jpg");
            searchEb.setDescription(choices.toString());
            searchEb.setFooter("⏩ Pesquisa feita por " + e.getMember().getNickname(), e.getAuthor().getEffectiveAvatarUrl());
            e.getChannel().sendMessage(searchEb.build()).queue(message -> {
               for (String str : emoteList) {
                 message.addReaction(str).submit();
               }
              bot.setSearch(message, DiscordBot.TypeSearch.YOUTUBE);
              Timer timer = new Timer();
              TimerTask task = new TimerTask() {

                public void run() {

                  if (bot.getSearch() != null) {
                    message.delete().queue();
                    bot.destroyCache();
                    bot.getResultYoutube().clear();
                    this.cancel();
                  }

                }
              };
              timer.schedule(task, TimeManager.createtime(TimeUnit.SECONDS, 20));
            });

            break;
          case "p":
          case "play":
            if (bot.getCurrentMusicChannel() != null) {
              if (bot.getCurrentMusicChannel() != e.getMessage().getMember().getVoiceState().getChannel() && bot.isPlayingMusic()) {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTimestamp(OffsetDateTime.now());
                eb.setFooter("Comando executado por " + e.getMember().getNickname());
                eb.setColor(Color.RED);
                eb.setDescription("Eu já estou sendo utilizado em outro canal.");
                e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                return;
              }
            }
            String searchCriteria = Joiner.on(" ").join(truthyArgs.subList(1, truthyArgs.size()));
            YoutubeBridge.SimpleResult firstResult = Core.getYoutubeBridge().getResults(searchCriteria);
            if (firstResult == null) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não foi encontrado nada a respeito de **\"" + searchCriteria + "\"** na pesquisa do Youtube.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            TrackPlayer.loadTrack("https://youtube.com/watch?v=" + firstResult.getCode(), e.getChannel(), Objects.requireNonNull(e.getMember()), true, searchCriteria);

            break;
          case "volume":
          case "vol":
            if (!bot.isPlayingMusic()) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setTimestamp(OffsetDateTime.now());
              eb.setFooter("Comando executado por " + e.getMember().getNickname());
              eb.setDescription("Não há nenhuma música tocando nesse momento.");
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
              return;
            }
            TrackPlayer.getAudioPlayer(e.getGuild()).setVolume(Integer.parseInt(truthyArgs.get(1)));
            EmbedBuilder volume = new EmbedBuilder();
            volume.setTitle("Volume alterado!");
            volume.setFooter(TimeManager.formatDateBR(new Date(System.currentTimeMillis())));
            volume.setImage("attachment://file.png");
            e.getChannel().sendFile(
              ImageBuilder.buildVolumeImage(TrackPlayer.getAudioPlayer(e.getGuild()).getPlayingTrack().getInfo().title, e.getMember(), Integer.parseInt(truthyArgs.get(1))),
              "file.png").embed(volume.build()).queue(message -> {
              message.addReaction("\uD83D\uDD0A").queue();
            });
            break;

        }
      }
    }
  /*  if (args.)
    String searchCriteria = Joiner.on(" ").join(truthyArgs);

    System.out.println(searchCriteria);
*/

    /*
      musica <link>
      musica p ou play <nome>
      musica pes ou search <word>
      musica stop ou s
      musica loop
      musica info
      musica help
      musica vol ou volume <percent>



     */



  }

  @Override
  public String[] getInvoke() {
    return new String[] {"musica", "music", "m"};
  }
}
