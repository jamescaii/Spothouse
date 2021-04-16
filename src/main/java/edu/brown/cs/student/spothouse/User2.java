package edu.brown.cs.student.spothouse;

import java.util.ArrayList;

public class User2 implements Comparable<User2> {
  private String username;
  private double score = 0;
  private boolean onFire = false;
  private boolean isHost;
  private ArrayList<Song2> userSongs = new ArrayList<>();
  public User2(String username, boolean isHost) {
    this.username = username;
    this.isHost = isHost;
  }

  public boolean isHost() {
    return isHost;
  }

  public void setOnFire(boolean onFire) {
    this.onFire = onFire;
  }

  public String getUsername() {
    return username;
  }

  public double getScore() {
    return score;
  }

  public boolean isOnFire() {
    return onFire;
  }

  public void addScore(double s) {
    score += s;
  }

  public void subScore(double s) {
    score -= s;
  }

  public void addSong(Song2 song) {
    userSongs.add(song);
  }

  public boolean songExists(String name) {
    for (Song2 s: userSongs) {
      if (name.equals(s.getName())) {
        return true;
      }
    }
    return false;
  }

  public double getNormalizedScore() {
    return (1 / (1 + Math.pow(Math.E, (-1 * score))));
  }

  public int compareTo(User2 u) {
    return u.getScore() < this.getScore() ? -1 : 1;
  }
}
