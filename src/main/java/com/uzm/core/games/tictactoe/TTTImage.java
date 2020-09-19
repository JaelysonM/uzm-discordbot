package com.uzm.core.games.tictactoe;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.uzm.core.Core;
import com.uzm.core.games.tictactoe.TTTPlayer.Icon;
import com.uzm.core.games.tictactoe.TicTacToe.State;

import com.uzm.core.image.ImageBuilder;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.utils.AttachmentOption;

public class TTTImage {



  public static void buildGameImage(TicTacToe ticTacToe, boolean finalGame, ArrayList<TTTPlayer> winnersList) throws IOException {

    BufferedImage background = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics2D = background.createGraphics();
    graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    graphics2D.drawImage(ImageBuilder.TTT_BACKGROUND, 0, 0, null);
    graphics2D.setFont(new Font("Arial Black", Font.BOLD, 11));
    graphics2D.drawString(ticTacToe.getCreator().getIcon() == Icon.O ? ticTacToe.getCreator().getMember().getUser().getName() : ticTacToe.getVisitor().getMember().getUser().getName(), 68, 41);
    graphics2D.drawString(ticTacToe.getCreator().getIcon() == Icon.X ? ticTacToe.getCreator().getMember().getUser().getName() : ticTacToe.getVisitor().getMember().getUser().getName(), 338, 41);
    graphics2D.setFont(new Font("Arial", Font.BOLD, 120));
    if (!finalGame) {
      for (TTTCamp camp : ticTacToe.getBoard()) {
        if (!camp.isFree())
          graphics2D.drawString(camp.getPlayer().getIcon() == TTTPlayer.Icon.O ? "O" : "X", (int) TicTacToe.getCampArray()[camp.getPosition()][2], (int) TicTacToe.getCampArray()[camp.getPosition()][3]);

      }
    }

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(background, "png", os);

    InputStream is = new ByteArrayInputStream(os.toByteArray());

    ticTacToe.setCurrent((ticTacToe.getCurrent() == ticTacToe.getVisitor() ? ticTacToe.getCreator() : ticTacToe.getVisitor()));

    EmbedBuilder embedBuilder = new EmbedBuilder();
    embedBuilder.setColor(new Color(0, 255, 183));
    embedBuilder.setAuthor("Jogo da velha 🕹 (Em jogo) ", null, "https://i.imgur.com/sqj929K.gif");
    embedBuilder.setImage("attachment://file.png");
    embedBuilder.setDescription("\n**Observação**: Reaja com o emote correspondente a posição que você deseja colocar uma peça.");
    embedBuilder.addField("Jogo entre:",
      ticTacToe.getOwner().getNickname() + " (" + ticTacToe.getCreator().getIcon().get() + ") ⚔ " + ticTacToe.getVisitor().getMember().getNickname() + " (" + ticTacToe.getVisitor().getIcon().get() + ")", false);
    embedBuilder.setFooter("Jogo iniciado por " + ticTacToe.getOwner().getNickname(), ticTacToe.getOwner().getUser().getAvatarUrl());
    if (!finalGame)  {
      embedBuilder.addField("Jogada de:", ticTacToe.getCurrent().getMember().getNickname(), false);
      ticTacToe.getPrivateTextChannel().sendFile(is, "file.png").embed(embedBuilder.build()).queue(message -> {
        ticTacToe.setGameMessage(message);
        ticTacToe.setDisplay(message);
        ticTacToe.update(message, null);
        for (TTTCamp b : ticTacToe.getBoard()) {
          if (b.isFree()) {
            if (message != null)
              message.addReaction(b.getReaction()).submit();
          }
        }
      });
    }else {

     if (winnersList.size() == 1) {
       TTTPlayer winner = winnersList.get(0);
       embedBuilder.addField("🏆 Vitória de :", winner.getMember().getNickname(), false);
     }else {
       embedBuilder.addField("⚖  Deu velha! O jogo empatou, bom jogo aos dois.", "", false);
     }
      ticTacToe.getPrivateTextChannel().sendFile(is, "file.png").embed(embedBuilder.build()).queue(message -> {
        ticTacToe.setGameMessage(message);
        ticTacToe.setDisplay(message);
        ticTacToe.update(message, null);
        message.clearReactions().queue();

      });
    }
  }

/*
  public static void buidPrimary(TicTacToe tic) throws IOException {
    BufferedImage base = ImageIO.read(new URL("https://i.imgur.com/qc7ESQJ.png"));
    Graphics2D g = base.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setFont(new Font("Arial Black", Font.BOLD, 11));
    g.drawString(tic.getCreator().getIcon() == Icon.O ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 68, 41);
    g.drawString(tic.getCreator().getIcon() == Icon.X ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 338, 41);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ImageIO.write(base, "png", os);

    InputStream is = new ByteArrayInputStream(os.toByteArray());
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(0xf44242);
    eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Em jogo)");
    eb.addField("Jogo entre:",
      tic.getOwner().getNickname() + " (" + tic.getCreator().getIcon().get() + ") ⚔ " + tic.getVisitor().getMember().getNickname() + " (" + tic.getVisitor().getIcon().get() + ")", false);
    eb.addField("Jogada de:", tic.getOwner().getNickname(), false);
    eb.setFooter("Jogo iniciado por " + tic.getOwner().getNickname(), tic.getOwner().getUser().getAvatarUrl());
    eb.setImage("attachment://file.png");

    tic.getPrivateTextChannel().sendFile(is, "file.png").embed(eb.build()).queue(message -> {
      message.addReaction("1⃣").queue();
      message.addReaction("2⃣").queue();
      message.addReaction("3⃣").queue();
      message.addReaction("4⃣").queue();
      message.addReaction("5⃣").queue();
      message.addReaction("6⃣").queue();
      message.addReaction("7⃣").queue();
      message.addReaction("8⃣").queue();
      message.addReaction("9⃣").queue();

      tic.setGameMessage(message);
      tic.setDisplay(message);
      tic.update(message, null);
    });
  }

  public static void drawNew(TicTacToe tic) {
    try {
      BufferedImage base = copyImage(ImageBuilder.TTT_BACKGROUND);
      Graphics2D g = base.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setFont(new Font("Arial Black", Font.BOLD, 11));
      g.drawString(tic.getCreator().getIcon() == Icon.O ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 68, 41);
      g.drawString(tic.getCreator().getIcon() == Icon.X ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 338, 41);

      g.setFont(new Font("Arial", Font.BOLD, 120));
      for (TTTCamp camp : tic.getBoard()) {
        if (!camp.isFree()) {
          g.drawString(camp.getPlayer().getIcon() == TTTPlayer.Icon.O ? "O" : "X", (int) TicTacToe.getCampArray()[camp.getPosition()][2], (int) tic.camp[camp.getPosition()][3]);
        }

      }


      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(base, "png", os);

      InputStream is = new ByteArrayInputStream(os.toByteArray());
      tic.setCurrent((tic.getCurrent() == tic.getCreator() ? tic.getVisitor() : tic.getCreator()));

      EmbedBuilder eb = new EmbedBuilder();
      eb.setColor(0xf44242);
      eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Em jogo)");
      eb.addField("Jogo entre:",
        tic.getOwner().getNickname() + " (" + tic.getCreator().getIcon().get() + ") ⚔ " + tic.getVisitor().getMember().getNickname() + " (" + tic.getVisitor().getIcon().get() + ")", false);
      eb.addField("Jogada de:", tic.getCurrent().getMember().getNickname(), false);
      eb.setImage("attachment://file.png");
      eb.setFooter("Jogo iniciado por " + tic.getOwner().getNickname(), tic.getOwner().getUser().getAvatarUrl());
      tic.getPrivateTextChannel().sendFile(is, "file.png").embed(eb.build()).queue(message -> {
        tic.setGameMessage(message);
        tic.setDisplay(message);
        tic.update(message, null);
        for (TTTCamp b : tic.getBoard()) {
          if (b.isFree()) {
            if (message != null)
                message.addReaction(b.getReaction()).queue();

          }
        }
      });



    } catch (Exception ignored) {}

  }

  public static void drawWin(TicTacToe tic, ArrayList<TTTPlayer> winner) {
    try {
      BufferedImage base = copyImage(ImageBuilder.TTT_BACKGROUND);

      Graphics2D g = base.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g.setFont(new Font("Arial Black", Font.BOLD, 11));
      g.drawString(tic.getCreator().getIcon() == Icon.O ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 68, 41);
      g.drawString(tic.getCreator().getIcon() == Icon.X ? tic.getCreator().getMember().getUser().getName() : tic.getVisitor().getMember().getUser().getName(), 338, 41);

      g.setFont(new Font("Arial", Font.BOLD, 120));
      for (TTTCamp camp : tic.getBoard()) {
        if (!camp.isFree()) {
          g.drawString(camp.getPlayer().getIcon() == TTTPlayer.Icon.O ? "O" : "X", (int) tic.camp[camp.getPosition()][2], (int) tic.camp[camp.getPosition()][3]);
        }

      }


      ByteArrayOutputStream os = new ByteArrayOutputStream();
      ImageIO.write(base, "png", os);

      InputStream is = new ByteArrayInputStream(os.toByteArray());
      tic.setCurrent((tic.getCurrent() == tic.getCreator() ? tic.getVisitor() : tic.getCreator()));


      if (winner.size() == 1) {
        TTTPlayer player = winner.get(0);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Vitória)");
        eb.addField("Jogo entre:",
          tic.getOwner().getNickname() + " (" + tic.getCreator().getIcon().get() + ") ⚔ " + tic.getVisitor().getMember().getNickname() + " (" + tic.getVisitor().getIcon().get() + ")", false);
        eb.addField("🏆 Vitória de :", player.getMember().getNickname(), false);
        eb.setImage("attachment://file.png");
        eb.setFooter("Jogo iniciado por " + tic.getOwner().getNickname(), tic.getOwner().getUser().getAvatarUrl());
        tic.getPrivateTextChannel().sendFile(is, "file.png").embed(eb.build()).queue(message -> {
          tic.setGameMessage(message);
          tic.setDisplay(message);
          tic.update(message, null);
          message.clearReactions().queue();

        });
      }

      if (winner.size() == 2) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(0xf44242);
        eb.setTitle("🕹  Barão - Jogos (Jogo da velha - Vitória)");
        eb.addField("Jogo entre:",
          tic.getOwner().getNickname() + " (" + tic.getCreator().getIcon().get() + ") ⚔ " + tic.getVisitor().getMember().getNickname() + " (" + tic.getVisitor().getIcon().get() + ")", false);
        eb.addField("⚖  Deu velha! O jogo empatou, bom jogo aos dois.", "", false);
        eb.setFooter("Jogo iniciado por " + tic.getOwner().getNickname(), tic.getOwner().getUser().getAvatarUrl());
        eb.setImage("attachment://file.png");
        tic.getPrivateTextChannel().sendFile(is, "file.png").embed(eb.build()).queue(message -> {
          tic.setGameMessage(message);
          tic.setDisplay(message);
          tic.update(message, null);
          message.clearReactions().queue();

        });
      }



    } catch (Exception e1) {
      e1.printStackTrace();
    }

  }
*/
  public static BufferedImage copyImage(BufferedImage source) {
    BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
    Graphics g = b.getGraphics();
    g.drawImage(source, 0, 0, null);
    g.dispose();
    return b;
  }
}
