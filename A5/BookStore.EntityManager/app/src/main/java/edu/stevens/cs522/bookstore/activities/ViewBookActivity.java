package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;


public class ViewBookActivity extends Activity {

    // Use this as the key to return the book details as a Parcelable extra in the result intent.
    public static final String BOOK_KEY = "book";

    private ArrayAdapter<String> authorsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_book);

        // get book as parcelable intent extra and populate the UI with book details.
        Book book = getIntent().getParcelableExtra(BOOK_KEY);
        TextView textView = (TextView) findViewById(R.id.view_title);
        textView.setText(book.title);
        textView = (TextView) findViewById(R.id.view_isbn);
        textView.setText(book.isbn);

        ListView listView = (ListView) findViewById(R.id.view_authors);
        authorsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1);
        listView.setAdapter(authorsAdapter);

        for (Author author : book.authors) {
            authorsAdapter.add(author.name);
        }
        authorsAdapter.notifyDataSetChanged();
    }

}
