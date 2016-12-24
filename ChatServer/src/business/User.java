package business;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author Dylan
 */

/**
 * 
 * DTO to allow users to register and log in from the database
 * Implements Serializable so that it can be sent over a network
 */

//server
public class User implements Serializable
{
 
    private String username;
    private String password;

    public User() {
        
    }
    
    /**
    *
    * @param username the unique identifier for the user object
    * @param password the password for the user
    */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
    *
    * @return String the unique identifier for the user object
    */
    public String getUsername() {
        return username;
    }

    /**
    *
    * @param username the unique identifier for the user object
    */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
    *
    * @return String the password of the user object
    */
    public String getPassword() {
        return password;
    }

    /**
    *
    * @param password the password for the user object
    */
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.username);
        hash = 29 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        User u2 = (User) o;
        if(username.equals(u2.username)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" + "username=" + username + ", password=" + password + '}';
    }


    
    
}
