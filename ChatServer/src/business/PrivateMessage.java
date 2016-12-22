/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package business;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Phillix
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
    public PrivateMessage(String author, String text, String reciever) {
        this.author = author;
        this.text = text;
        this.reciever = reciever;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public long getTimestamp() {
        return timestamp;
    }

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
