package chatroom_functionality;

import business.Message;
import business.User;
import callback_support.ChatRoomClientInterface;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoomInterface
{
    private final ArrayList<Message> quoteList = new ArrayList();
    private final ArrayList<User> userList  = new ArrayList();
    private final ArrayList<ChatRoomClientInterface> clientList = new ArrayList();
    
    public ChatRoomImpl() throws RemoteException
    {
        
    }

    @Override
    public boolean addQuote(Message q) throws RemoteException {
        boolean added = false;
        synchronized(quoteList)
        {
            added = quoteList.add(q);
        }
        if(added)
        {
            synchronized(clientList)
            {
                for(ChatRoomClientInterface client : clientList)
                {
                    client.newQuoteNotification("New quote added: " + q);
                }
            }
        }
        return added;
    }

    @Override
    public Message getQuote() throws RemoteException {
        Random rand = new Random();
        synchronized(quoteList)
        {
            int choice = rand.nextInt(quoteList.size());
            return quoteList.get(choice);
        }
    }
    
    @Override
    public boolean register(User u) throws RemoteException
    {
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

}
