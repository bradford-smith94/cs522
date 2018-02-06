package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.stevens.cs522.bookstoredatabase.entities.Book;

/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = null; // TODO

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


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        // TODO
    }

    public Cursor fetchAllBooks() {
        // TODO
        return null;
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
        // TODO
    }

}
