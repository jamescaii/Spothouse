package edu.brown.cs.student.spothouse;

/**
 * A class representing a Guest in the lobby, that can be signed-in or signed-out
 */
public class Guest implements User {
    private boolean signedIn = false;
    private boolean enFuego = false;
    private final String userName;
    private final int id;
    private int timesVoted = 0;
    private int positiveVotes = 0;
    private int negativeVotes = 0;
    private int songsRequested = 0;
    private int songsSincePlayed = 0;
    private int songsRemovedByHost = 0;
    private int songsPlayed = 0;
    private int songsLoved = 0;
    private double score = 0.5;

    /**
     * Constructor for the Guest class
     */
    public Guest(String userName, int id) {
        this.userName = userName;
        this.id = id;
    }

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
    public String getUserName() {
        return null;
    }

    @Override
    public int getUserID() {
        return 0;
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

    @Override
    public void votablePlayed() {
        this.songsSincePlayed = 0;
        songsPlayed++;
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
