package com.uzm.core.command;


import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.uzm.core.DiscordBot;
import com.uzm.core.controllers.RulesController;

import com.uzm.core.command.interfaces.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;



public class BanCommand implements CommandInterface {


  @Override
  public void handle(DiscordBot bot, List<String> args, GuildMessageReceivedEvent e) {

    if (args.get(0).equalsIgnoreCase("ban")) {
      if (bot.getGroup(Objects.requireNonNull(e.getMember())).isStaff()) {

        if (args.size() < 3) {
          format(e);
          return;
        }
        if (args.size() == 3) {
          if (RulesController.is(args.get(2))) {
            RulesController
              regra = RulesController.getRule(("Art." + args.get(2).split("/")[0].replace("Art", "").replace(" ", "") + "=" + args.get(2).split("/")[1].replace(" ", "")).toLowerCase());
            if (regra != null) {
              List<User> users = new ArrayList<>();
              for (User u : e.getMessage().getMentionedUsers()) {
                if (!u.getId().equalsIgnoreCase("280474473856237569")) {
                  users.add(u);
                }
              }
              if (users.size() > 0) {
                for (User toban : users) {
                  e.getGuild().ban(toban, 1,
                    e.getAuthor().getAsTag() + " baniu você permanentemente por infringir o " + regra.getArticle() + " " + regra.getParagraph() + " do servidor.\n[" + regra
                      .getText() + "]").queue();
                }
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(0xf44242);
                eb.setTitle("🔨  Barão - Banimento");
                eb.addField("Usuários banidos: ", args.get(1), false);
                eb.addField("Motivo: ", regra.getArticle() + " " + regra.getParagraph() + " [" + regra.getText() + "]", false);
                eb.setFooter("Feito por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
                eb.setTimestamp(OffsetDateTime.now());
                Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(eb.build()).queue();

              } else {
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(0xf44242);
                eb.setTitle("⚠  Barão - Erro (Usuários)");
                eb.addField("Erro: ", " - Você não mencionou nenhum usuário válido.", false);
                eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
                eb.setTimestamp(OffsetDateTime.now());
                e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(4, TimeUnit.SECONDS));
              }
            } else {
              EmbedBuilder eb = new EmbedBuilder();
              eb.setColor(0xf44242);
              eb.setTitle("⚠  Barão - Erro (Regras)");
              eb.addField("Erro: ", " - Essa regra não existe no banco de dados do servidor.", false);
              eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
              eb.setTimestamp(OffsetDateTime.now());
              e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(4, TimeUnit.SECONDS));
            }
          } else {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(0xf44242);
            eb.setTitle("⚠  Barão - Erro (Formato)");
            eb.addField("Erro: ", " - Formato inválido tente: [Art(número)º/§(númeroº)].", false);
            eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

            e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(4, TimeUnit.SECONDS));
          }
				}

			} else {
        permerror(e);
      }
    }

  }


  @Override
  public String getInvoke() {

    return "ban";
  }

  public void format(GuildMessageReceivedEvent e) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(0xf44242);
    eb.setTitle("⚠  Barão - Erro (Comando)");
    eb.addField(" -ban <usuários> <artigo/paragrafo>", "Banir usuários.", false);
    eb.addField(" -ban <usuários> <artigo/paragrafo> <dias>", "Banir usuários temporariamente.", false);
    eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
    e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
  }

  public void permerror(GuildMessageReceivedEvent e) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(0xf44242);
    eb.setTitle("🚫  Barão - Aviso (Permissão)");
    eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
    eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());

    e.getChannel().sendMessage(eb.build()).queue(message -> message.delete().queueAfter(3, TimeUnit.SECONDS));
  }
}
