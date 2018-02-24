package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;

import static android.provider.BaseColumns._ID;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";


    private static final String MESSAGES_PEER_INDEX = "MessagesPeerIndex";

    private static final String PEER_NAME_INDEX = "PeerNameIndex";

    private static final String FK_ON = "PRAGMA foreign_keys=ON;";

    private static final String[] MESSAGE_PROJECTION = {_ID, MessageContract.MESSAGE_TEXT,
            MessageContract.TIMESTAMP, MessageContract.SENDER, MessageContract.SENDER_ID};

    private static final String[] PEER_PROJECTION = {_ID, PeerContract.NAME,
            PeerContract.TIMESTAMP, PeerContract.ADDRESS, PeerContract.PORT};

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String PEERS_CREATE = "CREATE TABLE " + PEERS_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + PeerContract.NAME + " TEXT NOT NULL,"
                + PeerContract.TIMESTAMP + " LONG NOT NULL,"
                + PeerContract.ADDRESS + " TEXT NOT NULL,"
                + PeerContract.PORT + " INTEGER NOT NULL );";

        private static final String MESSAGES_CREATE = "CREATE TABLE " + MESSAGES_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + MessageContract.MESSAGE_TEXT + " TEXT NOT NULL,"
                + MessageContract.TIMESTAMP + " LONG NOT NULL,"
                + MessageContract.SENDER + " TEXT NOT NULL,"
                + MessageContract.SENDER_ID + " INTEGER NOT NULL,"
                + "FOREIGN KEY (" + MessageContract.SENDER_ID + ") REFERENCES " + PEERS_TABLE + "(" + _ID + ") ON DELETE CASCADE );";

        private static final String MESSAGES_P_INDEX_CREATE = "CREATE INDEX "
                + MESSAGES_PEER_INDEX + " ON " + MESSAGES_TABLE + "(" + MessageContract.SENDER_ID + ");";

        private static final String PEER_NAME_INDEX_CREATE = "CREATE INDEX "
                + PEER_NAME_INDEX + " ON " + PEERS_TABLE + "(" + PeerContract.NAME + ");";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(PEERS_CREATE);
            db.execSQL(PEER_NAME_INDEX_CREATE);
            db.execSQL(MESSAGES_CREATE);
            db.execSQL(MESSAGES_P_INDEX_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(FK_ON);
            db.execSQL("DROP TABLE IF EXISTS " + PEERS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGES_TABLE);

            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return MessageContract.CONTENT_PATH;
            case MESSAGES_SINGLE_ROW:
                return MessageContract.CONTENT_PATH_ITEM;
            case PEERS_ALL_ROWS:
                return PeerContract.CONTENT_PATH;
            case PEERS_SINGLE_ROW:
                return PeerContract.CONTENT_PATH_ITEM;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
        long row;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                row = db.insert(MESSAGES_TABLE, null, values);
                if (row > 0) {
                    Uri instanceURI = MessageContract.CONTENT_URI(row);

                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(MessageContract.CONTENT_URI, null);

                    return instanceURI;
                }
            case PEERS_ALL_ROWS:
                row = db.insert(PEERS_TABLE, null, values);
                if (row > 0) {
                    Uri instanceURI = PeerContract.CONTENT_URI(row);

                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(PeerContract.CONTENT_URI, null);

                    return instanceURI;
                }
            case PEERS_SINGLE_ROW:
            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.execSQL(FK_ON);
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                cursor = db.query(MESSAGES_TABLE, MESSAGE_PROJECTION, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), MessageContract.CONTENT_URI);
                return cursor;
            case PEERS_ALL_ROWS:
                cursor = db.query(PEERS_TABLE, PEER_PROJECTION, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), PeerContract.CONTENT_URI);
                return cursor;
            case MESSAGES_SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(MessageContract.getId(uri))};
                cursor = db.query(MESSAGES_TABLE, MESSAGE_PROJECTION, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), MessageContract.CONTENT_URI);
                return cursor;
            case PEERS_SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(PeerContract.getId(uri))};
                cursor = db.query(PEERS_TABLE, PEER_PROJECTION, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), PeerContract.CONTENT_URI);
                return cursor;
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
        switch (uriMatcher.match(uri)) {
            case PEERS_SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(PeerContract.getId(uri))};
                return db.update(PEERS_TABLE, values, selection, selectionArgs);
            case PEERS_ALL_ROWS:
            case MESSAGES_ALL_ROWS:
            case MESSAGES_SINGLE_ROW:
            default:
                throw new IllegalStateException("update: bad case");
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
        switch (uriMatcher.match(uri)) {
            case PEERS_ALL_ROWS:
                return db.delete(PEERS_TABLE, selection, selectionArgs);
            case PEERS_SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(PeerContract.getId(uri))};
                return db.delete(PEERS_TABLE, selection, selectionArgs);
            case MESSAGES_ALL_ROWS:
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            case MESSAGES_SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(MessageContract.getId(uri))};
                return db.delete(MESSAGES_TABLE, selection, selectionArgs);
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }

}
