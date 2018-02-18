package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks {

    private static final int LOADER_ID = 2;

    private SimpleCursorAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // initialize peerAdapter with empty cursor (null)
        String[] from = {PeerContract.NAME};
        int[] to = {R.id.text};
        peerAdapter = new SimpleCursorAdapter(getApplicationContext(), R.layout.message, null, from, to);

        ListView lv = (ListView)findViewById(R.id.peerList);
        lv.setAdapter(peerAdapter);
        lv.setOnItemClickListener(this);

        LoaderManager lm = getLoaderManager();
        lm.initLoader(LOADER_ID, null, this);
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
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        Uri baseUri = PeerContract.CONTENT_URI;

        return new CursorLoader(this, baseUri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        peerAdapter.swapCursor((Cursor)data);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        peerAdapter.swapCursor(null);
    }
}
