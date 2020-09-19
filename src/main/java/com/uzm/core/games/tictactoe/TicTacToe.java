package com.uzm.core.games.tictactoe;

import com.google.common.collect.Maps;
import com.uzm.core.DiscordBot;
import com.uzm.core.audio.utilities.TimeManager;
import com.uzm.core.games.tictactoe.TTTPlayer.Icon;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class TicTacToe {

  public static int[][] WINCOMBOS = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
  public static Object[][] CAMP_ARRAY =
    {{"1⃣", 0, 78, 195}, {"2⃣", 1, 210, 195}, {"3⃣", 2, 340, 195}, {"4⃣", 3, 78, 320}, {"5⃣", 4, 210, 320}, {"6⃣", 5, 340, 320}, {"7⃣", 6, 78, 450}, {"8⃣", 7, 210, 450},
      {"9⃣", 8, 340, 450}};


  private Guild guild;
  private TextChannel privatetextchannel;

  private TTTCamp[] board =
    {new TTTCamp(0, "1⃣"), new TTTCamp(1, "2⃣"), new TTTCamp(2, "3⃣"), new TTTCamp(3, "4⃣"), new TTTCamp(4, "5⃣"), new TTTCamp(5, "6⃣"), new TTTCamp(6, "7⃣"), new TTTCamp(7, "8⃣"),
      new TTTCamp(8, "9⃣")};

  private TTTPlayer current;
  private ArrayList<TTTPlayer> winner;
  private ArrayList<TTTCamp> plays;
  private TTTPlayer creator;
  private TTTPlayer visitor;
  private ArrayList<File> files;


  private Member player;


  private State state;

  private TextChannel channel;


  private Message display;
  private Message table;

  public static HashMap<Message, TicTacToe> game = Maps.newHashMap();

  public TicTacToe(Member owner, Guild guild, TextChannel channel) {
    this.guild = guild;
    this.player = owner;
    this.channel = channel;
    this.creator = new TTTPlayer(owner);
    this.winner = new ArrayList<>();
    this.plays = new ArrayList<>();
    this.files = new ArrayList<>();
    invite();
  }

  public void invite() {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(0xf44242);
    eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Convite)");
    eb.addField("Quer jogar?", "Em 15 segundos o convite expira, corre!", false);
    eb.addField("Obs:", "O primeiro que clicar no '✅' participa!", false);
    eb.setFooter("Jogo iniciado por " + getOwner().getNickname(), getOwner().getUser().getAvatarUrl());
    state = State.INVITE;
    channel.sendMessage(eb.build()).queue(message -> {
      message.addReaction("✅").queue();
      game.put(message, this);
      TimerTask task = new TimerTask() {

        public void run() {
          if (getVisitor() == null) {
            message.delete().queue();
            game.remove(message);
            destroy(getOwner());
            cancel();
          }
        }
      };
      new java.util.Timer().schedule(task, TimeManager.createtime(TimeUnit.SECONDS, 15));
    });
  }

  public void buildChannel() {
    Category category = DiscordBot.get(getGuild()).getGames();

    String id = category.createTextChannel("🧓 " + getCreator().getMember().getUser().getName() + " 🎌 " + getVisitor().getMember().getUser().getName()).complete().getId();
    TextChannel channel = getGuild().getTextChannelById(id);
    DiscordBot bot = DiscordBot.get(getGuild());
    bot.actionChannel(channel, false);
    privatetextchannel = channel;

  }

  public void startChoose(Member tic2) {
    setState(State.CHOOSE);
    setVisitor(tic2);
    display.addReaction("⭕").queue();
    display.addReaction("❌").queue();
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(0xf44242);
    eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Escolha)");
    eb.addField("Jogo entre:", getOwner().getNickname() + " ⚔ " + tic2.getNickname(), false);
    eb.addField("Escolha o seu simbolo:", "Em 20 segundos a escolha será aleatória!", false);
    eb.addField("Alerta!", "O primeiro que clicar '⭕' ou '❌' será o dono do símbolo", false);
    eb.setFooter("Jogo iniciado por " + getOwner().getNickname(), getOwner().getUser().getAvatarUrl());
    display.editMessage(eb.build()).queue();
    updater();
  }

  public void updater() {
    TimerTask task = new TimerTask() {
      int count = 1;

      @lombok.SneakyThrows
      public void run() {

        if (getGameState() == State.CHOOSE) {
          if (count > 0 && count < 21) {
            count++;
          }
          if (count == 21) {
            buildChannel();
            System.out.println("[TTT] Automatic choose complete!");


            getCreator().setIcon(Icon.O);
            getVisitor().setIcon(Icon.X);
            setState(State.INGAME);
            display.clearReactions().queue();

            display.delete().queue();
            current = getCreator();



            try {
              TTTImage.buildGameImage(TicTacToe.this, false, null);
            }catch (IOException ignore) {}



          } else {
            if (getCreator().getIcon() != null && getVisitor().getIcon() != null) {
              buildChannel();
              setState(State.INGAME);
              display.delete().queue();
              current = getCreator();
              TTTImage.buildGameImage(TicTacToe.this, false, null);

            }
          }

        }

        if (getGameState() == State.INGAME) {
          for (int[] combo : WINCOMBOS) {
            if (board[combo[0]].isFree()) {
              continue;
            }

            if (getCreator() == board[combo[0]].getPlayer() && getCreator() == board[combo[1]].getPlayer() && getCreator() == board[combo[2]].getPlayer()) {
              setState(State.END);
              setWinner(getCreator());
              break;
            } else if (getVisitor() == board[combo[0]].getPlayer() && getVisitor() == board[combo[1]].getPlayer() && getVisitor() == board[combo[2]].getPlayer()) {
              setState(State.END);
              setWinner(getVisitor());
              break;
            } else if (plays.size() == 9) {
              setState(State.END);
              setWinner(getCreator());
              setWinner(getVisitor());
              break;
            }

          }
          if (winner.size() == 1) {
            getDisplayMessage().delete().submit();
            getTableMessage().delete().submit();
            try {
              TTTImage.buildGameImage(TicTacToe.this, true, winner);
            }catch (IOException ignore) {}
            destroyGame();
            cancel();

          }
          if (winner.size() == 2) {
            getDisplayMessage().delete().submit();
            getTableMessage().delete().submit();
            try {
              TTTImage.buildGameImage(TicTacToe.this, true, winner);
            }catch (IOException ignore) {}
            destroyGame();
            cancel();

          }

        }

      }
    };
    new Timer().schedule(task, 0, TimeManager.createtime(TimeUnit.SECONDS, 1));
  }

  public void destroyGame() {
    TimerTask task = new TimerTask() {

      @Override
      public void run() {
        List<Message> message = getPrivateTextChannel().getHistory().retrievePast(6).complete();
        for (Message msg : message) {
          if (msg.getContentRaw().contains("Jogo:")) {
            msg.delete().queue();
            break;
          }
        }


        privatetextchannel.delete().queue();
        DiscordBot.get(getGuild()).channels.clear();
        clearAll();

      }
    };
    new Timer().schedule(task, TimeManager.createtime(TimeUnit.SECONDS, 4));

    destroy(getOwner());



  }

  public void draw(int x, Member mb) {
    if (getGameState() == State.INGAME) {
     try {
       TTTImage.buildGameImage(TicTacToe.this, false, null);
     }catch (IOException ignore) {}
    }

  }

  public void delete(File file) {
    files.remove(file);
    try {
      Files.deleteIfExists(Paths.get(file.getPath()));
      Date date = new Date(System.currentTimeMillis());
      DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
      String finalformat = "(" + format.format(date) + ") [SUCESSS TTT]";
      System.out.println(finalformat + " Table file has been deleted.");



    } catch (IOException e) {
      System.out.println("[TTT] An error occoured while we try delete this file.");

      Date date = new Date(System.currentTimeMillis());
      DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
      String finalformat = "(" + format.format(date) + ") [ERROR TTT]";
      System.out.println(finalformat + " An error occoured while we try delete this file.");
    }
  }

  public void clearAll() {
    try {


      Date date = new Date(System.currentTimeMillis());
      DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
      String finalformat = "(" + format.format(date) + ") [SUCESSS TTT]";
      System.out.println(finalformat + " Table file has been deleted.");
      for (File files : files) {
        Files.deleteIfExists(Paths.get(files.getPath()));

      }
    } catch (IOException e) {
      Date date = new Date(System.currentTimeMillis());
      DateFormat format = new SimpleDateFormat("EEE, d 'de' MMM 'de' yyy 'às' h:mm");
      String finalformat = "(" + format.format(date) + ") [ERROR TTT]";
      System.out.println(finalformat + " An error occoured while we try delete this file.");
    }


  }

  public void setDisplay(Message m) {
    display = m;

  }

  public void setGameMessage(Message m) {
    table = m;
  }

  public void setWinner(TTTPlayer player) {
    if (!winner.contains(player)) {
      winner.add(player);
    }
  }

  public Member getOwner() {
    return player;
  }

  public Message getTableMessage() {
    return table;
  }

  public Message getDisplayMessage() {
    return display;
  }

  public void setVisitor(Member m) {
    visitor = new TTTPlayer(m);
  }

  public void setCurrent(TTTPlayer m) {
    current = m;
  }

  public TTTPlayer getCreator() {
    return creator;
  }

  public TTTPlayer getVisitor() {
    return visitor;
  }

  public TextChannel getPrivateTextChannel() {
    return privatetextchannel;
  }

  public State getGameState() {
    return state;
  }

  public ArrayList<File> getFiles() {
    return files;
  }

  public void setState(State state) {
    this.state = state;
  }

  public void updateField(int x, TTTPlayer ttt) {
    board[x].setPlayer(ttt);
    if (!plays.contains(board[x]))
      plays.add(board[x]);
  }

  public void update(Message m, File file) {
    game.put(m, this);

    table = m;
    /*
    mainfile = file;
    if (!files.contains(file))
      files.add(file);*/
  }

  public TTTCamp[] getBoard() {
    return board;
  }

  public TTTPlayer getCurrent() {
    return current;

  }


  public TextChannel getDefaultChannel() {
    return channel;
  }


  public static Map<Member, TicTacToe> datas = new HashMap<>();

  public Guild getGuild() {
    return guild;
  }

  public static TicTacToe build(Guild g, Member m, TextChannel channel) {
    if (datas.containsKey(m)) {
      return get(m);
    }

    datas.put(m, new TicTacToe(m, g, channel));

    return get(m);
  }

  public static void destroy(Member p) {
    if (datas.containsKey(p)) {

      try {
        for (File f : Objects.requireNonNull(get(p)).getFiles()) {
          Files.deleteIfExists(Paths.get(f.getPath()));
          System.out.println("[TTT] Table file has been deleted [" + Objects.requireNonNull(get(p)).getFiles().size() + "]");
        }
      } catch (IOException e) {
        System.out.println("[TTT] An error occoured while we try delete this file.");
      }

      datas.remove(p);

    }
  }

  public static TicTacToe get(Member g) {
    return datas.getOrDefault(g, null);

  }

  public TTTPlayer getPlayer(Member m) {
    if (getCreator().getMember() == m) {
      return getCreator();
    }
    if (getVisitor().getMember() == m) {
      return getVisitor();
    }
    return getCreator();
  }

  public static List<TicTacToe> getDatas() {
    return new ArrayList<>(datas.values());
  }

  public enum State {
    INVITE,
    CHOOSE,
    INGAME,
    END
  }

  public static Object[][] getCampArray() {
    return CAMP_ARRAY;
  }
}

