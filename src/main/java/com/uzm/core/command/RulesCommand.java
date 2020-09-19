package com.uzm.core.command;


import java.awt.Color;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.uzm.core.DiscordBot;
import com.uzm.core.audio.utilities.TimeManager;

import com.uzm.core.command.interfaces.CommandInterface;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class RulesCommand implements CommandInterface {

	  
	@Override
	public void handle(DiscordBot bot,List<String> args, GuildMessageReceivedEvent e) {

		if (args.get(0).equalsIgnoreCase("regras")) {
			
			
		     switch (args.get(1)) {
			case "fixar":
				if (bot.getGroup(e.getMember()).isStaff()) {
					 EmbedBuilder eb2 = new EmbedBuilder();
					 //É proibido qualquer tipo de desrespeito, tanto no âmbito moral quanto ético.
			    	 eb2.setColor(Color.YELLOW);
			    	 eb2.setTitle("📜  Regras [" + TimeManager.formatDateBR(new Date(System.currentTimeMillis()))  + "]");
			    	 eb2.setDescription("**Regras do servidor __Uzamigo's Squad__:**\n"
			    	 		+ "\n__Art. 1º [Moral]__ ```fix\n§ 1º É proibido qualquer tipo de desrespeito, tanto no âmbito moral quanto ético. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 2º Xingamentos intencionais,com a finalidade de ferir o próximo, não serão tolerados no servidor. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 3º É proibido atitudes racistas ou discriminatórias. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 4º Desrespeitar staffs, principalmente CEOs, é proibido e imperdoável. [Sucessivel a banimento]\n```"
			    	 		+ "\n__Art. 2º [Segurança]__ "
			    	 		+ "```fix\n§ 1º A divulgação de outros servidores é proibída. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 2º A divulgação de links suspeitos que contenham vírus, malwares ou spywares é proibída. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 3º Divulgar conteúdo adulto em nossos canais de texto é terminantemente proibido. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 4º Divulgar imagens ou conteúdo de terceiros sem autorização é proibido. [Sucessivel a banimento e prisão (Código penal brasileiro)]\n```"
			    	 		+ "\n__Art. 3º [Canais]__ "
			    	 		+ "```fix\n§ 1º A divulgação de outros servidores é proibída. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 2º Ficar gritando em canais de voz, pertubando os usuários é proibido no servidor. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 3º Falar no canal de música não é permitido. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 4º Comandos só podem ser executados no seu devido canal.\n"
			    	 		+ "\n§ 5º Não pertube staffs para entrar em suas salas entre em 'Sala para mover' e aguarde.\n"
			    	 		+ "\n§ 6º É proibido abusar de bugs tanto de permissões quanto de comandos. [Sucessivel a banimento]\n```"
			    			);
	
			    	 eb2.setFooter("Solicitadas por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
			    	 eb2.setTimestamp(OffsetDateTime.now());
			    	 e.getChannel().sendMessage(eb2.build()).queue();
					return;
				}	
				break;

			default:
				
				if (bot.getGroup(e.getMember()).isStaff()) {
					 EmbedBuilder eb2 = new EmbedBuilder();
					
			    	 eb2.setColor(Color.YELLOW);
			    	 eb2.setTitle("📜  Regras [" + TimeManager.formatDateBR(new Date(System.currentTimeMillis()))  + "]");
			    	 eb2.setDescription("**Regras do servidor __Uzamigo's Squad__:**\n"
			    	 		+ "\n__Art. 1º [Moral]__ ```fix\n§ 1º É proibido qualquer tipo de desrespeito, tanto no âmbito moral quanto ético. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 2º Xingamentos intencionais,com a finalidade de ferir o próximo, não serão tolerados no servidor. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 3º É proibido atitudes racistas ou discriminatórias. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 4º Desrespeitar staffs, principalmente CEOs, é proibido e imperdoável. [Sucessivel a banimento]\n```"
			    	 		+ "\n__Art. 2º [Segurança]__ "
			    	 		+ "```fix\n§ 1º A divulgação de outros servidores é proibída. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 2º A divulgação de links suspeitos que contenham vírus, malwares ou spywares é proibída. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 3º Divulgar conteúdo adulto em nossos canais de texto é terminantemente proibido. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 4º Divulgar imagens ou conteúdo de terceiros sem autorização é proibido. [Sucessivel a banimento e prisão (Código penal brasileiro)]\n```"
			    	 		+ "\n__Art. 3º [Canais]__ "
			    	 		+ "```fix\n§ 1º A divulgação de outros servidores é proibída. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 2º Ficar gritando em canais de voz, pertubando os usuários é proibido no servidor. [Sucessivel a banimento]\n"
			    	 		+ "\n§ 3º Falar no canal de música não é permitido. [Sucessivel a silenciamento]\n"
			    	 		+ "\n§ 4º Comandos só podem ser executados no seu devido canal.\n"
			    	 		+ "\n§ 5º Não pertube staffs para entrar em suas salas entre em 'Sala para mover' e aguarde.\n"
			    	 		+ "\n§ 6º É proibido abusar de bugs tanto de permissões quanto de comandos. [Sucessivel a banimento]\n```"
			    			);
	
			    	 eb2.setFooter("Solicitadas por " + e.getMessage().getAuthor().getName(), e.getMessage().getAuthor().getAvatarUrl());
			    	 eb2.setTimestamp(OffsetDateTime.now());
			  
			    	 e.getChannel().sendMessage(eb2.build()).queue( message ->
			    	  {
			    		  message.delete().queueAfter(30, TimeUnit.SECONDS);
			    	  });	
					return;
				}
				break;
			}
		}
	   
	}


	@Override
	public String getInvoke() {

		return "regras";
	}

}
