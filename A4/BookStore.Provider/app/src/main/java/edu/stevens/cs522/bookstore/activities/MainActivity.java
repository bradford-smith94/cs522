package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, LoaderManager.LoaderCallbacks {

	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();

	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;

	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    static final private int LOADER_ID = 1;

    BookAdapter bookAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);

        // set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setMultiChoiceModeListener(this);


        // use loader manager to initiate a query of the database
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// inflate a menu with ADD and CHECKOUT options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // ADD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // CHECKOUT provide the UI for checking out
            case R.id.checkout:
                Cursor cursor = getContentResolver().query(BookContract.CONTENT_URI, null, null, null, null);
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                checkoutIntent.putExtra(CheckoutActivity.NUM_BOOKS_KEY, cursor.getCount());
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
		// Handle results from the Add and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_REQUEST:
                    // ADD: add the book that is returned to the shopping cart.
                    // It is okay to do this on the main thread for BookStoreWithContentProvider
                    Book book = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    ContentValues values = new ContentValues();
                    book.writeToProvider(values);
                    getContentResolver().insert(BookContract.CONTENT_URI, values);
                    getContentResolver().notifyChange(BookContract.CONTENT_URI, null);
                    break;
                case CHECKOUT_REQUEST:
                    // CHECKOUT: empty the shopping cart.
                    // It is okay to do this on the main thread for BookStoreWithContentProvider
                    getContentResolver().delete(BookContract.CONTENT_URI, null, null);
                    getContentResolver().notifyChange(BookContract.CONTENT_URI, null);
                    break;
            }
        }

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	}

    /*
     * Loader callbacks
     */

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
		// use a CursorLoader to initiate a query on the database
        Uri baseURI = BookContract.CONTENT_URI;

		return new CursorLoader(this, baseURI, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader loader, Object data) {
        // populate the UI with the result of querying the provider
        ((Cursor)data).setNotificationUri(getContentResolver(), BookContract.CONTENT_URI);
        bookAdapter.swapCursor((Cursor)data);
	}

	@Override
	public void onLoaderReset(Loader loader) {
        // reset the UI when the cursor is empty
        bookAdapter.swapCursor(null);
	}


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // query for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider
        Book book = new Book((Cursor)bookAdapter.getItem(position));
        Intent viewIntent = new Intent(this, ViewBookActivity.class);
        viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
        startActivity(viewIntent);
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.books_cab, menu);

        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            selected.add(id);
        } else {
            selected.remove(id);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch(item.getItemId()) {
            case R.id.delete:
                // TODO delete the selected books
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

}
