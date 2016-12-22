package business;

import java.io.Serializable;
import java.util.Objects;


public class User implements Serializable
{
 
    private String username;
    private String password;

    public User() {
        
    }
    
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

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
