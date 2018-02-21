package edu.stevens.cs522.bookstore.managers;

import android.content.Context;
import android.database.Cursor;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
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
        // TODO use QueryBuilder to complete this
    }

    public void getBookAsync(long id, IContinue<Book> callback) {
        // TODO
    }

    public void persistAsync(Book book) {
        // TODO
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
            for (int ix=1; ix<ids.length; ix++) {
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
