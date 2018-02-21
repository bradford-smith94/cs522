package edu.stevens.cs522.chatserver.entities;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class Message {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public Message(Cursor cursor) {
        // TODO
    }

}
