package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
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

    private static final String FK_ON = "PRAGMA foreign_keys=ON;";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String PEERS_CREATE = "CREATE TABLE " + PEER_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + PeerContract.NAME + " TEXT NOT NULL,"
                + PeerContract.TIMESTAMP + " LONG NOT NULL,"
                + PeerContract.ADDRESS + " TEXT NOT NULL,"
                + PeerContract.PORT + " INTEGER NOT NULL );";

        private static final String MESSAGES_CREATE = "CREATE TABLE " + MESSAGE_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + MessageContract.MESSAGE_TEXT + " TEXT NOT NULL,"
                + MessageContract.TIMESTAMP + " LONG NOT NULL,"
                + MessageContract.SENDER + " TEXT NOT NULL,"
                + PEER_FK + " INTEGER NOT NULL,"
                + "FOREIGN KEY (" + PEER_FK + ") REFERENCES " + PEER_TABLE + "(" + _ID + ") ON DELETE CASCADE );";

        private static final String MESSAGES_P_INDEX_CREATE = "CREATE INDEX "
                + MESSAGES_PEER_INDEX + " ON " + MESSAGE_TABLE + "(" + PEER_FK + ");";

        private static final String PEER_NAME_INDEX_CREATE = "CREATE INDEX "
                + PEER_NAME_INDEX + " ON " + PEER_TABLE + "(" + PeerContract.NAME + ");";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PEERS_CREATE);
            db.execSQL(MESSAGES_CREATE);
            db.execSQL(MESSAGES_P_INDEX_CREATE);
            db.execSQL(PEER_NAME_INDEX_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(FK_ON);
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);

            onCreate(db);
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
    }

    public Cursor fetchAllMessages() {
        String[] projection = {_ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER};
        db.execSQL(FK_ON);
        return db.query(MESSAGE_TABLE, projection, null, null, null, null, null);
    }

    public Cursor fetchAllPeers() {
        String[] projection = {_ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS, PeerContract.PORT};
        db.execSQL(FK_ON);
        return db.query(PEER_TABLE, projection, null, null, null, null, null);
    }

    public Peer fetchPeer(long peerId) {
        String[] projection = {_ID, PeerContract.NAME, PeerContract.TIMESTAMP, PeerContract.ADDRESS, PeerContract.PORT};
        String selection = _ID + " = ?";
        String[] selectionArgs = {Long.toString(peerId)};
        db.execSQL(FK_ON);
        Cursor result = db.query(PEER_TABLE, projection, selection, selectionArgs, null, null, null);
        return new Peer(result);
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        String[] projection = {_ID, MessageContract.MESSAGE_TEXT, MessageContract.TIMESTAMP, MessageContract.SENDER};
        String selection = MessageContract.SENDER + " = ?";
        String[] selectionArgs = {peer.name};
        db.execSQL(FK_ON);
        return db.query(MESSAGE_TABLE, projection, selection, selectionArgs, null, null, null);
    }

    public void persist(Message message) throws SQLException {
        ContentValues values = new ContentValues();
        MessageContract.putMessageText(values, message.messageText);
        MessageContract.putTimestamp(values, message.timestamp.getTime());
        MessageContract.putSender(values, message.sender);

        db.insert(MESSAGE_TABLE, null, values);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        ContentValues values = new ContentValues();
        PeerContract.putName(values, peer.name);
        PeerContract.putTimestamp(values, peer.timestamp.getTime());
        PeerContract.putAddress(values, peer.address.getAddress());
        PeerContract.putPort(values, peer.port);

        return db.insert(PEER_TABLE, null, values);
    }

    public void close() {
        db.close();
    }
}
