/*********************************************************************

    Chat server: accept chat messages from clients.

    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class RegisterActivity extends Activity implements OnClickListener, ResultReceiverWrapper.IReceive {

	final static public String TAG = RegisterActivity.class.getCanonicalName();

	final static public int ALREADY_REGISTERED = RESULT_OK - 1;
	final static public int USERNAME_TAKEN = ALREADY_REGISTERED - 1;
	final static public int REGISTER_REQUEST = 1;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText userNameText;

    private Button registerButton;

    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when registered.
     */
    private ResultReceiverWrapper registerResultReceiver;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (Settings.isRegistered(this)) {
		    setResult(ALREADY_REGISTERED, null);
			finish();
            return;
		}

        setContentView(R.layout.register);

        // initialize registerResultReceiver
        registerResultReceiver = new ResultReceiverWrapper(new Handler());

        // instantiate helper for service
        helper = new ChatHelper(this, registerResultReceiver);

        userNameText = (EditText) findViewById(R.id.chat_name_text);
        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(this);
    }

	public void onResume() {
        super.onResume();
        registerResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        registerResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback for the REGISTER button.
     */
    public void onClick(View v) {
        if (helper != null) {

            // get userName from UI, and use helper to register
            String userName = userNameText.getText().toString().trim();
            if (userName.equals("")) {
                return;
            }

            // set registered in settings upon completion
            helper.register(userName);

            Log.i(TAG, "Registered: " + userName);
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        String text;
        switch (resultCode) {
            case RESULT_OK:
                // show a success toast message
                text = getString(R.string.register_success);
                break;
            case RESULT_CANCELED:
                text = getString(R.string.already_registered);
                break;
            case USERNAME_TAKEN:
                text = getString(R.string.username_taken);
                break;
            default:
                // show a failure toast message
                text = getString(R.string.register_failed);
                break;
        }
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
        if (resultCode == RESULT_OK || resultCode == RESULT_CANCELED) {
            finish();
        }
    }

}
