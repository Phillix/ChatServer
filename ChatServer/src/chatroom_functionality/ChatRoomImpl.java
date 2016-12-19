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
        synchronized(chatRoomMessages)
        {
            added = chatRoomMessages.add(m);
        }
        if(added)
        {
            synchronized(clientList)
            {
                for(ChatRoomClientInterface client : clientList)
                {
                    client.newQuoteNotification(m.getAuthor() + ": " + m.getText());
                }
            }
        }
        return added;
    }

    //may not need this method
    @Override
    public Message getMessage() throws RemoteException {
        Random rand = new Random();
        synchronized(chatRoomMessages)
        {
            int choice = rand.nextInt(chatRoomMessages.size());
            return chatRoomMessages.get(choice);
        }
    }
    
    @Override
    public boolean register(User u) throws RemoteException
    {
        UserDao uDao = new UserDao();
        synchronized(userList)
        {
            if(u != null)
            {
                for(User user : userList)
                {
                    if(u.getUsername().equals(user.getUsername()))
                    {
                        return false;
                    }
                }
            }
            uDao.register(u);
            userList.add(u);
            return true;
        }
    }
    
    @Override
    public boolean login(User u) throws RemoteException
    {
        synchronized(userList)
        {
            if(u != null && userList.contains(u))
            {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean registerForCallback(ChatRoomClientInterface client) throws RemoteException {
        synchronized(clientList)
        {
            if(client != null && !clientList.contains(client))
            {
                clientList.add(client);
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean unregisterForCallback(ChatRoomClientInterface client) throws RemoteException {
        synchronized(clientList)
        {
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
