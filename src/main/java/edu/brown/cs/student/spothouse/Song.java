package edu.brown.cs.student.spothouse;
/**
 * Class to store various information about song.
 */
public class Song implements Comparable<Song> {

  private final String name;
  private final String artist;
  private final String artwork;
  private final String uri;
  private final String requester;
  private double votes;

  /**
   * constructor initiates the final instance variables.
   * @param name song name
   * @param artist song artist
   * @param artwork song album artwork url
   * @param uri song uri
   * @param requester username of the requester
   * @param votes number of votes
   */
  public Song(String name, String artist, String artwork, String uri, String requester, int votes) {
    this.name = name;
    this.artist = artist;
    this.artwork = artwork;
    this.uri = uri;
    this.requester = requester;
    this.votes = votes;
  }

  /**
   * accessor method for name.
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * accessor method for artist.
   * @return artist
   */
  public String getArtist() {
    return artist;
  }

  /**
   * accessor method for artwork.
   * @return artwork
   */
  public String getArtwork() {
    return artwork;
  }

  /**
   * accessor method for uri.
   * @return uri
   */
  public String getUri() {
    return uri;
  }

  /**
   * accessor method for requester.
   * @return requester
   */
  public String getRequester() {
    return requester;
  }

  /**
   * accessor method for vote count.
   * @return vote count
   */
  public double getVotes() {
    return votes;
  }

  /**
   * adds votes.
   * @param vote number of votes to add
   */
  public void addVote(double vote) {
    // when we add the vote we want to look at the user score to scale it
    votes += vote;
  }

  /**
   * subtracts votes.
   * @param vote number of votes to subtract
   */
  public void subVote(double vote) {
    // when we sub the vote we want to look at the user score to scale it
    votes -= vote;
  }

  /**
   * override comparable method to customize the vote count comparison.
   * @param s song to compare
   * @return integer to represent the comparison.
   */
  @Override
  public int compareTo(Song s) {
    return s.getVotes() < this.getVotes() ? -1 : 1;
  }
}
