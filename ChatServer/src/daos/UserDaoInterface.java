
package daos;

import business.User;
import java.util.ArrayList;

/**
 *
 * @author Phillix
 */
public interface UserDaoInterface {
    
    public int checkUname(String username);
    public int register(User u);
    public User logIn(String username, String password);
    public boolean isValidUsername(String username);
}
