package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;

import static android.provider.BaseColumns._ID;
import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH;
import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH_ITEM;

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

    private static final String BOOKS_JOIN_AUTHORS = BOOKS_TABLE + " JOIN "
            + AUTHORS_TABLE + " ON " + BOOKS_TABLE + "." + _ID + " = "
            + AUTHORS_TABLE + "." + BOOK_FK;

    private static final String GET_AUTHORS = "GROUP_CONCAT("
            + AuthorContract.NAME + ", '|') AS " + BookContract.AUTHORS;

    private static final String GROUPBY = BOOKS_TABLE + "." + _ID + ", "
            + BookContract.TITLE + ", " + BookContract.PRICE + ", " + BookContract.ISBN;

    private static final String[] BOOK_PROJECTION = {BOOKS_TABLE + "." + _ID,
            BookContract.TITLE, BookContract.PRICE, BookContract.ISBN, GET_AUTHORS};

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
        // handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return BookContract.CONTENT_PATH;
            case SINGLE_ROW:
                return BookContract.CONTENT_PATH_ITEM;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // handle requests to insert a new row.
                // pull authors out before inserting book
                String[] authors = BookContract.readStringArray(values.getAsString(BookContract.AUTHORS));
                values.remove(BookContract.AUTHORS);
                long row = db.insert(BOOKS_TABLE, null, values);
                if (row > 0) {
                    // insert success, now insert authors
                    for (int i = 0; i < authors.length; i++) {
                        ContentValues authorValues = new ContentValues();
                        AuthorContract.putName(authorValues, authors[i]);
                        authorValues.put(BOOK_FK, row);
                        db.insert(AUTHORS_TABLE, null, authorValues);
                    }

                    // make URI
                    Uri instanceUri = BookContract.CONTENT_URI(row);

                    // Make sure to notify any observers
                    ContentResolver cr = getContext().getContentResolver();
                    cr.notifyChange(instanceUri, null);

                    return instanceUri;
                }
                throw new SQLException("insert: Insertion failed");
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
        db.execSQL(FK_ON);
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // handle query of all books.
                return db.query(BOOKS_JOIN_AUTHORS, BOOK_PROJECTION, selection, selectionArgs, GROUPBY, null, sortOrder);
            case SINGLE_ROW:
                // handle query of a specific book.
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(BookContract.getId(uri))};
                return db.query(BOOKS_JOIN_AUTHORS, BOOK_PROJECTION, selection, selectionArgs, GROUPBY, null, null);
            default:
                throw new IllegalStateException("query: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new IllegalStateException("Update of books not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.execSQL(FK_ON);
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return db.delete(BOOKS_TABLE, selection, selectionArgs);
            case SINGLE_ROW:
                selection = _ID + " = ?";
                selectionArgs = new String[]{String.valueOf(BookContract.getId(uri))};
                return db.delete(BOOKS_TABLE, selection, selectionArgs);
            default:
                throw new IllegalStateException("delete: bad case");
        }
    }

}
