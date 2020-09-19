package com.uzm.core.games;

import com.uzm.core.games.tictactoe.TTTPlayer.Icon;
import com.uzm.core.games.tictactoe.TicTacToe;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class GameEvents extends ListenerAdapter {


  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
    if (e.getUser().isBot()) {
      return;
    }
    Message m = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
    if (m == null) {
      return;
    }
    if (TicTacToe.game.containsKey(m) && TicTacToe.game.get(m) != null) {
      TicTacToe tic = TicTacToe.game.get(m);
      switch (tic.getGameState()) {
        case INVITE:
          if (e.getMember() != tic.getOwner() && tic.getVisitor() == null) {
            if (e.getReactionEmote().getName().toLowerCase().equals("✅")) {
              m.clearReactions().complete();
              tic.setDisplay(m);
              tic.startChoose(e.getMember());
            }
          } else {
            e.getReaction().removeReaction(e.getUser()).queue();
          }
          break;
        case CHOOSE:
          if (e.getMember() == tic.getOwner() || e.getMember() == tic.getVisitor().getMember()) {

            int one = (e.getReactionEmote().getName().equals("⭕") ? m.getReactions().get(0).getCount() : m.getReactions().get(1).getCount());

            if (tic.getPlayer(e.getMember()).getIcon() == null) {
              if ((one - 1) == 1) {
                tic.getPlayer(e.getMember()).setIcon(e.getReactionEmote().getName().toLowerCase().equals("⭕") ? Icon.O : Icon.X);
              } else {
                e.getReaction().removeReaction(e.getUser()).queue();
              }
            } else {
              e.getReaction().removeReaction(e.getUser()).queue();
            }


          } else {
            e.getReaction().removeReaction(e.getUser()).queue();
          }

          break;
        case INGAME:
          if (tic.getCurrent().getMember() == e.getMember()) {
            if (e.getReactionEmote().getName().contains("1") || e.getReactionEmote().getName().contains("2") || e.getReactionEmote().getName().contains("3") || e.getReactionEmote()
              .getName().contains("4") || e.getReactionEmote().getName().contains("5") || e.getReactionEmote().getName().contains("6") || e.getReactionEmote().getName()
              .contains("7") || e.getReactionEmote().getName().contains("8") || e.getReactionEmote().getName().contains("9")) {
              int x = (Integer.parseInt(e.getReactionEmote().getName().split("")[0]) - 1);
              tic.updateField(x, tic.getPlayer(e.getMember()));
              tic.draw(x, e.getMember());
              m.delete().queue();
            } else {
              e.getReaction().removeReaction(e.getUser()).queue();
            }
          } else {
            e.getReaction().removeReaction(e.getUser()).queue();
          }
          break;
        default:
          e.getReaction().removeReaction(e.getUser()).queue();
          break;
      }
    }

  }
}
