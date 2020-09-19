package com.uzm.core;

import com.uzm.core.audio.listeners.TrackListeners;
import com.uzm.core.audio.utilities.YoutubeBridge;
import com.uzm.core.configuration.JSONConfiguration;
import com.uzm.core.controllers.CommandController;
import com.uzm.core.controllers.CustomNameUpdater;
import com.uzm.core.controllers.RulesController;
import com.uzm.core.games.GameEvents;
import com.uzm.core.listeners.CommandListeners;
import com.uzm.core.listeners.GuildListeners;
import com.uzm.core.listeners.NicknameListeners;
import com.uzm.core.utilities.Manager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

import javax.security.auth.login.LoginException;

public class Core {
  private static String prefix = "!";
  private static JDA jda;
  private static YoutubeBridge youtubeBridge;
  private static CommandController commandController;


  public static void main(String[] args) {
    new Core();
  }


  public Core() {
    try {

      if (JSONConfiguration.load()) {
        jda = new JDABuilder(AccountType.BOT).setToken(JSONConfiguration.BOT_TOKEN).build().awaitReady();

        jda.setAutoReconnect(true);
        jda.addEventListener(new GuildListeners(), new GameEvents(), new TrackListeners(), new CommandListeners(), new NicknameListeners());

        commandController = new CommandController();
        youtubeBridge = new YoutubeBridge();
        Manager.maintask();
        RulesController.load();
        CustomNameUpdater.loadApplicableMembers();


        for (Guild g : jda.getGuilds()) {
          if (DiscordBot.get(g) == null) {
            DiscordBot bot = DiscordBot.create(g);

            bot.setDisplayCategory(g.getCategoryById("581979023442444301"));
            bot.nameTick();
          }
        }
        System.out.println(
          "[Uzm Discord Bot] Conectado ao WebSocket do DiscordAPI. [Ping: " + jda.getGatewayPing() + ", Usuários: " + jda.getUsers().size() + ", Servidores: " + jda.getGuilds().size() + "]");
      } else {
        System.err.println("[Uzm Discord Bot] Não foi possível carregar o bot pois há algum token faltando no config.json.");
      }
    }catch (LoginException | InterruptedException ignore) {}
  }

  public static JDA getJDA() {
    return jda;
  }

  public static String getPrefix() {
    return prefix;
  }

  public static void updatePrefix(String s) {
    prefix = s;
  }

  public static YoutubeBridge getYoutubeBridge() {
    return youtubeBridge;
  }


  public static CommandController getCommandController() {
    return commandController;
  }
}