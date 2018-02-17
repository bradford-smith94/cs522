package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Book;


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
        authorsAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.author_row, R.id.author_name);
        listView.setAdapter(authorsAdapter);

        for (int i = 0; i < book.authors.length; i++) {
            authorsAdapter.add(book.authors[i].name);
        }
        authorsAdapter.notifyDataSetChanged();
    }

}
