package edu.stevens.cs522.chatserver.managers;

import android.content.ContentResolver;
import android.content.Context;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IEntityCreator;


/**
 * Created by dduggan.
 */

public abstract class Manager<T> {

    protected final Context context;

    private final IEntityCreator<T> creator;

    protected final int loaderID;

    protected final String tag;

    protected Manager(Context context,
                      IEntityCreator<T> creator,
                      int loaderID) {
        this.context = context;
        this.creator = creator;
        this.loaderID = loaderID;
        this.tag = this.getClass().getCanonicalName();
    }

    private ContentResolver syncResolver;

    private AsyncContentResolver asyncResolver;

    protected ContentResolver getSyncResolver() {
        if (syncResolver == null)
            syncResolver = context.getContentResolver();
        return syncResolver;
    }

    protected AsyncContentResolver getAsyncResolver() {
        if (asyncResolver == null)
            asyncResolver = new AsyncContentResolver(context.getContentResolver());
        return asyncResolver;
    }

    // TODO Provide operations for executing queries (see lectures)


}
