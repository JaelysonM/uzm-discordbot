package com.uzm.core.controllers;


import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.uzm.core.command.*;
import com.uzm.core.command.interfaces.CommandInterface;
import org.jetbrains.annotations.NotNull;

import com.uzm.core.Core;
import com.uzm.core.DiscordBot;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class CommandController {

    private final Map<String, CommandInterface> commands = new HashMap<>();

    public CommandController() {
    	
    	addCommand(new ChatCommand());
    	addCommand(new NewMusicCommand());
    	addCommand(new PingCommand());
    	addCommand(new RestartCommand());
    	addCommand(new StopCommand());
    	addCommand(new RulesCommand());
    	addCommand(new TicTacCommand());
    	addCommand(new BanCommand());
      addCommand(new CustomNamesCommand());
    }

    private void addCommand(CommandInterface command) {
      if (command.getInvoke() instanceof String) {
        if (!commands.containsKey((String)command.getInvoke())) {
          commands.put((String)command.getInvoke(), command);
        }
      }else {
        for (String aliase : (String[])command.getInvoke()) {
          if (!commands.containsKey(aliase)) {
            commands.put(aliase, command);
          }
        }
      }

    }

    public Collection<CommandInterface> getCommands() {
        return commands.values();
    }

    public CommandInterface getCommand(@NotNull String name) {
        return commands.get(name);
    }

    public void handleCommand(GuildMessageReceivedEvent event) {
        final String prefix = Core.getPrefix();
        
        
        

        final String[] split = event.getMessage().getContentRaw().replaceFirst(
                "(?i)" + Pattern.quote(prefix), "").split("\\s+");
        final String invoke = split[0].toLowerCase();
        
        
        

        if (commands.containsKey(invoke)) {
           final List<String> args = Arrays.asList(split);
            DiscordBot bot = DiscordBot.get(event.getGuild());
            event.getMessage().delete().queue();
            commands.get(invoke
            		).handle(bot,args, event);
            
            
          
        }
    }
}