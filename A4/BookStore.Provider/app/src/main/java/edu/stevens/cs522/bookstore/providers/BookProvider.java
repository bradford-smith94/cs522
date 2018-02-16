package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;

import static android.provider.BaseColumns._ID;

public class BookProvider extends ContentProvider {
    public BookProvider() {
    }

    private static final String AUTHORITY = BookContract.AUTHORITY;

    private static final String CONTENT_PATH = BookContract.CONTENT_PATH;

    private static final String CONTENT_PATH_ITEM = BookContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 1;

    private static final String BOOKS_TABLE = "books";

    private static final String AUTHORS_TABLE = "authors";

    private static final String BOOK_FK = "book_fk";

    private static final String FK_ON = "PRAGMA foreign_keys=ON;";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String TAG = DbHelper.class.getCanonicalName();

        private static final String BOOKS_CREATE = "CREATE TABLE " + BOOKS_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + BookContract.TITLE + " TEXT NOT NULL, "
                + BookContract.ISBN + " TEXT NOT NULL, "
                + BookContract.PRICE + " TEXT );";

        private static final String AUTHORS_CREATE = "CREATE TABLE " + AUTHORS_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + AuthorContract.NAME + " TEXT NOT NULL, "
                + BOOK_FK + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + BOOK_FK + ") REFERENCES " + BOOKS_TABLE + "(" + _ID + ") ON DELETE CASCADE );";

        private static final String AUTHOR_INDEX_CREATE = "CREATE INDEX AuthorBookIndex ON " + AUTHORS_TABLE + "(" + BOOK_FK + ");";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // initialize database tables
            db.execSQL(BOOKS_CREATE);
            db.execSQL(AUTHORS_CREATE);
            db.execSQL(AUTHOR_INDEX_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading db from version " + oldVersion + " to " + newVersion);

            db.execSQL(FK_ON);
            db.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AUTHORS_TABLE);

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
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new row.
                // Make sure to notify any observers
                throw new UnsupportedOperationException("Not yet implemented");
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODO: Implement this to handle query of all books.
                return db.query(BOOKS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            case SINGLE_ROW:
                // TODO: Implement this to handle query of a specific book.
                throw new UnsupportedOperationException("Not yet implemented");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new IllegalStateException("Update of books not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

}
