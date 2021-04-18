package edu.brown.cs.student.spothouse;

/**
 * Class to store information about a user in the room.
 */
public class User implements Comparable<User> {

  private final String username;
  private final boolean isHost;
  private double score = 0;
  private boolean onFire = false;
  private static final double SIGMOID_SCALE = -0.25;

  /**
   * Constructor receives the user name and the host boolean.
   * @param username user name
   * @param isHost boolean to mark the user as host or guest.
   */
  public User(String username, boolean isHost) {
    this.username = username;
    this.isHost = isHost;
  }

  /**
   * accessor method for host marker.
   * @return boolean for host
   */
  public boolean isHost() {
    return isHost;
  }

  /**
   * mutator method for onFire marker for the user.
   * @param onFire boolean for onFire marker.
   */
  public void setOnFire(boolean onFire) {
    this.onFire = onFire;
  }

  /**
   * accessor method for user name.
   * @return user name
   */
  public String getUsername() {
    return username;
  }

  /**
   * accessor method for user score.
   * @return user score
   */
  public double getScore() {
    return score;
  }

  /**
   * accessor method for onFire marker.
   * @return onFire boolean.
   */
  public boolean isOnFire() {
    return onFire;
  }

  /**
   * adds scores.
   * @param s score value to add
   */
  public void addScore(double s) {
    score += s;
  }

  /**
   * subtracts scores.
   * @param s score value to subtract
   */
  public void subScore(double s) {
    score -= s;
  }

  /**
   * a normalized score value used for ranking.
   * @return normalzied score.
   */
  public double getNormalizedScore() {
    return (1 / (1 + Math.pow(Math.E, (SIGMOID_SCALE * score))));
  }

  /**
   * override comparable method to customize based on scores.
   * @param u user to compare
   * @return integer to represent the comparison.
   */
  @Override
  public int compareTo(User u) {
    return u.getScore() < this.getScore() ? -1 : 1;
  }
}
