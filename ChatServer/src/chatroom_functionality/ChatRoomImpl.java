package chatroom_functionality;

import business.Message;
import business.User;
import callback_support.ChatRoomClientInterface;
import daos.UserDao;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoomInterface
{
    private final ArrayList<Message> chatRoomMessages = new ArrayList();
    private final ArrayList<User> userList  = new ArrayList();
    private final ArrayList<ChatRoomClientInterface> clientList = new ArrayList();
    
    public ChatRoomImpl() throws RemoteException
    {
        
    }

    @Override
    public boolean addMessage(Message m) throws RemoteException {
        boolean added = false;
        synchronized(chatRoomMessages) {
            added = chatRoomMessages.add(m);
        }
        if(added && clientList.size() > 0) {
            synchronized(clientList)
            {
                for(ChatRoomClientInterface client : clientList)
                {
                    client.newMessageNotification(m.getAuthor() + ": " + m.getText());
                }
            }
        }
        return added;
    }

    //may not need this method
    @Override
    public Message getMessage() throws RemoteException {
        Random rand = new Random();
        synchronized(chatRoomMessages) {
            int choice = rand.nextInt(chatRoomMessages.size());
            return chatRoomMessages.get(choice);
        }
    }
    
    @Override
    public boolean register(User u) throws RemoteException {
        UserDao uDao = new UserDao();
        
        if(u != null && uDao.checkUname(u.getUsername()) != 0) {
            uDao.register(u);
            synchronized(userList) {
                for(User user : userList) {

                    if(u.getUsername().equals(user.getUsername())) {
                        return false;
                    }
                }
                userList.add(u);
                return true;
            }
        }    
        return false;
    }
    /**
     * @author Phil
     * takes the username and password of a user
     * checks those details against the database
     * if that user exists it is instantiated to u
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
            u = uDao.logIn(u.getUsername(), u.getPassword());
            if(u != null) {
                synchronized(userList) {
                    if(!userList.contains(u)) {
                        added = userList.add(u);
                    }
                }
                if(added && clientList.size() > 0) {
                    synchronized(clientList)
                    {
                        for(ChatRoomClientInterface client : clientList)
                        {
                            client.newLoginNotification(u.getUsername() + " has logged in!");
                        }
                    }
                }
            }
        }
        return added;
    }

    @Override
    public boolean registerForCallback(ChatRoomClientInterface client) throws RemoteException {
        synchronized(clientList) {
            if(client != null && !clientList.contains(client)) {
                clientList.add(client);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean unregisterForCallback(ChatRoomClientInterface client) throws RemoteException {
        synchronized(clientList) {
            if(client != null && clientList.contains(client))
            {
                clientList.remove(client);
                return true;
            }
            return false;
        }
    }
    
//    @Override
//    public boolean populateUserList() {
//    
//        UserDao uDao = new UserDao();
//        ArrayList<User> dbUserList = uDao.getUsers();
//        if(dbUserList != null && dbUserList.size() > 0) {
//            synchronized(userList) {
//                userList.addAll(dbUserList);
//                return true;
//            }
//        }
//        return false;
//    }

}
