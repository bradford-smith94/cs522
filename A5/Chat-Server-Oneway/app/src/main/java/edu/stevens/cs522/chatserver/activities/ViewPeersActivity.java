package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.async.QueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    private PeerManager peerManager;

    private SimpleCursorAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // initialize peerAdapter with empty cursor (null)
        String[] from = {PeerContract.NAME};
        int[] to = {R.id.messageText};
        peerAdapter = new SimpleCursorAdapter(this, R.layout.message, null, from, to);

        ListView lv = (ListView)findViewById(R.id.peerList);
        lv.setAdapter(peerAdapter);
        lv.setOnItemClickListener(this);

        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: " + position);
        }
    }

    @Override
    public void handleResults(TypedCursor<Peer> results) {
        peerAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        peerAdapter.swapCursor(null);
    }
}
