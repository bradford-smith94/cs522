package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class ChatMessage {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
    }

    public void writeToProvider(ContentValues values) {
        // TODO
    }

}
