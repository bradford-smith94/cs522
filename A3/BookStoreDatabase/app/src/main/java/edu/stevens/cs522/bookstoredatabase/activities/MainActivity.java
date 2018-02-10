package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.databases.CartDbAdapter;
import edu.stevens.cs522.bookstoredatabase.entities.Book;

public class MainActivity extends Activity {

    // Use this when logging errors and warnings.
    @SuppressWarnings("unused")
    private static final String TAG = MainActivity.class.getCanonicalName();

    // These are request codes for subactivity request calls
    static final private int ADD_REQUEST = 1;

    static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    // The database adapter
    private CartDbAdapter dba;

    private Cursor cursor;

    private SimpleCursorAdapter adapter;

    private Book selectedBook;

    private ActionMode mActionMode;

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.cab_menu, menu);
            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    dba.delete(selectedBook);
                    cursor.requery();
                    adapter.notifyDataSetChanged();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // open the database using the database adapter
        dba = new CartDbAdapter(getApplicationContext());
        dba.open();

        // query the database using the database adapter, and manage the cursor on the main thread
        cursor = dba.fetchAllBooks();
        startManagingCursor(cursor);

        // use SimpleCursorAdapter to display the cart contents.
        String[] from = new String[] { BookContract.TITLE, BookContract.AUTHORS };
        int[] to = new int[] { R.id.cart_row_title, R.id.cart_row_author };
        adapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.cart_row, cursor, from, to);
        ListView listView = findViewById(R.id.cart_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = new Book((Cursor)adapter.getItem(position));
                Intent viewIntent = new Intent(parent.getContext(), ViewBookActivity.class);
                viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
                startActivity(viewIntent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }

                selectedBook = new Book((Cursor)adapter.getItem(position));
                // Start the CAB using the ActionMode.Callback defined above
                mActionMode = MainActivity.this.startActionMode(mActionModeCallback);
                view.setSelected(true);
                return true;
            }
        });
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
        switch (item.getItemId()) {

            // ADD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // CHECKOUT provide the UI for checking out
            case R.id.checkout:
                Intent checkoutIntent = new Intent(this, CheckoutActivity.class);
                checkoutIntent.putExtra(CheckoutActivity.NUM_BOOKS_KEY, dba.fetchAllBooks().getCount());
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
                    Book book = (Book) intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    dba.persist(book);
                    adapter.notifyDataSetChanged();
                    break;
                case CHECKOUT_REQUEST:
                    // CHECKOUT: empty the shopping cart.
                    dba.deleteAll();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }


    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

}
