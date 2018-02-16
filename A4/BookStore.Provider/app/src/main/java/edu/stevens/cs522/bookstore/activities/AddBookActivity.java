package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;


public class AddBookActivity extends Activity {

	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// provide ADD and CANCEL options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addbook_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

            // ADD: return the book details to the BookStore activity
            case R.id.add:
                Book book = addBook();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(BOOK_RESULT_KEY, book);
                setResult(RESULT_OK, resultIntent);
                finish();
                break;

            // CANCEL: cancel the request
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;

            default:
        }

		return false;
	}

	public Book addBook(){
		// Just build a Book object with the search criteria and return that.
        Book book = new Book();
        book.title = ((TextView)findViewById(R.id.search_title)).getText().toString();
        book.isbn = ((TextView)findViewById(R.id.search_isbn)).getText().toString();
        book.price = String.valueOf(0);

        String[] authorStrings = ((TextView)findViewById(R.id.search_author)).getText().toString().split(",");
        Author[] authors = new Author[authorStrings.length];
        for (int i = 0; i < authorStrings.length; i++) {
            authors[i] = new Author(authorStrings[i]);
        }
        book.authors = authors;

		return book;
	}

}
