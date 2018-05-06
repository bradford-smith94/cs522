package edu.stevens.cs522.chat.rest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.activities.ChatActivity;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    final static public String TAG = RequestProcessor.class.getCanonicalName();

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
        ContentResolver resolver = context.getContentResolver();
        ChatMessage message = new ChatMessage();

        message.messageText = request.message;
        message.senderId = request.senderId;
        message.timestamp = DateUtils.now();
        message.sender = Settings.getChatName(context);
        message.longitude = Double.parseDouble(context.getString(R.string.longitude));
        message.latitude = Double.parseDouble(context.getString(R.string.latitude));

        Peer peer = new Peer();
        peer.name = message.sender;
        peer.timestamp = message.timestamp;
        peer.longitude = message.longitude;
        peer.latitude = message.latitude;

        String selection = PeerContract.NAME + " = ?";
        String selectionArgs[] = {peer.name};
        Cursor c = resolver.query(PeerContract.CONTENT_URI, null, selection, selectionArgs, null);
        ContentValues values = new ContentValues();
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            Peer p = new Peer(c);
            peer.id = p.id;
            peer.writeToProvider(values);
            resolver.update(PeerContract.CONTENT_URI(peer.id), values, null, null);
        } else {
            peer.writeToProvider(values);
            Uri uri = resolver.insert(PeerContract.CONTENT_URI, values);
            peer.id = PeerContract.getId(uri);
        }
        values = new ContentValues();
        message.senderId = peer.id;
        message.writeToProvider(values);
        resolver.insert(MessageContract.CONTENT_URI, values);

        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            // TODO update the message in the database with the sequence number
            //message.sequenceNum = ((PostMessageResponse)response).messageId;
            //manager.updateAsync(message);
        }
        return response;
    }

}
