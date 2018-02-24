package edu.stevens.cs522.chatserver.async;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor> {

    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }

    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;

    // not called directly
    private SimpleQueryBuilder(IEntityCreator<T> helper, ISimpleQueryListener<T> listener) {
        this.helper = helper;
        this.listener = listener;
    }

    public static <T> void executeQuery(Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener) {
        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
        AsyncContentResolver resolver = new AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, projection, selection, selectionArgs, null, qb);
    }

    @Override
    public void kontinue(Cursor cursor) {
        List<T> instances = new ArrayList<T>();
        if (cursor.moveToFirst()) {
            do {
                T instance = helper.create(cursor);
                instances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listener.handleResults(instances);
    }

}

