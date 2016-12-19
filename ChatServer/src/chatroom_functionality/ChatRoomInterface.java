package chatroom_functionality;

import business.Message;
import business.User;
import callback_support.ChatRoomClientInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRoomInterface extends Remote
{
    public boolean addMessage(Message m) throws RemoteException;
    public Message getMessage() throws RemoteException;
    
    public boolean register(User u) throws RemoteException;
    public boolean login(User u) throws RemoteException;
    
    public boolean registerForCallback(ChatRoomClientInterface client) throws RemoteException;
    public boolean unregisterForCallback(ChatRoomClientInterface client) throws RemoteException;
    
    public boolean populateUserList();
}
