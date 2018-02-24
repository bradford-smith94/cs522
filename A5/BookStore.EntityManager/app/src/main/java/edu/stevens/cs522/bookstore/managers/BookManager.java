package edu.stevens.cs522.bookstore.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.async.SimpleQueryBuilder;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public BookManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
    }

    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // use QueryBuilder to complete this
        executeQuery(BookContract.CONTENT_URI, null, null, null, listener);
    }

    // NOTE: callback was originally of type Book, made it type cursor to match
    // the signature of AsyncContentResolver
    public void getBookAsync(long id, IContinue<Cursor> callback) {
        String selection = BookContract._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        contentResolver.queryAsync(BookContract.CONTENT_URI, null, selection,
                selectionArgs, null, callback);
    }

    public void persistAsync(final Book book) {
        ContentValues values = new ContentValues();
        book.writeToProvider(values);
        contentResolver.insertAsync(BookContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                book.id = BookContract.getId(uri);
            }
        });
    }

    public void deleteBooksAsync(Set<Long> toBeDeleted) {
        Long[] ids = new Long[toBeDeleted.size()];
        toBeDeleted.toArray(ids);
        String[] args = new String[ids.length];

        StringBuilder sb = new StringBuilder();
        if (ids.length > 0) {
            sb.append(AuthorContract.ID);
            sb.append("=?");
            args[0] = ids[0].toString();
            for (int ix = 1; ix < ids.length; ix++) {
                sb.append(" or ");
                sb.append(AuthorContract.ID);
                sb.append("=?");
                args[ix] = ids[ix].toString();
            }
        }
        String select = sb.toString();

        contentResolver.deleteAsync(BookContract.CONTENT_URI, select, args);
    }

}
