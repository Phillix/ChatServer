
package business;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Phillix
 */

/**
 * 
 * DTO to create Private Message objects that can be stored on, and received from the database for a specific user
 * Implements Serializable so that it can be sent over a network
 * Implements Comparable so that the messages can be sorted in order of date and time as their natural order
 */

//server
public class PrivateMessage implements Serializable, Comparable<PrivateMessage>{
    
    private int id;
    private String author;
    private String text;
    private String reciever;
    private long timestamp;

    public PrivateMessage() {
        
    }
    
    /**
    *
    * @param author the creator of the private message
    * @param text the content of the private message
    * @param reciever the recipient of the private message
    */
    public PrivateMessage(String author, String text, String reciever) {
        this.author = author;
        this.text = text;
        this.reciever = reciever;
    }

    /**
    *
    * @return int the unique id (primary key) of the private message
    */
    public int getId() {
        return id;
    }

    /**
    *
    * @param id the id of the private message
    */
    public void setId(int id) {
        this.id = id;
    }

    /**
    *
    * @return String the creator of the message
    */
    public String getAuthor() {
        return author;
    }

    /**
    *
    * @param author the creator of the private message
    */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
    *
    * @return String the contents of the private message
    */
    public String getText() {
        return text;
    }

    /**
    *
    * @param text the contents of the private message
    */
    public void setText(String text) {
        this.text = text;
    }

    /**
    *
    * @return String the recipient of the private message
    */
    public String getReciever() {
        return reciever;
    }

    /**
    *
    * @param reciever the recipient of the private message
    */
    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    /**
    *
    * @return long the time that the private message was sent
    */
    public long getTimestamp() {
        return timestamp;
    }

    /**
    *
    * @param timestamp the time that the private message was sent
    */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "PrivateMessage{" + "id=" + id + ", author=" + author + ", text=" + text + ", reciever=" + reciever + ", timestamp=" + timestamp + '}';
    }

    @Override
    public int compareTo(PrivateMessage pm) {
        
        if(timestamp - pm.timestamp > 0) {
            return 1;
        }
        else if(timestamp - pm.timestamp < 0) {
            return -1;
        }
        else {
            return 0;
        }
    }
    
    public static String messageFormat(PrivateMessage pm) {
        return "Sent: "+ new Date(pm.timestamp).toString() +
                "\nTo: " + pm.reciever + "\nFrom: " + pm.author + 
                "\n" + pm.text + "\n\n";
    }
    
}
