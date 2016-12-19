/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import business.User;

/**
 *
 * @author Phillix
 */
public interface UserDaoInterface {
    
    public int checkUname(String username);
    public int register(User u);
    public User logIn(String email, String password);
}
