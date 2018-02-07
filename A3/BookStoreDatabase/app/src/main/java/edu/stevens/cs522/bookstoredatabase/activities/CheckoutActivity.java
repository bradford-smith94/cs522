package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstoredatabase.R;


public class CheckoutActivity extends Activity {

	public static final String NUM_BOOKS_KEY = "num_books";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// display ORDER and CANCEL options.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {

			// ORDER: display a toast message of how many books have been ordered and return
			case R.id.order:
				Context context = getApplicationContext();
				int numBooks = (int) getIntent().getSerializableExtra(NUM_BOOKS_KEY);
				String text;
				if (numBooks > 0)
					text = String.format(getString(R.string.checkoutMessage), numBooks);
				else
					text = getString(R.string.checkoutMessageEmpty);
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
				setResult(RESULT_OK);
				finish();
				break;

			// CANCEL: just return with REQUEST_CANCELED as the result code
			case R.id.cancel:
			    setResult(RESULT_CANCELED);
			    finish();
				break;

			default:
		}

		return false;
	}
	
}
