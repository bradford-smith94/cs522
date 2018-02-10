package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.ContentValues;
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

    //TODO put authors in AUTHOR_TABLE

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final String BOOK_FK = "book_fk";

    private static final String AUTHOR_BOOK_INDEX = "authorsBookIndex";

    private static final String _ID = "_id";

    private static final String FK_ON = "PRAGMA foreign_keys=ON;";

    private static final int DATABASE_VERSION = 1;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = DatabaseHelper.class.getCanonicalName();

        private static final String DATABASE_CREATE = "CREATE TABLE " + BOOK_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + BookContract.TITLE + " TEXT NOT NULL, "
                + BookContract.AUTHORS + " TEXT NOT NULL, "
                + BookContract.ISBN + " TEXT NOT NULL, "
                + BookContract.PRICE + " TEXT );"
                + "CREATE TABLE " + AUTHOR_TABLE + " ("
                + _ID + " INTEGER PRIMARY KEY, "
                + AuthorContract.FIRST_NAME + " TEXT NOT NULL, "
                + AuthorContract.MIDDLE_INITIAL + " TEXT, "
                + AuthorContract.LAST_NAME + " TEXT NOT NULL, "
                + BOOK_FK + " INTEGER NOT NULL,"
                + "FOREIGN KEY " + BOOK_FK + " REFERENCES " + BOOK_TABLE + "(" + _ID + ") ON DELETE CASCADE );"
                + "CREATE INDEX " + AUTHOR_BOOK_INDEX + " ON " + AUTHOR_TABLE + "(" + BOOK_FK + ");";

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

            db.execSQL(FK_ON);
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
        db.execSQL(FK_ON);
    }

    public Cursor fetchAllBooks() {
        String[] projection = {_ID, BookContract.TITLE, BookContract.AUTHORS,
                BookContract.ISBN, BookContract.PRICE};
        db.execSQL(FK_ON);
        return db.query(BOOK_TABLE, projection, null, null, null, null, null);
    }

    public Book fetchBook(long rowId) {
        String[] projection = {_ID, BookContract.TITLE, BookContract.AUTHORS,
                BookContract.ISBN, BookContract.PRICE};
        String selection = _ID + " = ? ";
        String[] selectionArgs = { Long.toString(rowId) };
        db.execSQL(FK_ON);
        Cursor result = db.query(BOOK_TABLE, projection, selection, selectionArgs, null, null, null);
        return new Book(result);
    }

    public void persist(Book book) throws SQLException {
        ContentValues values = new ContentValues();
        BookContract.putTitle(values, book.title);
        BookContract.putISBN(values, book.isbn);
        BookContract.putPrice(values, book.price);
        String[] authorStrings = new String[book.authors.length];
        for (int i = 0; i < book.authors.length; i++) {
            authorStrings[i] = book.authors[i].toString();
        }
        BookContract.putAuthors(values, authorStrings);

        db.insert(BOOK_TABLE, null, values);
    }

    public boolean delete(Book book) {
        //TODO more uniquely get book
        String selection = BookContract.TITLE + " = ?";
        String[] selectionArgs = { book.title };
        db.execSQL(FK_ON);
        db.delete(BOOK_TABLE, selection, selectionArgs);
        return false;
    }

    public boolean deleteAll() {
        db.execSQL(FK_ON);
        db.delete(BOOK_TABLE, null, null);
        //TODO db.delete(AUTHOR_TABLE, null, null);
        return false;
    }

    public void close() {
        db.close();
    }

}
