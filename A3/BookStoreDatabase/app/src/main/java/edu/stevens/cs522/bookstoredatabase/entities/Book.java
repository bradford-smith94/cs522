package edu.stevens.cs522.bookstoredatabase.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;

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
        //just ignoring id for now
        this.id = 0;
        this.title = BookContract.getTitle(cursor);
        String[] authorStrings = BookContract.getAuthors(cursor);
        Author[] authorObjects = new Author[authorStrings.length];
        for (int i = 0; i < authorStrings.length; i++) {
            authorObjects[i] = new Author(authorStrings[i]);
        }
        this.authors = authorObjects;
        this.isbn = BookContract.getISBN(cursor);
        this.price = BookContract.getPrice(cursor);
    }

    public void writeToProvider(ContentValues out) {
        BookContract.putTitle(out, title);
        String[] authorStrings = new String[authors.length];
        for (int i = 0; i < authors.length; i++) {
            authorStrings[i] = authors[i].toString();
        }
        BookContract.putAuthors(out, authorStrings);
        BookContract.putISBN(out, isbn);
        BookContract.putPrice(out, price);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
