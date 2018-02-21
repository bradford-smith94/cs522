package edu.stevens.cs522.bookstore.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.regex.Pattern;

/**
 * Created by dduggan.
 */

public class BookContract implements BaseColumns {

    public static final String AUTHORITY = "edu.stevens.cs522.bookstore";

    public static final Uri CONTENT_URI(String authority, String path) {
        return new Uri.Builder().scheme("content")
                .authority(authority)
                .path(path)
                .build();
    }

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Book");

    public static Uri withExtendedPath(Uri uri,
                                       String... path) {
        Uri.Builder builder = uri.buildUpon();
        for (String p : path)
            builder.appendPath(p);
        return builder.build();
    }

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final long getId(Uri uri) {
        return Long.parseLong(uri.getLastPathSegment());
    }

    public static final String CONTENT_PATH(Uri uri) {
        return uri.getPath().substring(1);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));



    public static final String ID = _ID;

    public static final String TITLE = "title";

    public static final String AUTHORS = "authors";

    public static final String ISBN = "isbn";

    public static final String PRICE = "price";

    /*
     * TITLE column
     */

    private static int titleColumn = -1;

    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn =  cursor.getColumnIndexOrThrow(TITLE);;
        }
        return cursor.getString(titleColumn);
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }

    /*
     * Synthetic authors column
     */
    public static final char SEPARATOR_CHAR = '|';

    private static final Pattern SEPARATOR =
            Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);

    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

    private static int authorColumn = -1;

    public static String[] getAuthors(Cursor cursor) {
        if (authorColumn < 0) {
            authorColumn =  cursor.getColumnIndexOrThrow(AUTHORS);;
        }
        return readStringArray(cursor.getString(authorColumn));
    }


    // TODO complete definitions of other getter and setter operations


}
