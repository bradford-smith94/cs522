package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.bookstoredatabase.contracts.AuthorContract;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.entities.Book;

/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final String _ID = "_id";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = DatabaseHelper.class.getCanonicalName();

        private static final String DATABASE_CREATE = "create table " + BOOK_TABLE + " ("
                + _ID + " integer primary key, "
                + BookContract.TITLE + " text not null, "
                + BookContract.AUTHORS + " text not null, "
                + BookContract.ISBN + " text not null, "
                + BookContract.PRICE + " text )"
                + "create table " + AUTHOR_TABLE + " ("
                + _ID + " integer primary key, "
                + AuthorContract.FIRST_NAME + " text not null "
                + AuthorContract.MIDDLE_INITIAL + " text "
                + AuthorContract.LAST_NAME + " text not null )";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading db from version " + oldVersion + " to " + newVersion);

            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AUTHOR_TABLE);

            onCreate(db);
        }
    }


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public Cursor fetchAllBooks() {
        String[] projection = {_ID, BookContract.TITLE, BookContract.AUTHORS};
        return db.query(BOOK_TABLE, projection, null, null, null, null, null);
    }

    public Book fetchBook(long rowId) {
        // TODO
        return null;
    }

    public void persist(Book book) throws SQLException {
        // TODO
    }

    public boolean delete(Book book) {
        // TODO
        return false;
    }

    public boolean deleteAll() {
        // TODO
        return false;
    }

    public void close() {
        db.close();
    }

}
