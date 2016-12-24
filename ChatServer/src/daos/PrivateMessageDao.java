/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import business.PrivateMessage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author Phillix
 * Dao class to handle PrivateMessages to and from the database
 */
public class PrivateMessageDao extends Dao implements PrivateMessageDaoInterface {

    private final String TABLE_NAME  = "messages";
    private final String ID          = "id";
    private final String SENDER      = "author";
    private final String RECEIVER    = "reciever";
    private final String DATE        = "timestamp";
    private final String CONTENT     = "text";
    
    /**
     * Default Constructor for MessageDao
     */
    public PrivateMessageDao() {
        super();
    }
    

    /**
     * This method is used for creating/storing a message
     * @param pm the message to write to database
     * @return int value indicating success/failures
     */
    public int addMsg(PrivateMessage pm) {

        Connection con       = null;
        PreparedStatement ps = null;

        try {
            con = getConnection();
            String query = "INSERT INTO " +
                           TABLE_NAME + " (" +
                           SENDER     + ", " +
                           RECEIVER   + ", " +
                           CONTENT    + ") VALUES (?,?,?)";
                           
            ps = con.prepareStatement(query);

            ps.setString(1, pm.getAuthor());
            ps.setString(2, pm.getReciever());
            ps.setString(3, pm.getText());

            ps.executeUpdate();
            return SUCCESS;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return SQLEX;
        }
        catch(ClassNotFoundException cnf) {
            cnf.printStackTrace();
            return CLASSNOTFOUND;
        }
        finally {
            try {
                if(ps != null)
                    ps.close();
                if(con != null)
                    freeConnection(con);
            }
            catch(SQLException e) {
                e.printStackTrace();
                return CONNCLOSEFAIL;
            }
        }
    }
    
    /**
     * A method to get back the entire conversation between two users
     * @param username the requester of the conversation
     * @param friendName the secondary participant in the conversation
     * @return an arrayList of their PrivateMessages
     */
    public ArrayList<PrivateMessage> getConversation(String username, String friendName) {

        Connection con       = null;
        PreparedStatement ps = null;
        ResultSet rs         = null;
        PrivateMessage pm    = null;
        ArrayList<PrivateMessage> conversation;

        try {
            con = getConnection();
            ps  = con.prepareStatement("SELECT * FROM " + TABLE_NAME +
                                       " WHERE " + SENDER + " = ? AND " + RECEIVER + " = ? " +
                                       " OR " + SENDER + " = ? AND " + RECEIVER + " = ? ");
            
            ps.setString(1, friendName);
            ps.setString(2, username);
            ps.setString(3, username);
            ps.setString(4, friendName);
            rs     = ps.executeQuery();
            conversation = new ArrayList<>();

            while(rs.next()) {
                pm = new PrivateMessage();

                pm.setId(rs.getInt(ID));
                pm.setAuthor(rs.getString(SENDER));
                pm.setReciever(rs.getString(RECEIVER));
                pm.setTimestamp(rs.getTimestamp(DATE).getTime());
                pm.setText(rs.getString(CONTENT));

                conversation.add(pm);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            try {
                if(ps != null)
                    ps.close();
                if(con != null)
                    freeConnection(con);
                if (rs != null)
                    rs.close();
            }
            catch(SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        return conversation;
    }
    
   
}