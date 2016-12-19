package launch;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import chatroom_functionality.ChatRoomImpl;


public class ChatRoomServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ChatRoomImpl chatRoomObject = new ChatRoomImpl();
            int portNum = 7777;
            startRegistry(portNum);
            
            String registryPath = "rmi://localhost:" + portNum;
            String objectLabel = "/chatroomService";
            
            Naming.rebind(registryPath+objectLabel, chatRoomObject);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ChatRoomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
        
    // This method starts a RMI registry on the local host, if it
    // does not already exist at the specified port number.
    private static void startRegistry(int RMIPortNum) throws RemoteException 
    {
        try {
            // Try to get the registry at a specific port number
            // If there is no registry started on that port, an exception will be thrown
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            
            registry.list();
        } catch (RemoteException ex) {
            // No valid registry at that port.
            System.out.println("RMI registry cannot be located at port " + RMIPortNum);
            
            // Create a registry on the given port number
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("RMI registry created at port " + RMIPortNum);
        }
    } // end startRegistry
}
