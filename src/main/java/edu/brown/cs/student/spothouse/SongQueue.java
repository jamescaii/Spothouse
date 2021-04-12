package edu.brown.cs.student.spothouse;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class SongQueue {
  private List<Song> songList = new ArrayList<>();
  private Queue<Song> songs = new PriorityQueue<>();
  public SongQueue() { }
  public void addSong(Song song) {
    songs.add(song);
  }
  public Song getTopSong() {
    return songs.remove();
  }
}
