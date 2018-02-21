package edu.stevens.cs522.chatserver.async;

import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;

import edu.stevens.cs522.chatserver.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder implements LoaderManager.LoaderCallbacks {

    public static interface IQueryListener<T> {

        public void handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    // TODO complete the implementation of this

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
