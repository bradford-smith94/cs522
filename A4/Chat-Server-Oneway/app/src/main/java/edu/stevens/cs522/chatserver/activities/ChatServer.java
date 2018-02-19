/*********************************************************************

    Chat server: accept chat messages from clients.

    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

public class ChatServer extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks {

	final static public String TAG = ChatServer.class.getCanonicalName();

	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSocket serverSocket;

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true;

    /*
     * UI for displayed received messages
     */
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private Button next;

    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    static private final int LOADER_ID = 1;

	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /**
         * Initialize settings to default values.
         */
		if (savedInstanceState == null) {
			PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		}

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        int port = Integer.valueOf(settings.getString(SettingsActivity.APP_PORT_KEY, getResources().getString(R.string.default_app_port)));

		try {
			serverSocket = new DatagramSocket(port);
		} catch (Exception e) {
			Log.e(TAG, "Cannot open socket", e);
			return;
		}

        setContentView(R.layout.messages);

        // use SimpleCursorAdapter to display the messages received.
        String[] from = { MessageContract.MESSAGE_TEXT};
        int[] to = {R.id.text};
        messagesAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.message, null, from, to);

        messageList = (ListView)findViewById(R.id.message_list);
        messageList.setAdapter(messagesAdapter);

        // bind the button for "next" to this activity as listener
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);

        // initiate a query for all messages, by initializing a loader via the loader manager
        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);
	}

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
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
                Intent viewPeers = new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeers);
                break;

            // SETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    public void onClick(View v) {

		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {

			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);

			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            final Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.messageText = msgContents[2];

			Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

            Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = receivePacket.getAddress();
            sender.port = receivePacket.getPort();

            // persist the message, and the peer if first encounter
            // OK to do this on the main thread for this assignment
            Cursor cursor = getContentResolver().query(PeerContract.CONTENT_URI, null, PeerContract.NAME + " = ?", new String[]{sender.name}, null, null);
            ContentValues values = new ContentValues();
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                sender.id = new Peer(cursor).id;
                sender.writeToProvider(values);
                message.senderId = sender.id;
                getContentResolver().update(PeerContract.CONTENT_URI(sender.id), values, null, null);
            } else {
                sender.writeToProvider(values);
                Uri newUri = getContentResolver().insert(PeerContract.CONTENT_URI, values);
                message.senderId = PeerContract.getId(newUri);
            }
            ContentValues messageValues = new ContentValues();
            message.writeToProvider(messageValues);
            getContentResolver().insert(MessageContract.CONTENT_URI, messageValues);

		} catch (Exception e) {

			Log.e(TAG, "Problems receiving packet: " + e.getMessage());
			socketOK = false;
		}

	}

	/*
	 * Close the socket before exiting application
	 */
	public void closeSocket() {
		serverSocket.close();
	}

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri baseUri = MessageContract.CONTENT_URI;

        return new CursorLoader(this, baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
	    messagesAdapter.swapCursor((Cursor)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
	    messagesAdapter.swapCursor(null);
    }
}
