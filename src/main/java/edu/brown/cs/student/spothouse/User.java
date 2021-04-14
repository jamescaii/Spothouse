package edu.brown.cs.student.spothouse;

/**
 * An interface representing a User, which includes the Host and Guest
 */
public interface User {

    /**
     * Gets the username of the Host
     * @return - a String representing the username of the Host
     */
    String getUserName();

    /**
     * Gets the ID of the Host
     * @return - an integer representing the ID of the Host
     */
    int getUserID();

    /**
     * Sets the score of the User, which will determine their priority for selecting Votables
     * @param score - an integer representing the score of the User
     */
    void setScore(double score);

    /**
     * Gets the score of the User
     * @return - a double representing the Users current score
     */
    double getScore();

    /**
     * Increases the number of times this user has voted by one
     */
    void incrementTimesVoted();

    /**
     * Clears any voting data associated with the User
     */
    void clearVotes();

    /**
     * Adds Positive Votes to the User's data
     * @param positiveVotes - the number of positive votes to add
     */
    void addPositiveVotes(int positiveVotes);

    /**
     * Adds Negative Votes to the User's data
     * @param negativeVotes - the number of negative votes to add
     */
    void addNegativeVotes(int negativeVotes);

    /**
     * Gets the number of times this User has voted
     * @return - the number of votes this user has cast
     */
    int getTimesVoted();

    /**
     * Increments the number of Votables requests the User has
     */
    void incrementVotablesRequested();

    /**
     * Gets the number of times this User has requested a Votable
     * @return - the number of requests
     */
    int getVotablesRequested();

    /**
     * Gets the User's total positive votes
     * @return - the number of positive votes
     */
    int getPositiveVotes();

    /**
     * Gets the User's total negative votes
     * @return - the number of negative votes
     */
    int getNegativeVotes();

    /**
     * Updates the score of the User
     */
    void updateScore();

    /**
     * Functionality for when the User's requested song is played
     */
    void votablePlayed();
}
