package daos;

import business.PrivateMessage;
import java.util.ArrayList;

/**
 *
 * @author Phillix
 */
public interface PrivateMessageDaoInterface {
    
    public int addMsg(PrivateMessage pm);
    public ArrayList<PrivateMessage> getConversation(String username, String friendName);
}
