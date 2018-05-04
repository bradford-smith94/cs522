package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.net.Uri;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // update the sender senderId in settings, updated peer record PK
            Settings.saveSenderId(context, ((RegisterResponse)response).senderId);
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        // insert the message into the local database
        ChatMessage message = new ChatMessage();
        // TODO message.chatRoom = request.chatRoom;
        message.messageText = request.message;
        message.senderId = request.senderId;
        message.timestamp = DateUtils.now();
        message.sender = Settings.getChatName(context);

        MessageManager manager = new MessageManager(context);
        manager.persistAsync(message);

        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            // TODO update the message in the database with the sequence number
            //message.sequenceNum = ((PostMessageResponse)response).messageId;
            //manager.updateAsync(message);
        }
        return response;
    }

}
