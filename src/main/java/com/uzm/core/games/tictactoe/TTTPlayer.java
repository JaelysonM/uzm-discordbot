package com.uzm.core.games.tictactoe;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Member;

public class TTTPlayer {
  private Member member;
  private Icon i;

  public TTTPlayer(Member m) {
    member = m;
  }

  public Member getMember() {
    return member;
  }


  public void setIcon(Icon i) {
    this.i = i;
  }

  public Icon getIcon() {
    return this.i;
  }

  public enum Icon {
    X("❌"),
    O("⭕");
    private String i;

    Icon(String i) {
      this.i = i;
    }

    public String get() {
      return i;
    }
  }


}
