package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import java.util.UUID;

import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

import static android.app.Activity.RESULT_CANCELED;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;

    // Provided by server when we register
    private long senderId;

    // Installation senderId created when the app is installed (and provided with every request for sanity check)
    private UUID clientID;

    private ResultReceiverWrapper receiver;

    public ChatHelper(Context context, ResultReceiverWrapper receiver) {
        this.context = context;
        this.clientID = Settings.getClientId(context);
        this.receiver = receiver;
    }

    // provide a result receiver that will update sender senderId (and add us to peer database)
    // and display a toast message upon completion
    public void register(String chatName) {
        this.senderId = Settings.getSenderId(context);
        if (senderId > 0) {
            // error we are already registered
            if (receiver != null) {
                receiver.send(RESULT_CANCELED, null);
            }
            return;
        }
        if (chatName != null && !chatName.isEmpty()) {
            RegisterRequest request = new RegisterRequest(chatName, clientID);
            this.senderId = Settings.getSenderId(context);
            if (senderId > 0) {
                // error we are already registered
                if (receiver != null) {
                    receiver.send(RESULT_CANCELED, null);
                }
                return;
            }
            Settings.saveChatName(context, chatName);
            addRequest(request, receiver);
        }
    }

    // provide a result receiver that will display a toast message upon completion
    public void postMessage(String chatRoom, String message) {
        this.senderId = Settings.getSenderId(context);
        if (senderId <= 0) {
            // error we are not registered
            if (receiver != null) {
                receiver.send(RESULT_CANCELED, null);
            }
            return;
        }
        if (message != null && !message.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            PostMessageRequest request = new PostMessageRequest(senderId, clientID, chatRoom, message);
            addRequest(request, receiver);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

    private void addRequest(Request request) {
        addRequest(request, null);
    }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);
        if (receiver != null) {
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

    public static Intent createIntent(Context context, Request request) {
        return createIntent(context, request, null);
    }

}
