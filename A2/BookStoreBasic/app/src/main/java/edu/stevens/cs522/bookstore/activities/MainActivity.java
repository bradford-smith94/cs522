package edu.stevens.cs522.bookstore.activities;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.util.BooksAdapter;

public class MainActivity extends AppCompatActivity {
	
	// Use this when logging errors and warnings.
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// There is a reason this must be an ArrayList instead of a List.
	private ArrayList<Book> shoppingCart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)

		// TODO Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

		// TODO use an array adapter to display the cart contents.

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);

        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            case R.id.checkout:
            	Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
            	startActivityForResult(checkoutIntent, CHECKOUT_REQUEST);
                break;

            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        switch(requestCode) {
            case ADD_REQUEST:
                // TODO ADD: add the book that is returned to the shopping cart.
                break;
            case CHECKOUT_REQUEST:
                // TODO CHECKOUT: empty the shopping cart.
                break;
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
		// TODO save the shopping cart contents (which should be a list of parcelables).
		
	}
	
}
