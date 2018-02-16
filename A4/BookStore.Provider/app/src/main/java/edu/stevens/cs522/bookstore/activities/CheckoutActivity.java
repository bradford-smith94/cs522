package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.stevens.cs522.bookstore.R;


public class CheckoutActivity extends Activity {

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
                //TODO: get number of books from intent to print message
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
