package com.uzm.core.command;


import com.uzm.core.DiscordBot;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.controllers.CustomNameUpdater;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class CustomNamesCommand implements CommandInterface {


  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {

    if (args.get(0).equalsIgnoreCase("names")) {

      if (!bot.getGroup(Objects.requireNonNull(e.getMember())).isStaff()) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🚫  Barão - Aviso (Permissão)");
        eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
        eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

        e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
        return;
      }
      switch (args.get(1).toLowerCase()) {
        case "reload":
          CustomNameUpdater.clearAll();
          CustomNameUpdater.loadApplicableMembers();
          EmbedBuilder eb = new EmbedBuilder();
          eb.setColor(Color.BLACK);
          eb.setTitle("Nome de usuário customizado - Reload");
          eb.setDescription("A pasta ``custom-names`` foi atualizada, agora os novos nomes ou novos membros serão aplicados e armazenados.");

         String users = CustomNameUpdater.list().stream().filter(map -> e.getGuild().getMemberById(map.getLongId()) !=null).map(map ->
           {
             Member member = e.getGuild().getMemberById(map.getLongId());
             return " " +member.getUser().getName() + "#" + member.getUser().getDiscriminator();
           }
          ).collect(Collectors.joining("\n"));

          eb.addField("Usuários aplicados:", "```\n" + users + "\n```", true);
          eb.setFooter("Feito por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
          eb.setTimestamp(OffsetDateTime.now());
         e.getChannel().sendMessage(eb.build()).queue();
          break;
      }
    }

  }


  @Override
  public String getInvoke() {

    return "names";
  }

}
