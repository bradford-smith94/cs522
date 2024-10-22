package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {
	
	public int id;
	
	public String title;
	
	public Author[] authors;
	
	public String isbn;
	
	public String price;

	public Book(int id, String title, Author[] author, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = author;
		this.isbn = isbn;
		this.price = price;
	}

	protected Book(Parcel in) {
		id = in.readInt();
		title = in.readString();
		int size = in.readInt();
		authors = new Author[size];
		in.readTypedArray(authors, Author.CREATOR);
		isbn = in.readString();
		price = in.readString();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(title);
		dest.writeInt(authors.length);
		dest.writeTypedArray(authors, flags);
		dest.writeString(isbn);
		dest.writeString(price);
	}
}
