package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by dduggan.
 */

public class PeerContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Peer");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));


    public static final String NAME = "name";

    public static final String TIMESTAMP = "timestamp";

    public static final String ADDRESS = "address";

    public static final String PORT = "port";

    private static final String _ID = "_id";

    private static int nameColumn = -1;

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn = cursor.getColumnIndexOrThrow(NAME);
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues out, String name) {
        out.put(NAME, name);
    }

    private static int timestampColumn = -1;

    public static long getTimestamp(Cursor cursor) {
        if (timestampColumn < 0) {
            timestampColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getLong(timestampColumn);
    }

    public static void putTimestamp(ContentValues out, long timestamp) {
        out.put(TIMESTAMP, timestamp);
    }

    private static int addressColumn = -1;

    public static byte[] getAddress(Cursor cursor) {
        if (addressColumn < 0) {
            addressColumn = cursor.getColumnIndexOrThrow(ADDRESS);
        }
        return cursor.getBlob(addressColumn);
    }

    public static void putAddress(ContentValues out, byte[] address) {
        out.put(ADDRESS, address);
    }

    private static int portColumn = -1;

    public static int getPort(Cursor cursor) {
        if (portColumn < 0) {
            portColumn = cursor.getColumnIndexOrThrow(PORT);
        }
        return cursor.getInt(portColumn);
    }

    public static void putPort(ContentValues out, int port) {
        out.put(PORT, port);
    }

    private static int idColumn = -1;

    public static long getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(_ID);
        }
        return Long.parseLong(cursor.getString(idColumn));
    }
}
