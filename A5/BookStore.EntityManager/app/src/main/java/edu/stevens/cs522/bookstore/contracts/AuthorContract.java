package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String ID = _ID;

    public static final String NAME = "name";

    /*
     * NAME column
     */

    private static int nameColumn = -1;

    public static String getFirstName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn =  cursor.getColumnIndexOrThrow(NAME);;
        }
        return cursor.getString(nameColumn);
    }

    public static void putFirstName(ContentValues values, String firstName) {
        values.put(NAME, firstName);
    }

    // TODO complete the definitions of the operations for Parcelable, cursors and contentvalues

}
