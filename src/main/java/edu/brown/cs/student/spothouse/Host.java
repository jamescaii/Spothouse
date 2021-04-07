package edu.brown.cs.student.spothouse;

/**
 * A class representing the Host of a Lobby
 */
public class Host implements User {
    private int timesVoted = 0;
    private int positiveVotes = 0;
    private int negativeVotes = 0;
    private int songsRequested = 0;
    private int songsSincePlayed = 0;
    private int songsPlayed = 0;
    private int songsLoved = 0;
    private double score = 0;

    /**
     * A constructor for the Host class
     */
    public Host() {}

    @Override
    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void incrementTimesVoted() {
        this.timesVoted++;
    }

    @Override
    public void clearVotes() {
        this.positiveVotes = 0;
        this.negativeVotes = 0;
        this.score = 0;
    }

    @Override
    public void addPositiveVotes(int positiveVotes) {
        this.positiveVotes += positiveVotes;
    }

    @Override
    public void addNegativeVotes(int negativeVotes) {
        this.negativeVotes += negativeVotes;
    }

    @Override
    public int getTimesVoted() {
        return this.timesVoted;
    }

    @Override
    public void incrementVotablesRequested() {
        this.songsRequested++;
    }

    @Override
    public int getVotablesRequested() {
        return this.songsRequested;
    }

    @Override
    public int getPositiveVotes() {
        return this.positiveVotes;
    }

    @Override
    public int getNegativeVotes() {
        return this.negativeVotes;
    }

    @Override
    public void updateScore() {
        setScore(positiveVotes
                - negativeVotes
                + (0.1 * timesVoted)
                + (0.1 * songsSincePlayed)
                + songsPlayed
                + songsLoved);
    }
}
