package edu.brown.cs.student.spothouse.ranking;

import java.util.ArrayList;

import edu.brown.cs.student.spothouse.components.Song;
import edu.brown.cs.student.spothouse.components.User;

/**
 * Class to wrap the constantly updating songs and users list.
 */
public class Result {

  private final ArrayList<Song> songList;
  private final ArrayList<User> userList;

  /**
   * Constructor receives two lists.
   * @param songList list of current songs
   * @param userList list of current users
   */
  public Result(ArrayList<Song> songList, ArrayList<User> userList) {
    this.songList = songList;
    this.userList = userList;
  }

  /**
   * accessor method for song list.
   * @return song list
   */
  public ArrayList<Song> getSongList() {
    return songList;
  }

  /**
   * accessor method for user list.
   * @return user list
   */
  public ArrayList<User> getUserList() {
    return userList;
  }
}
