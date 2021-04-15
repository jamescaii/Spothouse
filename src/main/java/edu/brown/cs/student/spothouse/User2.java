package edu.brown.cs.student.spothouse;

public class User2 {
  private String username;
  private int score = 0;
  private boolean onFire = false;
  private boolean isHost;
  public User2(String username, boolean isHost) {
    this.username = username;
    this.isHost = isHost;
  }

  public boolean isHost() {
    return isHost;
  }

  public String getUsername() {
    return username;
  }

  public int getScore() {
    return score;
  }

  public boolean isOnFire() {
    return onFire;
  }

  public void addScore(int s) {
    score += s;
  }
}
