package com.uzm.core.command;

import java.awt.Color;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.uzm.core.Core;
import com.uzm.core.DiscordBot;

import com.uzm.core.command.interfaces.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PingCommand implements CommandInterface {

  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {
    if (args.get(0).equalsIgnoreCase("ping")) {


      if (!bot.getGroup(Objects.requireNonNull(e.getMember())).isStaff()) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🚫  Barão - Aviso (Permissão)");
        eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
        eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));


        return;
      }



      e.getChannel().sendMessage(new EmbedBuilder().setColor(Color.white).setTitle("✋ 👺 Barão - Manager [Info - Ping]").addField("Ping: " ,"Calculando...", true).addField("Servidores: ", Core.getJDA().getGuilds().size() + " servidores", true).setFooter("Solicitado por " + Objects.requireNonNull(e.getGuild().getMember(e.getAuthor())).getNickname(), e.getMessage().getAuthor().getAvatarUrl()).build()).queue(message -> {
        long ping = e.getMessage().getTimeCreated().until(message.getTimeCreated(), ChronoUnit.MILLIS);
       message.editMessage(new EmbedBuilder().setColor(Color.white).setTitle("✋ 👺 Barão - Manager [Info - Ping]").addField("Ping: " ,ping+ " ms", true).addField("Servidores: ", Core.getJDA().getGuilds().size() + " servidores", true).setFooter("Solicitado por " + Objects.requireNonNull(e.getGuild().getMember(e.getAuthor())).getNickname(), e.getMessage().getAuthor().getAvatarUrl()).build()).queue();
      } );


    }

  }


  @Override
  public String getInvoke() {

    return "ping";
  }

}
