package com.uzm.core.command;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.uzm.core.DiscordBot;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.games.tictactoe.TicTacToe;

import com.uzm.core.utilities.Manager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class TicTacCommand implements CommandInterface {

  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {
    if (args.get(0).equalsIgnoreCase("tictac")) {


      if (bot.getGroup(Objects.requireNonNull(e.getMember())).ordinal() >= Manager.Ranks.AMIGUINHO.ordinal()) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🚫  Barão - Aviso (Permissão)");
        eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
        eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
        eb.setTimestamp(OffsetDateTime.now());
        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));


        return;
      }
      TicTacToe.build(e.getGuild(), e.getMember(), e.getChannel());


    }

  }


  @Override
  public String getInvoke() {

    return "tictac";
  }

}
