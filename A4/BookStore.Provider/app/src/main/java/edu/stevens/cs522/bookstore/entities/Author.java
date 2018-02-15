package edu.stevens.cs522.bookstore.entities;

import static android.R.attr.author;

public class Author {
	
	// TODO Modify this to implement the Parcelable interface.

	public long id;
	
	public String name;

	public Author(String authorText) {
		this.name = authorText;
	}

}
