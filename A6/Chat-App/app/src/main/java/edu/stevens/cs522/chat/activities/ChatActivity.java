/*********************************************************************

 Chat server: accept chat messages from clients.

 Sender name and GPS coordinates are encoded
 in the messages, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ServiceConnection, ResultReceiverWrapper.IReceive {

    final static public String TAG = ChatActivity.class.getCanonicalName();

    /*
     * UI for displaying received messages
     */
    private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;


    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

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
        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        }

        settings = PreferenceManager.getDefaultSharedPreferences(this);

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

        // initiate binding to the service
        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, this, Context.BIND_AUTO_CREATE);

        // initialize sendResultReceiver
        sendResultReceiver = new ResultReceiverWrapper(new Handler());

        // initialize UI
        destinationHost = (EditText) findViewById(R.id.destination_host);
        destinationPort = (EditText) findViewById(R.id.destination_port);
        messageText = (EditText) findViewById(R.id.message_text);
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
        switch (item.getItemId()) {

            // PEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent viewPeersIntent = new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeersIntent);
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
        if (chatService != null) {
            /*
             * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            InetAddress destAddr;

            int destPort;

            String username;

            String message;

            try {
                // get destination and message from UI, and username from preferences.
                destAddr = InetAddress.getByName(String.valueOf(destinationHost.getText()));
                destPort = Integer.parseInt(String.valueOf(destinationPort.getText()));
                message = String.valueOf(messageText.getText());
                username = settings.getString(SettingsActivity.USERNAME_KEY, SettingsActivity.DEFAULT_USERNAME);

                // use the service to send a message to the specified destination.
                chatService.send(destAddr, destPort, username, message, sendResultReceiver);

                Log.i(TAG, "Sent message: " + message);
            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            }

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

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // initialize chatService
        chatService = ((ChatService.ChatBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatService = null;
    }
}
