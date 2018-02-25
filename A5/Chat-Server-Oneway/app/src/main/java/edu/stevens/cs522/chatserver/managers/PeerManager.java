package edu.stevens.cs522.chatserver.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.CursorAdapter;

import java.util.List;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.async.SimpleQueryBuilder;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;

import static android.provider.BaseColumns._ID;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = getAsyncResolver();
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // use QueryBuilder to complete this
        executeQuery(PeerContract.CONTENT_URI, null, null, null, listener);
    }

    public void getPeerAsync(long id, final IContinue<Peer> callback) {
        // need to check that peer is not null (not in database)
        executeSimpleQuery(PeerContract.CONTENT_URI(id), null, null, null, new SimpleQueryBuilder.ISimpleQueryListener<Peer>() {
            @Override
            public void handleResults(List<Peer> results) {
                if (!results.isEmpty()) {
                    Log.i(tag, " handling results Peer already exists");
                    callback.kontinue(results.get(0));
                } else {
                    Log.i(tag, " handling results Peer is new");
                    callback.kontinue(null);
                }
            }
        });
    }

    public void getPeerAsync(String name, final IContinue<Peer> callback) {
        // need to check that peer is not null (not in database)
        String selection = PeerContract.NAME + " = ?";
        String[] selectionArgs = {name};
        executeSimpleQuery(PeerContract.CONTENT_URI, null, selection, selectionArgs, new SimpleQueryBuilder.ISimpleQueryListener<Peer>() {
            @Override
            public void handleResults(List<Peer> results) {
                if (!results.isEmpty()) {
                    Log.i(tag, " handling results Peer already exists");
                    callback.kontinue(results.get(0));
                } else {
                    Log.i(tag, " handling results Peer is new");
                    callback.kontinue(null);
                }
            }
        });
    }

    public void persistAsync(Peer peer, final IContinue<Long> callback) {
        // need to ensure the peer is not already in the database
        final ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        getPeerAsync(peer.name, new IContinue<Peer>() {
            @Override
            public void kontinue(Peer returnedPeer) {
                if (returnedPeer != null) {
                    //update
                    Log.i(tag, " persist updating Peer");
                    contentResolver.updateAsync(PeerContract.CONTENT_URI(returnedPeer.id), values, null, null);
                    callback.kontinue(returnedPeer.id);
                } else {
                    //insert
                    Log.i(tag, " persist inserting Peer");
                    contentResolver.insertAsync(PeerContract.CONTENT_URI, values, new IContinue<Uri>() {
                        @Override
                        public void kontinue(Uri uri) {
                            callback.kontinue(PeerContract.getId(uri));
                        }
                    });
                }
            }
        });
    }

}
