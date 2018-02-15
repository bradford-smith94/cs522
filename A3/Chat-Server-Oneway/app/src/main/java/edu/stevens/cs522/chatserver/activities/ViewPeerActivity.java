package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";

    private MessagesDbAdapter dbAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        long id = getIntent().getLongExtra(PEER_ID_KEY, 0);

        dbAdapter = new MessagesDbAdapter(getApplicationContext());
        dbAdapter.open();
        Peer peer = dbAdapter.fetchPeer(id);

        TextView textView = (TextView)findViewById(R.id.view_user_name);
        textView.setText(peer.name);
        textView = (TextView) findViewById(R.id.view_timestamp);
        textView.setText(peer.timestamp.toString());
        textView = (TextView) findViewById(R.id.view_address);
        textView.setText(peer.address.toString());
        textView = (TextView) findViewById(R.id.view_port);
        textView.setText(String.valueOf(peer.port));

        dbAdapter.close();
    }

}
