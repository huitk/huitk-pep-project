package Controller;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;
import java.util.*;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    AccountService accountService;
    MessageService messageService;

    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postNewUserHandler);
        app.post("/login", this::postLoginHandler);
        app.post("/messages", this::postNewMsgHandler);
        app.get("/messages", this::getAllMessageHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteByMessageIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessageByUserHandler);

        return app;
    }

    /* Account
     *  account_id interger primary key auto_increment,
     *  username varchar(255) unique,
     *  password varchar(255)
     *
     * Message
     * message_id integer primary key auto_increment,
     * posted_by integer,
     * message_text varchar(255),
     * time_posted_epoch long,
     * foreign key (posted_by) references Account(account_id)
     *
     * 1. API should be able to process new user registration
     *  As a user, I should be able to create a new Account on the endpoint POST localhost:8080/register.
     *  The body will contain a representation of a JSON Account, but will not contain an account_id.
     *  The registration will be successful if and only if the username is not blank, the password
     *  is at least 4 characters long, and an Account with that username does not already exist.
     *  If all these conditions are met, the response body should contain a JSON of the Account,
     *  including its account_id. The response status should be 200 OK, which is the default.
     *  The new account should be persisted to the database. If the registration is not successful,
     *  the response status should be 400. (Client error)
     */
    private void postNewUserHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addAccount = accountService.addNewAccount(account);
        if(addAccount != null) {
            ctx.json(mapper.writeValueAsString(addAccount));
        } else {
            ctx.status(400);
        }
    }

    /* 2. API should be able to process user logins
     *  As a user, I should be able to verify my login on the endpoint POST localhost:8080/login.
     *  The request body will contain a JSON representation of an Account, not containing an account_id.
     *  In the future, this action may generate a Session token to allow the user to securely use the site.
     *  We will not worry about this for now. The login will be successful if and only if the username and
     *  password provided in the request body JSON match a real account existing on the database. If successful,
     *  the response body should contain a JSON of the account in the response body, including its account_id.
     *  The response status should be 200 OK, which is the default. If the login is not successful,
     *  the response status should be 401. (Unauthorized)
     */
    private void postLoginHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Account loginAttempt = mapper.readValue(ctx.body(), Account.class);
        Account actualAccount = accountService.verifyLogin(loginAttempt);
        if(actualAccount != null) {
            ctx.json(mapper.writeValueAsString(actualAccount));
        } else {
            ctx.status(401);
        }
    }

    /* 3. Our API should be able to process the creation of new messages.
     *  As a user, I should be able to submit a new post on the endpoint POST localhost:8080/messages.
     *  The request body will contain a JSON representation of a message, which should be persisted to
     *  the database, but will not contain a message_id. The creation of the message will be successful
     *  if and only if the message_text is not blank, is not over 255 characters, and posted_by refers
     *  to a real, existing user. If successful, the response body should contain a JSON of the message,
     *  including its message_id. The response status should be 200, which is the default. The new message
     *  should be persisted to the database. If the creation of the message is not successful, the response
     *  status should be 400. (Client error)
     */
    private void postNewMsgHandler(Context ctx) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message newMsg = messageService.addNewMessage(message);
        if(newMsg != null) {
            ctx.json(mapper.writeValueAsString(newMsg));
        } else {
            ctx.status(400);
        }
    }

    /* 4. Our API should be able to retrieve all messages.
     *  As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.
     *  The response body should contain a JSON representation of a list containing all messages retrieved
     *  from the database. It is expected for the list to simply be empty if there are no messages. The
     *  response status should always be 200, which is the default.
     */
    private void getAllMessageHandler(Context ctx) {
        List<Message> message = messageService.getAllMessage();
        ctx.json(message);
    }

    /* 5. Our API should be able to retrieve a message by its ID.
     *  As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.
     *  The response body should contain a JSON representation of the message identified by the message_id. It is
     *  expected for the response body to simply be empty if there is no such message. The response status should
     *  always be 200, which is the default.
     */
    private void getMessageByIdHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message msg = messageService.getMessageById(messageId);
            if(msg != null) {
                ctx.json(msg);
            } else {
                ctx.json("");
            }
        //if something wrong return empty. Response status should always be 200.
        } catch(Exception e) {
            ctx.json("");
        }
    }

    /* 6. Our API should be able to delete a message identified by a message ID.
     *  As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.
     *  The deletion of an existing message should remove an existing message from the database. If the message existed,
     *  the response body should contain the now-deleted message. The response status should be 200, which is the default.
     *  If the message did not exist, the response status should be 200, but the response body should be empty. This is
     *  because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond
     *  with the same type of response.
     */
    private void deleteByMessageIdHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message msg = messageService.deleteMessageById(messageId);
            if(msg != null) {
                ctx.json(msg);
            } else {
                ctx.json("");
            }
        } catch(Exception e) {
            ctx.json("");
        }
    }

    /* 7. Our API should be able to update a message text identified by a message ID.
     *    As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}.
     *    The request body should contain a new message_text values to replace the message identified by message_id. The
     *    request body can not be guaranteed to contain any other information. The update of a message should be successful
     *    if and only if the message id already exists and the new message_text is not blank and is not over 255 characters.
     *    If the update is successful, the response body should contain the full updated message (including message_id,
     *    posted_by, message_text, and time_posted_epoch), and the response status should be 200, which is the default.
     *    The message existing on the database should have the updated message_text. If the update of the message is not
     *    successful for any reason, the response status should be 400. (Client error)
     */
    private void updateMessageByIdHandler(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message update = ctx.bodyAsClass(Message.class);
            Message updated = messageService.updateMessageById(messageId, update);
            if(updated != null) {
                ctx.json(updated);
            } else {
                ctx.status(400);
            }
        } catch(Exception e) {
            ctx.status(400);
        }
    }

    /* 8. Our API should be able to retrieve all messages written by a particular user.
     *   As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.
     *  The response body should contain a JSON representation of a list containing all messages posted by a particular user,
     *  which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. The
     *  response status should always be 200, which is the default.
     */
    private void getMessageByUserHandler(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> message = messageService.getMessageByUserId(accountId);
            ctx.json(message);
        } catch(Exception e) {
            //return empty list on error
            ctx.json(new ArrayList<>());
        }
    }
}
