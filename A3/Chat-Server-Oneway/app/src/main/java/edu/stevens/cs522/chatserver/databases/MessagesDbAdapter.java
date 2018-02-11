package edu.stevens.cs522.chatserver.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class MessagesDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "view_peers";

    private static final String _ID = "_id";

    private static final String PEER_FK = "peer_fk";

    private static final String MESSAGES_PEER_INDEX = "MessagesPeerIndex";

    private static final String PEER_NAME_INDEX = "PeerNameIndex";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "CREATE TABLE " + PEER_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + PeerContract.NAME + " TEXT NOT NULL,"
                + PeerContract.TIMESTAMP + " LONG NOT NULL,"
                + PeerContract.ADDRESS + " TEXT NOT NULL,"
                + PeerContract.PORT + " INTEGER NOT NULL );"
                + "CREATE TABLE " + MESSAGE_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + MessageContract.MESSAGE_TEXT + " TEXT NOT NULL,"
                + MessageContract.TIMESTAMP + " LONG NOT NULL,"
                + "FOREIGN KEY " + PEER_FK + " REFERENCES " + PEER_TABLE + "(" + _ID + ") ON DELETE CASCADE );"
                + "CREATE INDEX " + MESSAGES_PEER_INDEX + " ON " + MESSAGE_TABLE + "(" + PEER_FK + ");"
                + "CREATE INDEX " + PEER_NAME_INDEX + " ON " + PEER_TABLE + "(" + PeerContract.NAME + ");";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        // TODO
    }

    public Cursor fetchAllMessages() {
        // TODO
        return null;
    }

    public Cursor fetchAllPeers() {
        // TODO
        return null;
    }

    public Peer fetchPeer(long peerId) {
        // TODO
        return null;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        return null;
    }

    public void persist(Message message) throws SQLException {
        // TODO
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        // TODO
        throw new SQLException("Failed to add peer "+peer.name);
    }

    public void close() {
        // TODO
    }
}
