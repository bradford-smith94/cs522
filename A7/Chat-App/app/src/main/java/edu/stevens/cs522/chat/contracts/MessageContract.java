package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String LONGITUDE = "longitude";

    public static final String LATITUDE = "latitude";

    public static final String SENDER = "sender";

    public static final String SENDER_ID = "sender_id";

    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    private static int timestampColumn = -1;

    public static long getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timestampColumn);
    }

    public static void putTimestamp(ContentValues out, Long timestamp) {
        out.put(TIMESTAMP, timestamp);
    }

    private static int senderColumn = -1;

    public static String getSender(Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    private static int idColumn = -1;

    public static long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return Long.parseLong(cursor.getString(idColumn));
    }

    private static int longitudeColumn = -1;

    public static Double getLongitude(Cursor cursor) {
        if (longitudeColumn < 0) {
            longitudeColumn = cursor.getColumnIndexOrThrow(LONGITUDE);
        }
        return Double.parseDouble(cursor.getString(longitudeColumn));
    }

    public static void putLongitude(ContentValues out, Double longitude) {
        out.put(LONGITUDE, longitude);
    }

    private static int latitudeColumn = -1;

    public static Double getLatitude(Cursor cursor) {
        if (latitudeColumn < 0) {
            latitudeColumn = cursor.getColumnIndexOrThrow(LATITUDE);
        }
        return Double.parseDouble(cursor.getString(latitudeColumn));
    }

    public static void putLatitude(ContentValues out, Double latitude) {
        out.put(LATITUDE, latitude);
    }

    private static int senderIdColumn = -1;

    public static long getSenderId(Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return Long.parseLong(cursor.getString(senderIdColumn));
    }

    public static void putSenderId(ContentValues out, long senderId) {
        out.put(SENDER_ID, senderId);
    }
}
