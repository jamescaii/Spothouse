package edu.brown.cs.student.spothouse;

/**
 * A class representing the Host of a Lobby
 */
public class Host implements User {
    private final String userName;
    private final int id;
    private boolean enFuego = false;
    private int timesVoted = 0;
    private int positiveVotes = 0;
    private int negativeVotes = 0;
    private int songsRequested = 0;
    private int songsSincePlayed = 0;
    private int songsPlayed = 0;
    private int songsLoved = 0;
    private double score = 0.5;

    /**
     * A constructor for the Host class
     */
    public Host(String userName, int id) {
        this.userName = userName;
        this.id = id;
    }

    @Override
    public String getUserName() {
        return this.userName;
    }

    @Override
    public int getUserID() {
        return this.id;
    }

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
//        setScore(positiveVotes
//                - negativeVotes
//                + (0.1 * timesVoted)
//                + (0.1 * songsSincePlayed)
//                + songsPlayed
//                + songsLoved);

        setScore( (1 * score)
                + (0.1 * timesVoted)
                + (0.1 * songsSincePlayed)
                + (0.1 * songsPlayed)
                + (0.1 * songsLoved));
    }

    @Override
    public void votablePlayed() {
        this.songsSincePlayed = 0;
        songsPlayed++;
    }

    public void setTopUser(Boolean bool) {
        this.enFuego = bool;
    }
}
