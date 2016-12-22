package callback_support;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRoomClientInterface extends Remote
{
    //server
    public void newMessageNotification(String newMessage) throws RemoteException;
    public void newPrivateMessageNotification(String newMessage) throws RemoteException;
    public void newLoginNotification(String newMessage) throws RemoteException;
    public void newLogoutNotification(String newMessage) throws RemoteException;

}
