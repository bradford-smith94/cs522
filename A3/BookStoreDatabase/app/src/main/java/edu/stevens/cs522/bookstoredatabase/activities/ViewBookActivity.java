package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Book;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// TODO get book as parcelable intent extra and populate the UI with book details.
		Book book = getIntent().getParcelableExtra(BOOK_KEY);

	}

}