package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

public class Author implements Parcelable {

    public long id;

    public String name;

    public Author(String authorText) {
        this.id = 0;
        this.name = authorText;
    }

    protected Author(Parcel in) {
        id = in.readLong();
        name = in.readString();
    }

    public static final Creator<Author> CREATOR = new Creator<Author>() {
        @Override
        public Author createFromParcel(Parcel in) {
            return new Author(in);
        }

        @Override
        public Author[] newArray(int size) {
            return new Author[size];
        }
    };

    public Author(Cursor cursor) {
        // init from cursor
        id = AuthorContract.getId(cursor);
        name = AuthorContract.getName(cursor);
    }

    public void writeToProvider(ContentValues out) {
        // write to ContentValues
        AuthorContract.putName(out, name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
    }
}

