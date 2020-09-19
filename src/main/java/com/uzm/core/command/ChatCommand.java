package com.uzm.core.command;


import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.uzm.core.DiscordBot;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.utilities.Manager;
import com.uzm.core.utilities.Manager.NType;

import com.uzm.core.utilities.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class ChatCommand implements CommandInterface {


  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {

    if (args.get(0).equalsIgnoreCase("limpar")) {

      if (!bot.getGroup(Objects.requireNonNull(e.getMember())).isStaff()) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🚫  Barão - Aviso (Permissão)");
        eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
        eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        return;
      }
      if (args.size() > 1) {
        if (Utils.isNumeric(args.get(1))) {

          try {
            int v = Integer.parseInt(args.get(1));
            List<Message> messages = e.getChannel().getHistory().retrievePast(v).complete();
            e.getChannel().deleteMessages(messages).queue();
            e.getChannel().sendMessage("💨 Ei barão, eu apaguei " + v + " mensagens desse canal!").queue();
          } catch (Exception ex) {
            if (ex.toString().startsWith("java.lang.IllegalArgumentException: Message Id provided was older than 2 weeks")) {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setColor(0xf44242);
              eb.setTitle("✋ 👺  Erro de quantidade!");
              eb.setDescription("Não existe essa quantidade de mensagens.\nTente um valor entre 1 e 100!");
              eb.setFooter("", e.getMessage().getAuthor().getAvatarUrl());
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
						}
          }
        } else {
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(0xf44242);
          eb.setTitle("⚠  Barão - Erro (Numérico)");
          eb.addField("Erro: ", " - '" + args.get(1) + "' não se trata de um número inteiro.", false);
          e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        }

      } else {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("⚠  Barão - Erro (Comando)");
        eb.addField(" -limpar", "<numero>", true);


        eb.setFooter("", e.getMessage().getAuthor().getAvatarUrl());
        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));

      }
    }

  }


  @Override
  public String getInvoke() {

    return "limpar";
  }

}
