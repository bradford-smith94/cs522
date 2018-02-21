package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;

public class Book {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

    public Book() {
    }

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	public Book(Parcel in) {
		// TODO init from parcel
	}

	public void writeToParcel(Parcel out) {
		// TODO save state to parcel
	}

	public Book(Cursor cursor) {
		// TODO init from cursor
	}

	public void writeToProvider(ContentValues out) {
		// TODO write to ContentValues
	}


}