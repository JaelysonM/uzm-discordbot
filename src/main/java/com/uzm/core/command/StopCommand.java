package com.uzm.core.command;

import java.awt.Color;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.uzm.core.Core;
import com.uzm.core.DiscordBot;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.games.tictactoe.TicTacToe;
import com.uzm.core.utilities.InternalStats;
import com.uzm.core.utilities.Manager.Ranks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class StopCommand implements CommandInterface {

  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {

    if (args.get(0).equalsIgnoreCase("desligar")) {
      if (bot.getGroup(Objects.requireNonNull(e.getMember())) == Ranks.UZM) {
        EmbedBuilder eb = new EmbedBuilder();
        InternalStats internalStats = InternalStats.collect();
        long secondsUptime = internalStats.getUptime();
        long days = secondsUptime / 86400;
        long hours = (secondsUptime / 3600) % 24;
        long minutes = (secondsUptime / 60) % 60;
        long seconds = secondsUptime % 60;
        StringBuilder uptime = new StringBuilder();
        if (days > 0)
          uptime.append(days).append(" d ");
        if (hours > 0)
          uptime.append(hours).append(" h ");
        if (minutes > 0)
          uptime.append(minutes).append(" m ");
        uptime.append(seconds).append(" s");

        eb.setColor(Color.WHITE);
        eb.setTitle("✋ 👺 Barão - Manager [Shutdown]");

        eb.addField("Ping: ", Core.getJDA().getGatewayPing() + " ms", true);
        eb.addField("Servidores: ", Core.getJDA().getGuilds().size() + " servidores", true);
        eb.addField("Tempo ligado: ", uptime.toString(), true);

        eb.setFooter("Solicitado por " + Objects.requireNonNull(e.getGuild().getMember(e.getAuthor())).getNickname(), e.getMessage().getAuthor().getAvatarUrl());
        e.getChannel().sendMessage(eb.build()).queue();
        for (Guild g : e.getJDA().getGuilds()) {

          if (!DiscordBot.get(g).getChannels().isEmpty()) {
            for (TextChannel c : DiscordBot.get(g).getChannels()) {
              c.delete().complete();
            }
            System.out.println("[BOT] All private channel has been deleted!");
          }

        }


        if (TicTacToe.getDatas().size() > 0) {
          TicTacToe tic = TicTacToe.getDatas().get(0);
          tic.clearAll();

        }
        System.out.println("[BOT] All game-images has been deleted!");

        Core.getJDA().shutdown();
        System.exit(0);

        if (TicTacToe.getDatas().size() > 0) {
          TicTacToe tic = TicTacToe.getDatas().get(0);

          tic.clearAll();

        }



      } else {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🚫  Barão - Aviso (Permissão)");
        eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
        eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
      }


    }

  }

  @Override
  public String getInvoke() {

    return "desligar";
  }
}
