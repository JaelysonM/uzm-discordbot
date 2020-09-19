package com.uzm.core.listeners;

import com.uzm.core.Core;
import com.uzm.core.DiscordBot;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class CommandListeners extends ListenerAdapter {
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    String rw = e.getMessage().getContentRaw();

    DiscordBot bot = DiscordBot.get(e.getGuild());
    assert bot != null;
    bot.getGroup(Objects.requireNonNull(e.getMember()));
    if (!e.getAuthor().isBot() && !e.getMessage().isWebhookMessage() && rw.startsWith(Core.getPrefix())) {
      Core.getCommandController().handleCommand(e);

    }
  }
}
