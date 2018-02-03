package edu.stevens.cs522.bookstore.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.util.BooksAdapter;

public class MainActivity extends AppCompatActivity {
	
	// Use this when logging errors and warnings.
	private static final String TAG = MainActivity.class.getCanonicalName();

	private static final String SAVED_CART = "saved_cart";
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

	// There is a reason this must be an ArrayList instead of a List.
	private ArrayList<Book> shoppingCart;
	private BooksAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// check if there is saved UI state, and if so, restore it (i.e. the cart contents)
        if (savedInstanceState != null) {
            shoppingCart = savedInstanceState.getParcelableArrayList(SAVED_CART);
        } else {
            shoppingCart = new ArrayList<Book>();
        }

		// Set the layout (use cart.xml layout)
        setContentView(R.layout.cart);

		// use an array adapter to display the cart contents.
        adapter = new BooksAdapter(this, shoppingCart);
        ListView listView = (ListView) findViewById(R.id.cart_list);
        listView.setEmptyView(findViewById(R.id.cart_empty));
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book book = adapter.getItem(position);
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
        Book book = (Book) listView.getItemAtPosition(info.position);
        switch(item.getItemId()) {

            case R.id.view:
                Intent viewIntent = new Intent(this, ViewBookActivity.class);
                viewIntent.putExtra(ViewBookActivity.BOOK_KEY, book);
                startActivity(viewIntent);
                break;

            case R.id.delete:
                shoppingCart.remove(book);
                adapter.notifyDataSetChanged();
                break;

            default:
        }

        return false;
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
            	checkoutIntent.putExtra(CheckoutActivity.BOOK_NUM_KEY, shoppingCart.size());
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
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ADD_REQUEST:
                    // ADD: add the book that is returned to the shopping cart.
                    Book book = (Book) intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    shoppingCart.add(book);
                    adapter.notifyDataSetChanged();
                    break;
                case CHECKOUT_REQUEST:
                    // CHECKOUT: empty the shopping cart.
                    shoppingCart.clear();
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    super.onSaveInstanceState(savedInstanceState);
		// save the shopping cart contents (which should be a list of parcelables).
        savedInstanceState.putParcelableArrayList(SAVED_CART, shoppingCart);
	}
	
}
