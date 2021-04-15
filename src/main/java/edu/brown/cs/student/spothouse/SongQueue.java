package edu.brown.cs.student.spothouse;

import java.util.ArrayList;
import java.util.List;

public class SongQueue {
  private List<Song> songList = new ArrayList<>();
  public SongQueue() { }
  public List<Song> getSongList() {
    return songList;
  }
  public void setSongList(List<Song> songList) {
    this.songList = songList;
  }
}
