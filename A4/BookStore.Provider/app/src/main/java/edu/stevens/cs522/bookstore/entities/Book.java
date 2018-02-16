package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

public class Book implements Parcelable {

	public long id;

	public String title;

	public Author[] authors;

	public String isbn;

	public String price;

    public Book() {
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
        id = in.readLong();
        title = in.readString();
        authors = in.createTypedArray(Author.CREATOR);
        isbn = in.readString();
        price = in.readString();
    }

	public Book(Cursor cursor) {
		// init from cursor
        id = BookContract.getId(cursor);
        title = BookContract.getTitle(cursor);
        String[] authorStrings = BookContract.getAuthors(cursor);
        Author[] authorObjects = new Author[authorStrings.length];
        for (int i = 0; i < authorStrings.length; i++) {
            authorObjects[i] = new Author(authorStrings[i]);
        }
        this.authors = authorObjects;
        isbn = BookContract.getISBN(cursor);
        price = BookContract.getPrice(cursor);
	}

	public void writeToProvider(ContentValues out) {
		// write to ContentValues
        BookContract.putTitle(out, title);
        /* TODO: what am I doing with authors?
        String[] authorStrings = new String[authors.length];
        for (int i = 0; i < authors.length; i++) {
            authorStrings[i] = authors[i].toString();
        }
        BookContract.putAuthors(out, authorStrings);
        */
        BookContract.putISBN(out, isbn);
        BookContract.putPrice(out, price);
	}


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeTypedArray(authors, flags);
        dest.writeString(isbn);
        dest.writeString(price);
    }
}
