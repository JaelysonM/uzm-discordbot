package com.uzm.core.listeners;

import com.uzm.core.DiscordBot;
import com.uzm.core.controllers.CustomNameUpdater;
import com.uzm.core.utilities.Manager;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class NicknameListeners extends ListenerAdapter {
  @Override
  public void onGuildVoiceJoin(GuildVoiceJoinEvent e) {
    DiscordBot bot = DiscordBot.get(e.getGuild());
    assert bot != null;
    bot.newplayer(e.getMember());
    if (bot.getGroup(e.getMember()) == Manager.Ranks.UZM) {
      Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(e.getMember().getAsMention() + "  Bem-vindo de volta meu patrão!")
        .queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
    }
    if (bot.getGroup(e.getMember()) == Manager.Ranks.EXECUTIVE) {
      Objects.requireNonNull(e.getGuild().getDefaultChannel()).sendMessage(e.getMember().getAsMention() + "  Bem-vindo de volta meu doutor!")
        .queue(message -> message.delete().queueAfter(1, TimeUnit.MINUTES));
    }
    if (  CustomNameUpdater.get(e.getMember().getIdLong()) !=null)
      bot.updateTag(e.getMember(), CustomNameUpdater.get(e.getMember().getIdLong()).getCurrentName());
    else
      bot.updatetag(e.getMember());

  }

  @Override
  public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent e) {
    DiscordBot bot = DiscordBot.get(e.getGuild());

    assert bot != null;
    bot.getUsers().remove(e.getMember());
    bot.newplayer(e.getMember());
    if (  CustomNameUpdater.get(e.getMember().getIdLong()) !=null)
      bot.updateTag(e.getMember(), CustomNameUpdater.get(e.getMember().getIdLong()).getCurrentName());
    else
      bot.updatetag(e.getMember());

  }

  @Override
  public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent e) {
    DiscordBot bot = DiscordBot.get(e.getGuild());
    assert bot != null;
    bot.getUsers().remove(e.getMember());
    bot.newplayer(e.getMember());

    if (  CustomNameUpdater.get(e.getMember().getIdLong()) !=null)
      bot.updateTag(e.getMember(), CustomNameUpdater.get(e.getMember().getIdLong()).getCurrentName());
    else
      bot.updatetag(e.getMember());

  }
}
