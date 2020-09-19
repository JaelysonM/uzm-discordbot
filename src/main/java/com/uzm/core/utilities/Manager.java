package com.uzm.core.utilities;


import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.uzm.core.Core;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.enums.QueueState;
import com.uzm.core.audio.utilities.Scroller;
import com.uzm.core.audio.utilities.TimeManager;

import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.TextChannel;


public class Manager {


  public static Activity[] frases = {Activity.playing("Minecraft (Mas não conta pra ninguém)"), Activity.watching("jogo do Ceará!\n - Eai e tricolor como tá?"),
    Activity.listening("Como é barão? Eu nem escuto música."), Activity.watching("xbarao.com - \nComo é barão?"), Activity.watching("O show do barão!"),
    Activity.listening("É quessia,quezia,quessa ou kessia como é? "), Activity.streaming("O jogo do Ceará: Ta 2x0", ""), Activity.watching("Ei barão to cansado ó, to online a N/A")

  };
  public static String api_token = "AIzaSyAQ5VIlEmkv9mqQ6vCeupjxC6ZifmoX3ds";
  public static String[] icons = {"https://i.imgur.com/MiakKcv.png", "https://i.imgur.com/U87lIcS.png", "https://i.imgur.com/whoGE9M.png", "https://i.imgur.com/3gk2EkC.png",
    "https://i.imgur.com/OfumWRN.png",};

  public static String[][] rules = {{"", "", ""}};


  public enum NType {
    INTEGER,
    FLOAT,
    DOUBLE
  }


  public static void maintask() {

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
      Scroller scroller = null;
      int minutes = 0;
      int minute = 59;

      int icon_walker = 0;
      int phrase_walker = 0;

      public void run() {
       /* for (Guild g : Core.getJDA().getGuilds()) {
          DiscordBot bot = DiscordBot.get(g);

          if (Core.getPlayer().getPlayer(g).getPlayingTrack() != null) {
            AudioTrack t = Core.getPlayer().getPlayer(g).getPlayingTrack();
            if (scroller == null)
              scroller = new Scroller(t.getInfo().title, t.getInfo().title.length(), Math.min(t.getInfo().title.length(), 5));


            String string = scroller.next();

            if (string.trim().length() == 0) {
              string = "...";
            }
            String st = state(g);

            String display = "(" + st + TimeManager.time(t.getPosition()) + "/" + TimeManager.time(t.getDuration()) + ") 🔊 " + string;

            if (display.length() > 100) {
              bot.getDisplayCategory().getManager().setName(display.substring(0, 90)).queue();
            } else {
              bot.getDisplayCategory().getManager().setName(display).queue();
            }


          } else {
            if (!bot.getDisplayCategory().getName().equalsIgnoreCase("🗑 A fila está limpa!")) {
              bot.getDisplayCategory().getManager().setName("🗑 A fila está limpa!").complete();


              Date date = new Date(System.currentTimeMillis());
              DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
              String finalformat = "(" + format.format(date) + ") [UPDATE NAME]";
              System.out.println(finalformat + " A display category name has been reset to : '🗑 A fila está limpa'.");
              scroller = null;

            }
          }

        }
*/

        // Avatar

        if (minute != 60) {
          minute++;
        } else {
          minute = 0;
          minutes++;
          if (phrase_walker == (frases.length - 1)) {
            InternalStats internalStats = InternalStats.collect();
            long secondsUptime = internalStats.getUptime();
            long days = secondsUptime / 86400;
            long hours = (secondsUptime / 3600) % 24;
            long minutes = (secondsUptime / 60) % 60;
            long seconds = secondsUptime % 60;
            StringBuilder uptime = new StringBuilder();
            if (days > 0)
              uptime.append(days).append(" d ");
            if (hours > 0)
              uptime.append(hours).append(" h ");
            if (minutes > 0)
              uptime.append(minutes).append(" m ");
            uptime.append(seconds).append(" s");
            Core.getJDA().getPresence().setActivity(Activity.watching("Ei barão to cansado ó, to online a " + uptime));
            phrase_walker = 0;
          } else {



            Core.getJDA().getPresence().setActivity(frases[phrase_walker]);
            phrase_walker += 1;
          }
          Date date = new Date(System.currentTimeMillis());
          DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");

          String finalformat = "(" + format.format(date) + ") [UPDATE LOG]";
          System.out.println(finalformat + " Game presence changed!");
        }
        if (minutes == 15) {
          try {
            BufferedImage image = ImageIO.read(new URL(icons[icon_walker]));
            File outputfile = new File("avatar.png");
            ImageIO.write(image, "png", outputfile);
            Icon avatar = Icon.from(outputfile);
            Core.getJDA().getSelfUser().getManager().setAvatar(avatar).complete();
            Files.deleteIfExists(Paths.get(outputfile.getPath()));

            Date date = new Date(System.currentTimeMillis());
            DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
            String finalformat = "(" + format.format(date) + ") [UPDATE ICON]";
            System.out.println(finalformat + " Bot icon has been changed.");

          } catch (IOException e) {

            Date date = new Date(System.currentTimeMillis());
            DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
            String finalformat = "(" + format.format(date) + ") [ERROR ICON]";
            System.out.println(finalformat + " Error to download new icon.");
          }
          if (icon_walker >= (icons.length - 1)) {
            icon_walker = 0;
          } else {
            icon_walker += 1;
          }
          minutes = 0;
        }

        // Automatic FNShop

        /*
         * Disabled
         */
            	/*
            	  if (TimeManager.isTime("21:40:00")) {
                	  
                	  for (DiscordBot bot : DiscordBot.getDatas()) {
                		  bot.getShop().sendMessage("!fm shop").queue( message-> {
                  			   message.delete().queueAfter(10, TimeUnit.MILLISECONDS);
                		   });  
                	  }
               		                		   
               	   }
            	*/
      }
    };

    timer.schedule(task, 0, (TimeManager.createtime(TimeUnit.SECONDS, 1)));
  }

  public enum Ranks {
    UZM("[UZM] ", " | \uD83C\uDFA9", true),
    CAPACIDADE("[C] ", " | \uD83E\uDD47", true),
    MANAGER("[M] ", " | 🔒", true),
    BARÃO("", " | 💎", true),
    GUI_FRIEND("[Gui's Friend] ", " | 👌", false),
    JM_FRIEND("[Jm's Friend] ", " | 👌", false),
    EXECUTIVE("[E] ", " | 👑", true),
    SUPERAMIGO("[S] ", " | 🤞", false),
    GRUPO_DO_TINDER("[GDT] ", " | \uD83D\uDD25", false),
    AMIGUINHO("", " | 👊", false),
    NOVATO("[N] ", "", true);

    private String s;
    private String p;
    private boolean b;

    Ranks(String p, String s, boolean b) {
      this.s = s;
      this.p = p;
      this.b = b;
    }

    public String getSuffix() {
      return s;
    }

    public String getPrefix() {
      return p;
    }

    public boolean isStaff() {
      return b;
    }
  }







}
