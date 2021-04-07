package edu.brown.cs.student.spothouse;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing the algorithm that determines the
 * @param <T> - the type of Votable
 */
public class Elo<T extends Votable> {

    private final Lobby<T> lobby;
    private final List<User> users;
    private final int numUsers;
    private double totalScore;

    public Elo(Lobby<T> lobby) {
        this.lobby = lobby;
        this.users = lobby.getUsers();
        numUsers = users.size();
    }

    public void updateScores() {
        for (User user : users) {
            user.clearVotes();
        }

        for (T votable : lobby.getQueueHistory()) {
            votable.updateRequester();
        }

        for (T votable : lobby.getQueue()) {
            votable.updateRequester();
        }

        for (User user: users) {
            user.updateScore();
            totalScore += user.getScore();
        }

        for (User user: users) {
            logisticFunction(user);
        }

        for (Votable v : lobby.getQueue()) {
            v.updateScore();
        }
    }

    private void logisticFunction(User user) {
        int k = 1; //steepness of Logistic Function
        user.setScore(1 / (1 + Math.pow(Math.E, -1 * (k * (user.getScore()/ totalScore))))); //logistic function
    }
}
