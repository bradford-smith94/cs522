package edu.stevens.cs522.chat.async;

import android.database.Cursor;

import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder implements IContinue<Cursor>{

    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }

    // TODO Complete the implementation of this

    @Override
    public void kontinue(Cursor value) {
        // TODO complete this
    }

}
