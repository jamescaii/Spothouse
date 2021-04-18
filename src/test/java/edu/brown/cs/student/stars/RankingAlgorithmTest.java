package edu.brown.cs.student.stars;

import edu.brown.cs.student.spothouse.RankingAlgorithm;
import edu.brown.cs.student.spothouse.Result;
import edu.brown.cs.student.spothouse.Song;
import edu.brown.cs.student.spothouse.User;
import org.checkerframework.checker.units.qual.A;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RankingAlgorithmTest {
  private Song song1;
  private Song song2;
  private Song song3;
  private User user1;
  private User user2;
  private ArrayList<Song> songList;
  private ArrayList<User> userList;

  @Before
  public void setUp() {
    user1 = new User("David", true);
    user2 = new User("Adam", false);
    song1 = new Song("Viva La Vida", "Coldplay",
            "https://i.scdn.co/image/ab67616d0000b273e21cc1db05580b6f2d2a3b6e",
            "spotify:track:1mea3bSkSGXuIRvnydlB5b", "David", 0);
    song2 = new Song("Dangerous", "Kardinal Offishall",
            "https://i.scdn.co/image/ab67616d0000b273cedc24abc2e757d13b386d1e",
            "spotify:track:4NOZ35Dhucr6UlVyLOtktd", "Adam", 0);
    song3 = new Song("Black Mamba", "aespa",
            "https://i.scdn.co/image/ab67616d0000b2736f248f7695eb544a3a1955c5",
            "spotify:track:1t2qYCAjUAoGfeFeoBlK51", "Adam", 0);
    songList = new ArrayList<>();
    userList = new ArrayList<>();
    songList.add(song1);
    songList.add(song2);
    songList.add(song3);
    userList.add(user1);
    userList.add(user2);
  }

  @After
  public void tearDown() {
    user1 = null;
    user2 = null;
    song1 = null;
    song2 = null;
    song3 = null;
    songList = null;
    userList = null;
  }

  /**
   * Tests the update ranking function
   */
  @Test
  public void testAddUpdateRanking() {
    setUp();
    // David votes for Adam's song, increasing the score of his song and the score Adam
    Result r = RankingAlgorithm.updateRankings("Dangerous", "David", 1, true, songList, userList);
    assertEquals(r.getSongList().get(1).getVotes(), 0.5, 0.001);
    assertEquals(r.getUserList().get(1).getScore(), 0.5, 0.001);
    r = RankingAlgorithm.updateRankings("Viva La Vida", "Adam", 1, true, songList, userList);
    assertEquals(r.getSongList().get(0).getVotes(), 0.5312093733737563, 0.001);
    assertEquals(r.getUserList().get(0).getScore(), 0.5312093733737563, 0.001);
    r = RankingAlgorithm.updateRankings("Black Mamba", "David", 1, true, songList, userList);
    assertEquals(r.getSongList().get(2).getVotes(), 0.5331518766661613, 0.001);
    // Adam's score should be David's normalized score added to his current score
    assertEquals(r.getUserList().get(1).getScore(), 1.0331518766661612, 0.001);
    r = RankingAlgorithm.updateRankings("Viva La Vida", "Adam", 2, true, songList, userList);
    // Score of Viva La Vida is updated based on Adam's current score
    assertEquals(r.getSongList().get(0).getVotes(), 1.6596401538321128, 0.001);
    tearDown();
  }

  @Test
  public void testSubtractUpdateRanking() {
    setUp();
    // David downvotes Adam's song, decreasing the score of his song and the score Adam
    Result r = RankingAlgorithm.updateRankings("Dangerous", "David", 1, false, songList, userList);
    assertEquals(r.getSongList().get(1).getVotes(), -0.5, 0.001);
    assertEquals(r.getUserList().get(1).getScore(), -0.5, 0.001);
    r = RankingAlgorithm.updateRankings("Viva La Vida", "Adam", 1, false, songList, userList);
    // we see that Adam's vote has less power since his song was previously downvoted
    assertEquals(r.getSongList().get(0).getVotes(), -0.46879062662624377, 0.001);
    assertEquals(r.getUserList().get(0).getScore(), -0.46879062662624377, 0.001);
    r = RankingAlgorithm.updateRankings("Black Mamba", "David", 1, false, songList, userList);
    assertEquals(r.getSongList().get(2).getVotes(), -0.47073407616763746, 0.001);
    // Adam's score should be David's normalized score subtracted from his current score
    assertEquals(r.getUserList().get(1).getScore(), -0.970734076167637, 0.001);
    r = RankingAlgorithm.updateRankings("Viva La Vida", "Adam", 2, false, songList, userList);
    // Score of Viva La Vida is updated based on Adam's current score
    assertEquals(r.getSongList().get(0).getVotes(), -1.3480409186255191, 0.001);
    tearDown();
  }

  @Test
  public void testAddOnFire() {
    User u1 = new User("David", true);
    User u2 = new User("Adam", false);
    User u3 = new User("Livia", false);
    User u4 = new User("Michelle", false);
    u1.addScore(1.2);
    u2.addScore(0.5);
    u3.addScore(1.2);
    u4.addScore(-0.2);
    ArrayList<User> tempList = new ArrayList<>();
    tempList.add(u1);
    tempList.add(u2);
    tempList.add(u3);
    tempList.add(u4);
    ArrayList<User> lst = RankingAlgorithm.addOnFire(tempList, 4);
    assertEquals(lst.get(0).isOnFire(), true);
    assertEquals(lst.get(0).getUsername(), "David");
    assertEquals(lst.get(1).isOnFire(), false);
    assertEquals(lst.get(2).isOnFire(), false);
    assertEquals(lst.get(3).isOnFire(), false);
  }
}
