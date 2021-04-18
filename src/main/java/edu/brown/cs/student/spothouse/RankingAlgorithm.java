package edu.brown.cs.student.spothouse;

import java.util.ArrayList;

public final class RankingAlgorithm {
  private RankingAlgorithm() { }
  public static Result updateRankings(String toChange, String userName, int numAdd, boolean isIncrease,
                               ArrayList<Song> songArrayList, ArrayList<User> userArrayList) {
    ArrayList<Song> copySongList = songArrayList;
    ArrayList<User> copyUserList = userArrayList;
    for (Song s: copySongList) {
      if (s.getName().equals(toChange)) {
        // this gets finds the score of the user that upvoted the song and applies a sigmoid to it
        double voterScore = 0;
        for (User user: copyUserList) {
          if (userName.equals(user.getUsername())) {
            voterScore = user.getNormalizedScore();
          }
          break;
        }
        if (isIncrease) {
          System.out.println("Increased Score by " + voterScore);
          for (int i = 0; i < numAdd; i++) {
            s.addVote(voterScore);
          }
          // gets the requester of the song and adds the normalized score of the voter to it
          String requester = s.getRequester();
          for (User user: copyUserList) {
            if (requester.equals(user.getUsername())) {
              user.addScore(voterScore);
              break;
            }
          }
        } else {
          System.out.println("Decreased Score by " + voterScore);
          for (int i = 0; i < numAdd; i++) {
            s.subVote(voterScore);
          }
          // gets the requester of the song and subtracts the normalized score of the voter to it
          String requester = s.getRequester();
          for (User user: copyUserList) {
            if (requester.equals(user.getUsername())) {
              user.subScore(voterScore);
              break;
            }
          }
        }
      }
      break;
    }
    Result result = new Result(copySongList, copyUserList);
    return result;
  }

  public static ArrayList<User> addOnFire(ArrayList<User> userList, int listLength) {
    ArrayList<User> tempList = userList;
    int topUsersLength = listLength / 4;
    int start = 0;
    for (User u: tempList) {
      if (start < topUsersLength) {
        u.setOnFire(true);
      } else {
        u.setOnFire(false);
      }
      start++;
    }
    return tempList;
  }
}
