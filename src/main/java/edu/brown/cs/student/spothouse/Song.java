package edu.brown.cs.student.spothouse;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;

/**
 * A class representing a song to be voted on in a Lobby
 */
public class Song implements Votable, Comparable<Song> {
    private boolean used = false;
    private final User requester;
    private final String name;
    private final String art;
    private final String artist;
    private final String uri;
    private final List<User> positiveVotes = new ArrayList<>();
    private final List<User> negativeVotes = new ArrayList<>();
    private double score = 0;

    /**
     * A Constructor for the Song class
     * @param requester - the User who requested the song
     */
    public Song(String name, String art, String artist, String uri, User requester) {
        this.name = name;
        this.art = art;
        this.artist = artist;
        this.uri = uri;
        this.requester = requester;
    }

    public String getName() {
        return this.name;
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
        if (!this.positiveVotes.contains(user)) {
            this.positiveVotes.add(user);
        }
    }

    public void removePositiveVote(User user) {
        this.positiveVotes.remove(user);
    }

    public void removeNegativeVote(User user) {
        this.negativeVotes.remove(user);
    }

    @Override
    public void addNegativeVote(User user) {
        if (!this.negativeVotes.contains(user)) {
            this.negativeVotes.add(user);
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
    public void played() {
        this.requester.votablePlayed();
    }

    @Override
    public List<String> getAttributes() {
        List<String> atts = new ArrayList<>();
        atts.add(this.name);
        atts.add(this.artist);
        atts.add(this.art);
        atts.add(this.uri);
        return atts;
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

    @Override
    public int compareTo(Song s) {
        return s.getScore() < this.score ? -1 : 1;
    }
}
