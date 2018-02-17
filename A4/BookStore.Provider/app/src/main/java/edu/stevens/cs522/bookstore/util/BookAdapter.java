package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(ROW_LAYOUT, parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView titleLine = (TextView) view.findViewById(android.R.id.text1);
        TextView authorLine = (TextView) view.findViewById(android.R.id.text2);

        Book book = new Book(cursor);
        titleLine.setText(book.title);

        /* TODO:
        if (book.authors.length > 1) {
            authorLine.setText(book.getFirstAuthor().toString() + " et al");
        } else {
            authorLine.setText(book.getFirstAuthor().toString());
        }
        */
    }
}
