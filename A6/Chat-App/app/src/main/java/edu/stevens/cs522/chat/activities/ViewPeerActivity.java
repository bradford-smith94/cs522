package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        TextView textView = (TextView) findViewById(R.id.view_user_name);
        textView.setText(peer.name);
        textView = (TextView) findViewById(R.id.view_timestamp);
        textView.setText(peer.timestamp.toString());
        textView = (TextView) findViewById(R.id.view_address);
        textView.setText(peer.address.toString());
        textView = (TextView) findViewById(R.id.view_port);
        textView.setText(String.valueOf(peer.port));
    }

}
