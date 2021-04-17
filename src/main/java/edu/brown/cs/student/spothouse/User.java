package edu.brown.cs.student.spothouse;

import java.util.ArrayList;

public class User implements Comparable<User> {
  private String username;
  private double score = 0;
  private boolean onFire = false;
  private boolean isHost;
  private ArrayList<Song> userSongs = new ArrayList<>();
  public User(String username, boolean isHost) {
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

  public void addSong(Song song) {
    userSongs.add(song);
  }

  public boolean songExists(String name) {
    for (Song s: userSongs) {
      if (name.equals(s.getName())) {
        return true;
      }
    }
    return false;
  }

  public double getNormalizedScore() {
    return (1 / (1 + Math.pow(Math.E, (-0.25 * score))));
  }

  public int compareTo(User u) {
    return u.getScore() < this.getScore() ? -1 : 1;
  }
}
