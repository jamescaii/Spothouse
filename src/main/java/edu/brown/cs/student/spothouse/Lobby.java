package edu.brown.cs.student.spothouse;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * A class representing a Lobby that the users can join and vote on Votables
 */
public class Lobby<T extends Votable> {
    private final int lobbyID;
    private final int maxUsers;
    private Elo<T> elo;
    private final Host host;
    private final List<User> users = new ArrayList<>();
    private final List<User> topUsers = new ArrayList<>();
    private final Queue<T> queue = new PriorityQueue<>();
    private final List<T> queueHistory = new ArrayList<>();

    /**
     * A Constructor for the Lobby class
     * @param host - the host of the Lobby, who will have special permissions
     * @param maxUsers - the maximum number of users the lobby can support
     */
    public Lobby(Host host, int lobbyID, int maxUsers) {
        this.host = host;
        this.lobbyID = lobbyID;
        this.maxUsers = maxUsers;
        this.users.add(host);
    }

    /**
     * gets all of the users in the Lobby
     * @return - a List of Users that includes the host and every guest in the lobby
     */
    public List<User> getUsers() {
        return this.users;
    }

    /**
     * gets the top Users
     * @return - the list of Top Users, which is set in the given algorithm based on their scores
     */
    public List<User> getTopUsers() {
        return this.topUsers;
    }

    /**
     * sets all of the Top Users in the Lobby based on the
     * @param topUsers - the new list Top Users
     */
    public void setTopUsers(List<User> topUsers) {
        this.topUsers.clear();
        this.topUsers.addAll(topUsers);
    }

    /**
     * Adds a Votable to the Queue
     * @param votable - the Votable object that can be voted on and will be moved around the Queue
     */
    public void addVotable(T votable) {
        this.queue.add(votable);
    }

    public void removeVotable(String name, String artist, String art, String uri) {
        this.queue.removeIf(votable -> votable.getAttributes().get(0).equals(name)
                && votable.getAttributes().get(1).equals(artist)
                && votable.getAttributes().get(2).equals(art)
                && votable.getAttributes().get(3).equals(uri));
    }

    public T getVotable(String name, String artist, String art, String uri) {
        for (T votable : this.queue) {
            if (votable.getAttributes().get(0).equals(name)
                    && votable.getAttributes().get(1).equals(artist)
                    && votable.getAttributes().get(2).equals(art)
                    && votable.getAttributes().get(3).equals(uri)) {
                return votable;
            }
        }
        return null;
    }

    /**
     * Gets the history of what Votables have been used from the Queue
     * @return - the Votables that have been used off the Queue, with the first object
     *          being the last that was used
     */
    public List<T> getQueueHistory() {
        return this.queueHistory;
    }

    /**
     * The current Queue of Votables
     * @return - the Queue of Votables that the Users of the Lobby can vote on
     */
    public Queue<T> getQueue() {
        return this.queue;
    }

    /**
     * Adds a User to the User List
     * @param user - the User to add
     * @throws Exception - When the max lobby capacity has been hit
     */
    public void addUser(User user) throws Exception {
        if (this.users.size() <= maxUsers) {
            this.users.add(user); //todo: check to see if they are already in Lobby?
        } else {
            throw new Exception("ERROR: Lobby is full.");
        }
    }

    /**
     * Removes a user from the User list
     * @param user - a User to remove from the Lobby, if they are not the host
     * @throws Exception - When the user to be removed is the host
     */
    public void removeUser(User user) throws Exception {
        if (!user.equals(host)) {
            users.remove(user); //todo: throw error if user is not in lobby?
        } else {
            throw new Exception("ERROR: Cannot remove host from Lobby");
        }
    }

    /**
     * Gets the current host of the Lobby
     * @return - the User that is the host of the Lobby
     */
    public Host getHost() {
        return this.host;
    }

    public User getUserByID(int userID) {
        for (User user : users) {
            if (user.getUserID() == userID) {
                return user;
            }
        }
        return null;
    }

    /**
     * Updates the scores of the Users
     */
    public void updateUserScores() {
       this.elo = new Elo<>(this);
       this.elo.updateScores(); //todo: synchronize this with new votable coming off the front of the queue
    }

    public String getQueueAsJson() {
        StringBuilder stringJson = new StringBuilder("");
        List<String> atts;
        for (T s : queue) {
            atts = s.getAttributes();
            stringJson.append("{\"name:\"").append(atts.get(0))
                    .append(",\"artist:\" ").append(atts.get(1))
                    .append(",\"art:\" ").append(atts.get(2))
                    .append(",\"uri:\" ").append(atts.get(3))
                    .append("}, ");
        }
        System.out.println(stringJson.toString());
        return stringJson.toString();
    }
}
