package edu.stevens.cs522.chat.entities;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class ChatMessage {

    public long id;

    public String messageText;

    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public String sender;

    public long senderId;

    public ChatMessage() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODO
    }

}
