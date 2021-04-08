package edu.brown.cs.student.spothouse;

import java.util.List;

/**
 * A class representing a song to be voted on in a Lobby
 */
public class Song implements Votable {
    private boolean used = false;
    User requester;
    private List<User> positiveVotes;
    private List<User> negativeVotes;
    private double score = 0;

    /**
     * A Constructor for the Song class
     * @param requester - the User who requested the song
     */
    public Song(User requester) {
        this.requester = requester;
    }

    @Override
    public void setUsed(boolean used) {
        this.used = used;
    }

    @Override
    public boolean getUsed() {
        return this.used;
    }

    @Override
    public int getPositiveVotes() {
        return this.positiveVotes.size();
    }

    @Override
    public List<User> getPositiveVoteList() {
        return positiveVotes;
    }

    @Override
    public int getNegativeVotes() {
        return this.negativeVotes.size();
    }

    @Override
    public List<User> getNegativeVoteList() {
        return negativeVotes;
    }

    @Override
    public void addPositiveVote(User user) {
        if (!positiveVotes.contains(user)) {
            positiveVotes.add(user);
        }
    }

    @Override
    public void addNegativeVote(User user) {
        if (!negativeVotes.contains(user)) {
            negativeVotes.add(user);
        }
    }

    @Override
    public double getScore() {
        return this.score;
    }

    @Override
    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public User getRequester() {
        return this.requester;
    }

    @Override
    public void updateRequester() {
        //todo: check to make sure User is still in Lobby
        //todo: remove all of a user's songs after they leave Lobby? (only if they are removed?)
        this.requester.addPositiveVotes(this.positiveVotes.size());
        this.requester.addNegativeVotes(this.negativeVotes.size());

        double userScore = 0;

        for (User user: positiveVotes) {
            userScore += user.getScore();
        }
        for (User user: negativeVotes) {
            userScore -= user.getScore();
        }

        this.requester.setScore(userScore);
    }

    @Override
    public void updateScore() {
        this.score = 0.0;
        for (User u : positiveVotes) {
            this.score += u.getScore();
        }

        for (User u : negativeVotes) {
            this.score -= u.getScore();
        }
    }
}
