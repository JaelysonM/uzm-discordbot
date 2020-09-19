package com.uzm.core.games.tictactoe;

public class TTTCamp {
  private int pos;
  private TTTPlayer player;
  private String reaction;

  public TTTCamp(int pos, String reaction) {
    this.pos = pos;
    this.reaction = reaction;
  }

  public void setPlayer(TTTPlayer player) {
    this.player = player;

  }

  public int getPosition() {
    return pos;
  }

  public String getReaction() {
    return reaction;
  }

  public TTTPlayer getPlayer() {
    return player;
  }

  public boolean isFree() {
		return player == null;
  }

}
