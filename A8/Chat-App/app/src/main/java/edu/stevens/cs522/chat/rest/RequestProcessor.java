package edu.stevens.cs522.chat.rest;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.StringUtils;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        // Used for SYNC
        this.requestManager = new RequestManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // update the sender senderId in settings, updated peer record PK
            Settings.saveSenderId(context, ((RegisterResponse) response).senderId);
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        if (!Settings.SYNC) {
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

            }
            return response;
        } else {
            /*
             * We will just insert the message into the database, and rely on background
             * sync to upload.
             */
            ChatMessage chatMessage = new ChatMessage();
            // fill the fields with values from the request message
            chatMessage.messageText = request.message;
            chatMessage.senderId = request.senderId;
            chatMessage.timestamp = DateUtils.now();
            chatMessage.sender = Settings.getChatName(context);
            chatMessage.longitude = Double.parseDouble(context.getString(R.string.longitude));
            chatMessage.latitude = Double.parseDouble(context.getString(R.string.latitude));

            requestManager.persist(chatMessage);
            return request.getDummyResponse();
        }
    }

    /**
     * For SYNC: perform a sync using a request manager
     * @param request
     * @return
     */
    public Response perform(SynchronizeRequest request) {
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        try {
            /*
             * This is the callback from streaming new local messages to the server.
             */
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODO stream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */

                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            /*
             * Connect to the server and upload messages not yet shared.
             */
            response = restMethod.perform(request, out);

            /*
             * Stream downloaded peer and message information, and update the database.
             * The connection is closed in the finally block below.
             */
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODO parse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.


            /*
             *
             */
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(0, ErrorResponse.Status.SERVER_ERROR, e.getMessage());

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }


}
