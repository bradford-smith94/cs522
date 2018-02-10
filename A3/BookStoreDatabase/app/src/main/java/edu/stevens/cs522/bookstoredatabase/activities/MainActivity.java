package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO check if there is saved UI state, and if so, restore it (i.e. the cart contents)

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
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = new Book((Cursor)adapter.getItem(position));
                Intent viewIntent = new Intent(parent.getContext(), ViewBookActivity.class);
                viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
                startActivity(viewIntent);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.cart_list) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.listview_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        ListView listView = findViewById(R.id.cart_list);
        Book book = new Book((Cursor)listView.getItemAtPosition(info.position));
        switch(item.getItemId()) {

            case R.id.view:
                Intent viewIntent = new Intent(this, ViewBookActivity.class);
                viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
                startActivity(viewIntent);
                break;

            case R.id.delete:
                dba.delete(book);
                cursor.requery();
                adapter.notifyDataSetChanged();
                break;

            default:
        }

        return false;
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
        // TODO save the shopping cart contents (which should be a list of parcelables).

    }

}
