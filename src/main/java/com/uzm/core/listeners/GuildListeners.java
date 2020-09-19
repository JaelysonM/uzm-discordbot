package com.uzm.core.listeners;

import com.uzm.core.DiscordBot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Objects;

public class GuildListeners extends ListenerAdapter {
  @Override
  public void onGuildMemberJoin(GuildMemberJoinEvent e) {
    DiscordBot bot = DiscordBot.get(e.getGuild());

    bot.getGroup(e.getMember());
    bot.newplayer(e.getMember());
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(Color.green);
    eb.setTitle("✋ 👺  Receita federal - Usuário novo!");

    eb.addField("Nome: ", e.getUser().getAsMention(), true);
    eb.addField("ID: ", e.getUser().getId(), true);
    eb.addField("Cargo: ", "[Novato]", true);
    eb.addField("Quer convidar alguém? ", "Vá em <#584729906341085188>", false);
    eb.addField("Regras? ", "Vá em <#593898915267477515> ", false);
    eb.addField("", "Seja bem-vindo barão 👺 ", false);
    eb.setThumbnail(e.getUser().getAvatarUrl());
    eb.setFooter("✅ Aprovado por Barão!", e.getJDA().getSelfUser().getAvatarUrl());

    Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(eb.build()).queue();

  }
}
