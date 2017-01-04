package chatroom_functionality;

import business.Message;
import business.PrivateMessage;
import business.User;
import callback_support.ChatRoomClientInterface;
import daos.PrivateMessageDao;
import daos.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;


//server

/**
 * 
 * @author Phillix
 * A class used to handle database requests and callbacks to clients
 * as well as to keep track of logged in users and unseen PrivateMessages
 */
public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoomInterface {
    
    private final ArrayList<PrivateMessage> privateMessages = new ArrayList();
    private final ArrayList<User> userList  = new ArrayList();
    private final HashMap<String,ChatRoomClientInterface> clientList = new HashMap();
    
    public ChatRoomImpl() throws RemoteException {
        
    }
    
    /**
     * A method to return the current list of logged on users
     * to be displayed to another logged on user, the requesting user is first filtered out
     * of this list before the list is returned
     * @param username name of user requesting the List
     * @return an ArrayList of usernames
     * @throws RemoteException 
     */
    @Override
    public ArrayList<String> getLoggedUsers(String username) throws RemoteException {
        
        if(userList.size() > 0 && username != null) {
            synchronized(userList) {
                return userList.stream()
                        .map(u -> u.getUsername())
                        .filter(name -> !name.equals(username))
                        .collect(Collectors.toCollection(ArrayList::new));
            }
        }
        return null;
    }
    
    /**
     * A method to check if the given username exists in the database
     * @param username the username to check
     * @return true if that name exists, false otherwise
     * @throws RemoteException 
     */
    @Override
    public boolean isValidUser(String username) throws RemoteException {
        
        UserDao uDao = new UserDao();
        return uDao.isValidUsername(username);
    }
    
    /**
     * A method to retrieve the private chat history between two users
     * This method creates an ArrayList of the previous private messages between two people
     * and sorts them via date and time
     * @param username the user requesting the messages
     * @param friendName the second party in the conversation
     * @return an ArrayList of Private Message Objects sorted by Date and Time or null if no messages exist
     * @throws RemoteException 
     */
    @Override
    public ArrayList<PrivateMessage> getPrivateMessageHistory(String username, String friendName) throws RemoteException {
        
        PrivateMessageDao pmDao = new PrivateMessageDao();
        if(username != null && friendName != null) {
            ArrayList<PrivateMessage> pms = pmDao.getConversation(username, friendName);
            if(pms != null && pms.size() > 0) {
                Collections.sort(pms);
                return pms;
            }
        } 
        return null;
    }

    /**
     * A method to handle general messages intended for public chat
     * If the message isnt null and there are clients logged in, the message will be broadcast to all users
     * @param m the message to be broadcast
     * @return boolean value indicating success eg: message was sent via callback to all users
     * @throws RemoteException 
     */
    @Override
    public boolean addMessage(Message m) throws RemoteException {
    
        if(m != null && clientList.size() > 0) {
            synchronized(clientList) {
                for(ChatRoomClientInterface client : clientList.values()) {
                    client.newMessageNotification(m.getAuthor() + ": " + m.getText());
                }
                return true;
            }
        }
        return false;
    }
    
    /**
     * A method to handle private message passing
     * if the message is added to the database, an attempt to send it to the intended user is made,
     * two callbacks are made with different formats, one for the sender and another for the receiver
     * to assist with visual clarity on the GUI,
     * if the message was not successfully sent to the recipient, it is placed in server memory
     * in an ArrayList to be sent out when next that user registers for callback
     * @param pm the private message to be sent
     * @return boolean indicating success based on whether the message was added to the database
     * @throws RemoteException 
     */
    @Override
    public boolean addPrivateMessage(PrivateMessage pm) throws RemoteException {
    
        boolean added = false;
        if(pm != null) {
            PrivateMessageDao pmDao = new PrivateMessageDao();
            if(pmDao.addMsg(pm) == 0) {
                added = true;
            }  
        }
        if(pm != null && added && clientList.size() > 0) {
            boolean sent = false;
            synchronized(clientList) {
                for(String username : clientList.keySet()) {
                    //Format for the receipient of the message
                    if(username.equals(pm.getReciever())) {
                        clientList.get(username).newPrivateMessageNotification(pm.getAuthor() + ": " + pm.getText());
                        sent = true;
                        //Format for the sender of the message
                    } else if(username.equals(pm.getAuthor())) {
                        clientList.get(username).newPrivateMessageNotification("To " + pm.getReciever()+ ": " + pm.getText());
                    }
                }
            }
            if(!sent) {
                synchronized(privateMessages) {
                    privateMessages.add(pm);
                    Collections.sort(privateMessages);
                }
            }
        }
        return added;
    }    
    
    /**
     * To act the same as login.
     * A method to register a new user to the system
     * It first checks the database that the username hasn't been taken already
     * if the name is unique they get added to the database and 
     * if they do not exist on the servers userList ArrayList, they are added
     * if both are successful a call back to current online users is carried out
     * to inform them of a new login
     * @param u the new user to register
     * @return a boolean value indicating the success of registering
     * @throws RemoteException
     */
    @Override
    public boolean register(User u) throws RemoteException {
        UserDao uDao = new UserDao();
        boolean added = false;
        // 0 = that user exists on the database
        if(u != null && uDao.checkUname(u.getUsername()) != 0) {
            uDao.register(u);
            synchronized(userList) {
                for(User user : userList) {

                    if(u.getUsername().equals(user.getUsername())) {
                        return false;
                    }
                }
                added = userList.add(u);
            }
            if(added && clientList.size() > 0) {
                    synchronized(clientList) {
                        for(ChatRoomClientInterface client : clientList.values())
                        {
                            client.newLoginNotification(u.getUsername());
                        }
                    }
                }
        }    
        return added;
    }
    /**
     * A method to log a user in and add them to the servers userlist
     * and notify all other logged in users of this event
     * should be called before register for callback
     * takes the username and password of a user
     * checks those details against the database
     * if that user exists it is instantiated to dbUser
     * if the user is not contained in the list they are added
     * if they are added a notification gets sent to all clients to notify them
     * @param u The user to attempt log in with
     * @return boolean indicating success of login
     * @throws RemoteException 
     */
    @Override
    public boolean login(User u) throws RemoteException {
        
        boolean added = false;
        UserDao uDao = new UserDao();
        if(u != null) {
            User dbUser = uDao.logIn(u.getUsername(), u.getPassword());
            if(dbUser != null) {
                synchronized(userList) {
                    if(!userList.contains(dbUser)) {
                        added = userList.add(dbUser);
                    }
                }
                if(added && clientList.size() > 0) {
                    synchronized(clientList)
                    {
                        for(ChatRoomClientInterface client : clientList.values())
                        {
                            client.newLoginNotification(dbUser.getUsername());
                        }
                    }
                }
            }
        }
        return added;
    }

    /**
     * sets up a new arrayList of unseen PrivateMessages
     * for this user,registers a user for callbacks from the server
     * if they are registered and have unread messages these are then sent to them via callback
     * if the messages were sent they are removed from server memory
     * @param username name of the logged in user
     * @param client the clientInterface belonging to the user
     * @return boolean indicating whether the user was successfully registered
     * @throws RemoteException 
     */
    @Override
    public boolean registerForCallback(String username, ChatRoomClientInterface client) throws RemoteException {
        ArrayList<PrivateMessage> newMsgs;
        synchronized(privateMessages) {
            newMsgs = privateMessages.stream()
                    .filter(pm -> pm.getReciever().equals(username))
                    .collect(Collectors.toCollection(ArrayList::new));
        }
        boolean added = false;
        boolean sent = false;
        synchronized(clientList) {
            if(client != null && !clientList.values().contains(client)) {
                clientList.put(username, client);
                added =  true;
            }
            if(added && newMsgs != null && newMsgs.size() > 0) {
                for(PrivateMessage pm : newMsgs) {
                    clientList.get(username).newPrivateMessageNotification(pm.getAuthor() + ": " + pm.getText());
                }
                sent = true;
            }
        }
        if(sent) {
            synchronized(privateMessages) {
                privateMessages.removeAll(newMsgs);
            }
        }
        return added;
    }

    /**
     * A method to remove a user client interface from the servers hashmap so that they are no longer
     * targeted for callbacks and cause remote exceptions, must be called before logout!
     * @param username the username String used as a key in the hashmap of user clients
     * @param client the actual client interface to confirm it exists in the hashmap values list
     * @return boolean indicating success of unregistering
     * @throws RemoteException 
     */
    @Override
    public boolean unregisterForCallback(String username, ChatRoomClientInterface client) throws RemoteException {
        synchronized(clientList) {
            if(client != null && username != null && clientList.values().contains(client)) {
                
                clientList.remove(username);
                return true;
            }
            return false;
        }
    }
    
    /**
     * A method to log the user out of the system by removing
     * the user from the servers userList arrayList
     * and removing the clientInterface from the servers clientList hashmap
     * the method will then run a call back to inform any logged in users of this event
     * @param u the user to be logged out from the system
     * @return boolean indicating logout success
     * @throws RemoteException
     */
    @Override
    public boolean logout(User u) throws RemoteException {
        
        boolean removed = false;
        if(u != null) {
            synchronized(userList) {
                if(userList.contains(u)) {
                    removed = userList.remove(u);
                }
            }
            if(removed && clientList.size() > 0) {
                    synchronized(clientList) {
                        for(ChatRoomClientInterface client : clientList.values()) {
                            client.newLogoutNotification(u.getUsername());
                        }
                    }
                }
        }
        return removed;
    }

}
