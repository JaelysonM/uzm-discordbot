package com.uzm.core.command;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.uzm.core.Core;
import com.uzm.core.DiscordBot;
import com.uzm.core.command.interfaces.CommandInterface;
import com.uzm.core.games.tictactoe.TicTacToe;
import com.uzm.core.utilities.InternalStats;
import com.uzm.core.utilities.Manager.Ranks;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RestartCommand implements CommandInterface {
   public void clear(JDA jda) {
	   for (Guild g :jda.getGuilds()) {
		   for (TextChannel c : DiscordBot.get(g).getChannels()) {
			   if (c !=null) {
				   c.delete().complete();
			   }
		   }
		   
	   }	   
		 if (TicTacToe.getDatas().size() >0) {
         	   TicTacToe tic = TicTacToe.getDatas().get(0);        	   
         	   tic.clearAll();         
           } 
	    	System.out.println("[BOT] All private channel had been deleted!"); 
	    	
	    	System.out.println("[BOT] All game-images had been deleted!");
   }
	@Override
	public void handle(DiscordBot bot,List<String> args, GuildMessageReceivedEvent e) {

		if (args.get(0).equalsIgnoreCase("reiniciar")) {
			if (args.size() > 0) {
				if (bot.getGroup(e.getMember()) == Ranks.UZM) {
					 EmbedBuilder eb = new EmbedBuilder();
					 InternalStats internalStats = InternalStats.collect();
		        	  long secondsUptime = internalStats.getUptime();
		        	  long days = secondsUptime / 86400;
		              long hours = (secondsUptime / 3600) % 24;
		              long minutes = (secondsUptime / 60) % 60;
		              long seconds = secondsUptime % 60;
		              StringBuilder uptime = new StringBuilder();
		              if (days > 0) uptime.append(String.valueOf(days) + " d ");
		              if (hours > 0) uptime.append(String.valueOf(hours) + " h ");
		              if (minutes > 0) uptime.append(String.valueOf(minutes) + " m ");
		              uptime.append(String.valueOf(seconds) + " s");

		         
					 eb.setColor(Color.WHITE);
				  	 eb.setTitle("✋ 👺 Barão - Manager [Restart]");

				     eb.addField("Ping: ",Core.getJDA().getGatewayPing() + " ms", true);
				     eb.addField("Servidores: ",Core.getJDA().getGuilds().size() + " servidores", true);
				     eb.addField("Tempo ligado: ",uptime.toString(), true);
				     eb.setTimestamp(OffsetDateTime.now());
		        
		          	 eb.setFooter("Solicitado por " + e.getGuild().getMember(e.getAuthor()).getNickname(), e.getMessage().getAuthor().getAvatarUrl());
		          	e.getChannel().sendMessage(eb.build()).queue();
		          	try {
		          		if (System.getProperty("os.name").toLowerCase().contains("linux")) {
		          
		        			Runtime.getRuntime().exec("screen -d -m -S bot java -jar Uzm@Bot.jar");
		        			
		        			Core.getJDA().shutdown();
		          			System.exit(0);
	                     }
		          	}catch(Exception er) {
		          		er.printStackTrace();
		          	}
                      
				}else {
					 EmbedBuilder eb = new EmbedBuilder();
			    	 eb.setColor(0xf44242);
			    	 eb.setTitle("🚫  Barão - Aviso (Permissão)");
			    	 eb.addField("Aviso: ", " - Você não tem permissão para fazer isso.", false);
			         eb.setFooter("Solicitado por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
			  
			    	 e.getChannel().sendMessage(eb.build()).queue( message ->
			    	  {
			    		  message.delete().queueAfter(3, TimeUnit.SECONDS);
			    	  });	
				}
			}
		
		
         }
	   
	}
	
	public String executeCommand(String command) {

	    StringBuffer output = new StringBuffer();

	    Process p;
	    try {
	        p = Runtime.getRuntime().exec(command);
	        p.waitFor();
	        BufferedReader reader = 
	                        new BufferedReader(new InputStreamReader(p.getInputStream()));

	        String line = "";           
	        while ((line = reader.readLine())!= null) {
	            output.append(line + "\n");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return output.toString();
	}


	@Override
	public String getInvoke() {

		return "reiniciar";
	}
}
