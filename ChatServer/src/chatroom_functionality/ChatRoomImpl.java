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
public class ChatRoomImpl extends UnicastRemoteObject implements ChatRoomInterface {
    private final ArrayList<PrivateMessage> privateMessages = new ArrayList();
    private final ArrayList<User> userList  = new ArrayList();
    private final HashMap<String,ChatRoomClientInterface> clientList = new HashMap();
    
    public ChatRoomImpl() throws RemoteException {
        
    }
    
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
                    if(username.equals(pm.getReciever())) {
                        clientList.get(username).newPrivateMessageNotification(pm.getAuthor() + ": " + pm.getText());
                        sent = true;
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
    
    @Override
    public boolean register(User u) throws RemoteException {
        UserDao uDao = new UserDao();
        boolean added = false;
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
     * @author Phil
     * takes the username and password of a user
     * checks those details against the database
     * if that user exists it is instantiated to u
     * if the user is not contained in the list they are added
     * if they are added a notification gets sent to all clients to notify them
     * @param dbUser The user to attempt log in with
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
