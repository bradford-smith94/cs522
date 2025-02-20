package edu.stevens.cs522.bookstore.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstore.R;

public class CheckoutActivity extends AppCompatActivity {

	public static final String BOOK_NUM_KEY = "num_books";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
			case R.id.order:
				// ORDER: display a toast message of how many books have been ordered and return
				Context context = getApplicationContext();
				int numBooks = (int)getIntent().getSerializableExtra(BOOK_NUM_KEY);
				String text;
				if (numBooks > 0)
                    text = String.format(getString(R.string.checkoutMessage), numBooks);
				else
				    text = getString(R.string.checkoutEmptyMessage);
				Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.show();
				setResult(RESULT_OK);
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
	
}
