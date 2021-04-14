package edu.brown.cs.student.spothouse;

import java.util.List;

/**
 * An interface representing an object that can be voted on in a Lobby
 */
public interface Votable {

    /**
     * Sets whether or not the Votable has been used in the Queue already
     */
    void setUsed(boolean used);

    /**
     * Gets whether or not the Votable has already gone through the Queue and been used
     * @return - whether or not the Votable is used
     */
    boolean getUsed();

    /**
     * Gets the number of positive votes for the Votable
     * @return - the number of positive votes
     */
    int getPositiveVotes();

    /**
     * Gets the list of Users that have voted positively for the Votable
     * @return - the list of Users
     */
    List<User> getPositiveVoteList();

    /**
     * Gets the number of negative votes for the Votable
     * @return - the number of negative votes
     */
    int getNegativeVotes();

    /**
     * Gets the list of Users that have voted negatively for the Votable
     * @return - the list of Users
     */
    List<User> getNegativeVoteList();

    /**
     * Adds a positive vote to the Votable
     */
    void addPositiveVote(User user);

    /**
     * Adds a negative vote to the Votable
     */
    void addNegativeVote(User user);

    /**
     * Gets the score of the Votable
     * @return - the score of the Votable, as determined by the votes and Users
     */
    double getScore();

    /**
     * Sets the score of the Votable (called from the Elo class)
     */
    void setScore(double score);

    /**
     * Gets the User that requested the Votable in the first place
     * @return - the User that requested the Votable
     */
    User getRequester();

    /**
     * Updates the score of the Requester of the song
     */
    void updateRequester();

    /**
     * Updates the Score of the Votable
     */
    void updateScore();

    /**
     * Functionality for what happens when the Votable is played
     */
    void played();
}
