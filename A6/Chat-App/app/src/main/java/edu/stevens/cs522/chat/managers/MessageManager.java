package edu.stevens.cs522.chat.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = getAsyncResolver();
    }

    public void getAllMessagesAsync(IQueryListener<ChatMessage> listener) {
        // use QueryBuilder to complete this
        executeQuery(MessageContract.CONTENT_URI, null, null, null, listener);
    }

    public void persistAsync(final ChatMessage message) {
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        contentResolver.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri uri) {
                message.id = MessageContract.getId(uri);
            }
        });
    }

}
