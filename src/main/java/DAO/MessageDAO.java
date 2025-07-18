package DAO;

import Model.Message;
import Util.ConnectionUtil;
import java.util.*;

import java.sql.*;

     /* Message
     * message_id integer primary key auto_increment,
     * posted_by integer,
     * message_text varchar(255),
     * time_posted_epoch long,
     * foreign key (posted_by) references Account(account_id)
     */
public class MessageDAO {

    public Message insertMessage(Message msg) {
        Connection cn = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";
            PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, msg.getPosted_by());
            ps.setString(2, msg.getMessage_text());
            ps.setLong(3, msg.getTime_posted_epoch());
            int rowsAffected = ps.executeUpdate();
            //reading auto-generated message_id
            if(rowsAffected > 0) {
              ResultSet rs = ps.getGeneratedKeys();
              if(rs.next()) {
                  msg.setMessage_id(rs.getInt(1));
              }
            }
            return msg;

        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public List<Message> getAllMessage() {
        Connection cn = ConnectionUtil.getConnection();
        List<Message> message = new ArrayList<>();
        try {
            String sql = "SELECT * FROM message";
            PreparedStatement ps = cn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Message msg = new Message();
                msg.setMessage_id(rs.getInt("message_id"));
                msg.setPosted_by(rs.getInt("posted_by"));
                msg.setMessage_text(rs.getString("message_text"));
                msg.setTime_posted_epoch(rs.getLong("time_posted_epoch"));
                message.add(msg);
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return message;
    }

    public Message getMessageById(int id) {
        Connection cn = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE message_id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                return new Message(
                        rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch")
                );
            }
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void deleteMessageById(int id) {
        Connection cn = ConnectionUtil.getConnection();
        try {
            String sql = "DELETE * FROM message WHERE message_id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateMessageById(Message msg) {
        Connection cn = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text =? WHERE message_id = ?";
            PreparedStatement ps = cn.prepareStatement(sql);
            ps.setString(1, msg.getMessage_text());
            ps.setInt(2, msg.getMessage_id());
            ps.executeUpdate();
        } catch(SQLException e) {
            System.out.println(e.getMessage());
        }
    }

     public List<Message> getMessageByUserId(int accountId) {
         List<Message> messages = new ArrayList<>();
         Connection cn = ConnectionUtil.getConnection();
         try {
             String sql = "SELECT * FROM message WHERE posted_by = ?";
             PreparedStatement ps = cn.prepareStatement(sql);
             ps.setInt(1, accountId);
             ResultSet rs = ps.executeQuery();
             while(rs.next()) {
                 Message msg = new Message(
                    rs.getInt("message_id"),
                    rs.getInt("posted_by"),
                    rs.getString("message_text"),
                    rs.getLong("time_posted_epoch")
                 );
                 messages.add(msg);
             }
         } catch(SQLException e) {
             System.out.print(e.getMessage());
         }
         return messages;
     }
}
