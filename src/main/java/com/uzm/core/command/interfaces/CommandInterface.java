package com.uzm.core.command.interfaces;

import java.util.List;

import com.uzm.core.DiscordBot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface CommandInterface {

    void handle(DiscordBot bot ,List<String> args, GuildMessageReceivedEvent event);
    Object getInvoke();

}