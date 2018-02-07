package edu.stevens.cs522.bookstoredatabase.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    public long id;

    public String title;

    public Author[] authors;

    public String isbn;

    public String price;

    public Book(long id, String title, Author[] authors, String isbn, String price) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.price = price;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getFirstAuthor() {
        if (authors != null && authors.length > 0) {
            return authors[0].toString();
        } else {
            return "";
        }
    }

    public Book(Parcel in) {
        // init from parcel
        id = in.readLong();
        title = in.readString();
        int size = in.readInt();
        authors = new Author[size];
        in.readTypedArray(authors, Author.CREATOR);
        isbn = in.readString();
        price = in.readString();
    }

	@Override
    public void writeToParcel(Parcel out, int flags) {
        // save state to parcel
        out.writeLong(id);
        out.writeString(title);
        out.writeInt(authors.length);
        out.writeTypedArray(authors, flags);
        out.writeString(isbn);
        out.writeString(price);
    }

    public Book(Cursor cursor) {
        // TODO init from cursor
    }

    public void writeToProvider(ContentValues out) {
        // TODO write to ContentValues
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
