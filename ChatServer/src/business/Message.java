package business;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 * Class to facilitate sending messages to be pushed out to all clients via server
 * Implements Serializable so that it can be sent over a network
 */

//server
public class Message implements Serializable
{
    private String text;
    private String author;

     /**
     *
     * @param text the content of the message
     * @param author the author of the message
     */
    public Message(String text, String author) {
        this.text = text;
        this.author = author;
    }
    
    /**
    *
    * @return String containing the text (contents) of the message
    */
    public String getText() {
        return text;
    }
    
    /**
    *
    * @param text the content of the message
    */
    public void setText(String text) {
        this.text = text;
    }

    /**
    *
    * @return String containing the author of the message
    */
    public String getAuthor() {
        return author;
    }
    
    /**
    *
    * @param author the creator of the message
    */
    public void setAuthor(String author) {
        this.author = author;
    }
    
    @Override
    public String toString() {
        return "Message{" + "text=" + text + ", author=" + author + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.text);
        hash = 37 * hash + Objects.hashCode(this.author);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Message other = (Message) obj;
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.author, other.author)) {
            return false;
        }
        return true;
    }
    
    
}
