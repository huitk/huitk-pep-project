package Service;

import DAO.AccountDAO;
import DAO.MessageDAO;
import Model.Account;
import Model.Message;

import java.util.*;
public class MessageService {
    private MessageDAO msgDAO;
    private AccountDAO accountDAO;

    public MessageService() {
        msgDAO = new MessageDAO();
        this.accountDAO = new AccountDAO();
    }

    public MessageService(MessageDAO msgDAO) {
        this.msgDAO = msgDAO;
    }

    //Add new msg if not null, length not more than 255, and the person who posted msg is exists
    public Message addNewMessage(Message msg) {
        if(msg == null || msg.getMessage_text() == null || msg.getMessage_text().isBlank()) {
            return null;
        }
        if(msg.getMessage_text().length() > 255) {
            return null;
        }
        //need to get id from database before checking if user posted exists
        Account account = accountDAO.getAccountById(msg.getPosted_by());
        if(account == null) {
            return null;
        }
        return msgDAO.insertMessage(msg);
    }

    public List<Message> getAllMessage() {
        return msgDAO.getAllMessage();
    }

    public Message getMessageById(int msgId) {
        return msgDAO.getMessageById(msgId);
    }

    public Message deleteMessageById(int msgId) {
        Message existing = msgDAO.getMessageById(msgId);
        if(existing != null) {
            msgDAO.deleteMessageById(msgId);
            return existing;
        }
        return null;
    }

    public Message updateMessageById(int msgId, Message msg) {
        Message existing = msgDAO.getMessageById(msgId);
        if(existing != null && msg.getMessage_text() !=null && msg.getMessage_text().length() <= 255 && !msg.getMessage_text().isBlank()) {
            //Update the existing message object
            existing.setMessage_text(msg.getMessage_text());
            //Update in database
            msgDAO.updateMessageById(existing);
            //return update message
            return existing;
        }
        return null;
    }

    public List<Message> getMessageByUserId(int accountId) {
        return msgDAO.getMessageByUserId(accountId);
    }
}
