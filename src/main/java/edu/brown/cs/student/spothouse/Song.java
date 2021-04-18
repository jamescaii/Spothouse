package edu.brown.cs.student.spothouse;

public class Song implements Comparable<Song> {
  private String name;
  private String requester;
  private String artist;
  private String artwork;
  private String uri;
  private double votes;
  public Song(String name, String artist, String artwork, String uri, String requester, int votes) {
    this.name = name;
    this.artist = artist;
    this.artwork = artwork;
    this.uri = uri;
    this.requester = requester;
    this.votes = votes;
  }
  public String getName() {
    return name;
  }
  public String getArtist() {
    return artist;
  }
  public String getArtwork() {
    return artwork;
  }
  public String getUri() {
    return uri;
  }
  public String getRequester() {
    return requester;
  }
  public double getVotes() {
    return votes;
  }
  public void addVote(double vote) {
    // when we add the vote we want to look at the user score to scale it
    votes += vote;
  }
  public void subVote(double vote) {
    // when we sub the vote we want to look at the user score to scale it
    votes -= vote;
  }
  @Override
  public int compareTo(Song s) {
    return s.getVotes() < this.getVotes() ? -1 : 1;
  }
}
