package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements OnItemClickListener, AbsListView.MultiChoiceModeListener, IQueryListener {

    // Use this when logging errors and warnings.
    private static final String TAG = MainActivity.class.getCanonicalName();

    // These are request codes for subactivity request calls
    static final private int ADD_REQUEST = 1;

    static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private BookManager bookManager;

    private BookAdapter bookAdapter;

    private ActionMode actionMode = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

        // Use a custom cursor adapter to display an empty (null) cursor.
        bookAdapter = new BookAdapter(this, null);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setEmptyView(findViewById(R.id.cart_empty));
        lv.setAdapter(bookAdapter);

        // Set listeners for item selection and multi-choice CAB
        lv.setOnItemClickListener(this);
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        lv.setMultiChoiceModeListener(this);
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (actionMode != null)
                    return false;
                actionMode = startActionMode(MainActivity.this);
                ((ListView) parent).setItemChecked(position, true);
                return true;
            }
        });


        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);
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
                checkoutIntent.putExtra(CheckoutActivity.NUM_BOOKS_KEY, bookAdapter.getCount());
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

        if (resultCode == RESULT_OK) {
            // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
            switch (requestCode) {
                case ADD_REQUEST:
                    // ADD: add the book that is returned to the shopping cart.
                    Book book = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    Log.i(TAG, " adding a book");
                    bookManager.persistAsync(book);
                    bookManager.refreshAllBooksAsync(this);
                    break;
                case CHECKOUT_REQUEST:
                    // CHECKOUT: empty the shopping cart.
                    bookManager.deleteAllBooksAsync();
                    bookManager.refreshAllBooksAsync(this);
                    break;
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    }

    /*
     * Query listener callbacks
     */

    @Override
    public void handleResults(TypedCursor results) {
        // update the adapter
        Log.i(TAG, " handling results");
        bookAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        // update the adapter
        Log.i(TAG, " closing results");
        bookAdapter.swapCursor(null);
    }


    /*
     * Selection of a book from the list view
     */

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (actionMode == null) {
            // query for this book's details, and send to ViewBookActivity
            Book book = new Book((Cursor) bookAdapter.getItem(position));
            Intent viewIntent = new Intent(this, ViewBookActivity.class);
            viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
            startActivity(viewIntent);
        }
    }


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    Set<Long> selected;

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // inflate the menu for the CAB
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.books_cab, menu);

        selected = new HashSet<Long>();
        return true;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        ListView lv = (ListView) findViewById(android.R.id.list);
        if (checked) {
            selected.add(id);
            lv.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        } else {
            selected.remove(id);
            lv.getChildAt(position).setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                // delete the selected books
                bookManager.deleteBooksAsync(selected);
                bookManager.refreshAllBooksAsync(this);
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
        actionMode = null;
    }

}
