/*********************************************************************

    Chat server: accept chat messages from clients.

    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();

    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;

	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;


    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (!Settings.isRegistered(this)) {
			// launch registration activity
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);

            return;
		}

        setContentView(R.layout.messages);

        // use SimpleCursorAdapter to display the messages received.
        String[] from = {MessageContract.SENDER, MessageContract.MESSAGE_TEXT};
        int[] to = {R.id.senderName, R.id.messageText};
        messagesAdapter = new SimpleCursorAdapter(this, R.layout.message_with_sender, null, from, to);

        messageList = (ListView) findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        // create the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        peerManager = new PeerManager(this);
        messageManager.getAllMessagesAsync(this);

        // TODO instantiate helper for service

        // initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());

        sendButton = (Button) findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // inflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent viewPeersIntent = new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeersIntent);
                break;

            // REGISTER provide the UI for registering
            case R.id.register:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                break;

            // SETTINGS provide the UI for settings
            case R.id.settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (helper != null) {

            String chatRoom;

            String message = null;

            // TODO get chatRoom and message from UI, and use helper to post a message
            // TODO add the message to the database


            // End todo

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");
        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        String text;
        switch (resultCode) {
            case RESULT_OK:
                // show a success toast message
                text = getString(R.string.send_success);
                break;
            default:
                // show a failure toast message
                text = getString(R.string.send_failure);
                break;
        }
        Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        messagesAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        messagesAdapter.swapCursor(null);
    }

}
