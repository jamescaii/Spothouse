package edu.brown.cs.student.spothouse;

public class User2 {
  private String username;
  private int id;
  private int score = 0;
  private boolean onFire = false;
  public User2(String username, int id) {
    this.username = username;
    this.id = id;
  }
  public void addScore(int s) {
    score += s;
  }
}
