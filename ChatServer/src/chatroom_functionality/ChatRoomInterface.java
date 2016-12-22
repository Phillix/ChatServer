package chatroom_functionality;

import business.Message;
import business.PrivateMessage;
import business.User;
import callback_support.ChatRoomClientInterface;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

//server
public interface ChatRoomInterface extends Remote {
    
    public ArrayList<String> getLoggedUsers(String username) throws RemoteException;
    public ArrayList<PrivateMessage> getPrivateMessageHistory(String username, String friendName) throws RemoteException;
    
    public boolean addMessage(Message m) throws RemoteException;
    public boolean addPrivateMessage(PrivateMessage pm) throws RemoteException;
    
    public boolean register(User u) throws RemoteException;
    public boolean login(User u) throws RemoteException;
    public boolean logout(User u) throws RemoteException;
    
    public boolean registerForCallback(String username, ChatRoomClientInterface client) throws RemoteException;
    public boolean unregisterForCallback(String username, ChatRoomClientInterface client) throws RemoteException;
    
}
