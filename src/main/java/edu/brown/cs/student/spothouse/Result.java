package edu.brown.cs.student.spothouse;

import java.util.ArrayList;

public class Result {
  private ArrayList<Song> songList;
  private ArrayList<User> userList;
  public Result(ArrayList<Song> songList, ArrayList<User> userList) {
    this.songList = songList;
    this.userList = userList;
  }

  public ArrayList<Song> getSongList() {
    return songList;
  }

  public ArrayList<User> getUserList() {
    return userList;
  }
}
