package edu.brown.cs.student.stars;

import edu.brown.cs.student.spothouse.Song;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class SongTest {
  private Song song;
  @Before
  public void setUp() {
    song = new Song("Viva La Vida", "Coldplay",
            "https://i.scdn.co/image/ab67616d0000b273e21cc1db05580b6f2d2a3b6e",
            "spotify:track:1mea3bSkSGXuIRvnydlB5b", "David", 0);
  }

  @After
  public void tearDown() {
    song = null;
  }

  /**
   * Tests whether we can correctly construct a song object.
   */
  @Test
  public void testSongConstruction() {
    setUp();
    assertNotNull(song);
    tearDown();
  }

  /**
   * Tests the getter functions in the song class
   */
  @Test
  public void testGetterFunctions () {
    setUp();
    assertEquals(song.getName(), "Viva La Vida");
    assertEquals(song.getArtist(), "Coldplay");
    assertEquals(song.getArtwork(), "https://i.scdn.co/image/ab67616d0000b273e21cc1db05580b6f2d2a3b6e");
    assertEquals(song.getUri(), "spotify:track:1mea3bSkSGXuIRvnydlB5b");
    assertEquals(song.getRequester(), "David");
    assertEquals(song.getVotes(), 0, 0.001);
    tearDown();
  }

  @Test
  public void testAddVote() {
    setUp();
    song.addVote(1);
    assertEquals(song.getVotes(), 1, 0.001);
    song.addVote(0.56);
    assertEquals(song.getVotes(), 1.56, 0.001);
    tearDown();
  }

  @Test
  public void testSubVote() {
    setUp();
    song.addVote(2);
    song.subVote(0.5);
    assertEquals(song.getVotes(), 1.5, 0.001);
    song.subVote(1);
    assertEquals(song.getVotes(), 0.5, 0.001);
    song.subVote(0.64);
    assertEquals(song.getVotes(), -0.14, 0.001);
    tearDown();
  }
}