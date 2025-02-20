package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    private SimpleCursorAdapter adapter;

    private MessagesDbAdapter dbAdapter;

    private Cursor cursor;

    private ListView peers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        dbAdapter = new MessagesDbAdapter(getApplicationContext());
        dbAdapter.open();

        cursor = dbAdapter.fetchAllPeers();
        startManagingCursor(cursor);

        String[] from = {PeerContract.NAME};
        int[] to = {R.id.text};
        adapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.message, cursor, from, to);
        peers = (ListView)findViewById(R.id.peerList);
        peers.setOnItemClickListener(this);
        peers.setAdapter(adapter);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer clickedPeer = new Peer((Cursor)adapter.getItem(position));
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, clickedPeer.id);
        startActivity(intent);
    }
}
