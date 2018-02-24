package edu.stevens.cs522.bookstore.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import edu.stevens.cs522.bookstore.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks {

    public interface IQueryListener<T> {

        void handleResults(TypedCursor<T> results);

        void closeResults();

    }

    private Context context;
    private Uri uri;
    private String[] projection;
    private String selection;
    private String[] selectionArgs;
    private int loaderID;
    private IEntityCreator<T> creator;
    private IQueryListener<T> listener;

    // not called directly
    private QueryBuilder(String tag,
                         Context context,
                         Uri uri,
                         String[] projection,
                         String selection,
                         String[] selectionArgs,
                         int loaderID,
                         IEntityCreator<T> creator,
                         IQueryListener<T> listener) {
        this.context = context;
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.loaderID = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {
        QueryBuilder<T> qb = new QueryBuilder<>(tag, context, uri, projection,
                selection, selectionArgs, loaderID, creator, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }

    public static <T> void reexecuteQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {
        QueryBuilder<T> qb = new QueryBuilder<>(tag, context, uri, projection,
                selection, selectionArgs, loaderID, creator, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.restartLoader(loaderID, null, qb);
        lm.initLoader(loaderID, null, qb);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        if (id == loaderID) {
            return new CursorLoader(context,
                    uri,
                    projection,
                    selection,
                    selectionArgs,
                    null);
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        if (loader.getId() == loaderID) {
            listener.handleResults(new TypedCursor<T>((Cursor) data, creator));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == loaderID) {
            listener.closeResults();
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}
