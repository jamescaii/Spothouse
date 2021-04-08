package edu.brown.cs.student.spothouse;

/**
 * A class representing a Guest in the lobby, that can be signed-in or signed-out
 */
public class Guest implements User {
    boolean signedIn = false;
    private int timesVoted = 0;
    int positiveVotes = 0;
    int negativeVotes = 0;
    int songsRequested = 0;
    int songsSincePlayed = 0;
    int songsRemovedByHost = 0;
    int songsPlayed = 0;
    int songsLoved = 0;
    double score = 0.5;

    /**
     * Constructor for the Guest class
     */
    public Guest() {}

    @Override
    public void clearVotes() {
        this.positiveVotes = 0;
        this.negativeVotes = 0;
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
    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public double getScore() {
        return this.score;
    }

    @Override
    public void incrementTimesVoted() {
        this.timesVoted++;
    }

    @Override
    public void updateScore() {
//        setScore(positiveVotes
//                - negativeVotes
//                + (0.1 * timesVoted)
//                + (0.1 * songsSincePlayed)
//                - songsRemovedByHost
//                + songsPlayed
//                + songsLoved);

        setScore( (0.5)
                + (1 * score)
                + (0.1 * timesVoted)
                + (0.1 * songsSincePlayed)
                - (0.5 * songsRemovedByHost)
                + (0.1 * songsPlayed)
                + (0.1 * songsLoved));
    }

    /**
     * Sets the Guests signed-in status to true
     */
    public void signIn() {
        this.signedIn = true; //todo: authenticate users through Spotify API
    }

    /**
     * Sets the Guests signed-in status to false
     */
    public void signOut() {
        this.signedIn = false; //todo: delete user data from database
    }
}
