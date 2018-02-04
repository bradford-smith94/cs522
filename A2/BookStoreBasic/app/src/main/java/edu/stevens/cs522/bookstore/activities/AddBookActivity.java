package edu.stevens.cs522.bookstore.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Arrays;
import java.util.List;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;

public class AddBookActivity extends AppCompatActivity {
	
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

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.addbook_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
            case R.id.add:
				Book book = addBook();
				Intent resultIntent = new Intent();
				resultIntent.putExtra(BOOK_RESULT_KEY, book);
				setResult(RESULT_OK, resultIntent);
                finish();
                break;
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;

            default:
        }

		return false;
	}
	
	public Book addBook(){
        EditText editText = findViewById(R.id.search_title);
        String title = editText.getText().toString();
        editText = findViewById(R.id.search_author);
        String author = editText.getText().toString();
        editText = findViewById(R.id.search_isbn);
        String isbn = editText.getText().toString();

        // support multiple authors separated by commas
        List<String> authorList = Arrays.asList(author.split(","));
        Author[] authors = new Author[authorList.size()];
        for (int i = 0; i < authorList.size(); i++) {
            authors[i] = new Author(authorList.get(i).trim());
        }

        // just ignoring id and price for now
        return new Book(0, title, authors, isbn, null);
	}

}
