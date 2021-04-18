package edu.brown.cs.student.stars;

import edu.brown.cs.student.spothouse.Song;
import edu.brown.cs.student.spothouse.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.*;

public class UserTest {
  private User hostUser;
  private User guestUser;

  @Before
  public void setUp() {
    hostUser = new User("David", true);
    guestUser = new User("Adam", false);
  }

  @After
  public void tearDown() {
    hostUser = null;
    guestUser = null;
  }

  /**
   * Tests whether we can correctly construct a song object.
   */
  @Test
  public void testSongConstruction() {
    setUp();
    assertNotNull(hostUser);
    assertNotNull(guestUser);
    tearDown();
  }

  /**
   * Tests setter and getter functions
   */
  @Test
  public void testSetterGetter() {
    setUp();
    assertEquals(guestUser.isHost(), false);
    assertEquals(hostUser.isHost(), true);
    guestUser.setOnFire(true);
    assertEquals(guestUser.isOnFire(), true);
    hostUser.setOnFire(false);
    assertEquals(hostUser.isOnFire(), false);
    assertEquals(guestUser.getUsername(), "Adam");
    assertEquals(hostUser.getUsername(), "David");
    assertEquals(guestUser.getScore(), 0, 0.001);
    tearDown();
  }

  /**
   * Tests the adding score function
   */
  @Test
  public void testAddScore() {
    setUp();
    guestUser.addScore(0);
    assertEquals(guestUser.getScore(), 0, 0.001);
    guestUser.addScore(1);
    assertEquals(guestUser.getScore(), 1, 0.001);
    guestUser.addScore(0.87);
    assertEquals(guestUser.getScore(), 1.87, 0.001);
    tearDown();
  }

  /**
   * Tests the subtracting score function
   */
  @Test
  public void testSubScore() {
    setUp();
    guestUser.subScore(0);
    assertEquals(guestUser.getScore(), 0, 0.001);
    guestUser.subScore(1);
    assertEquals(guestUser.getScore(), -1, 0.001);
    guestUser.subScore(0.87);
    assertEquals(guestUser.getScore(), -1.87, 0.001);
    tearDown();
  }

  /**
   * Tests the normalizing score function
   */
  @Test
  public void testNormalizedScore() {
    setUp();
    assertEquals(hostUser.getNormalizedScore(), 0.5, 0.001);
    hostUser.addScore(3.5);
    assertEquals(hostUser.getNormalizedScore(), 0.7058, 0.001);
    hostUser.subScore(7);
    assertEquals(hostUser.getNormalizedScore(), 0.2942, 0.001);
    hostUser.subScore(2.2);
    assertEquals(hostUser.getNormalizedScore(), 0.1939, 0.001);
    tearDown();
  }
}
