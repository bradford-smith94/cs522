package edu.stevens.cs522.bookstoredatabase.entities;

import static android.R.attr.author;

public class Author {
	
	// TODO Modify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!

	public long id;
	
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;

	public Author(String authorText) {
		String[] name = authorText.split(" ");
		switch (name.length) {
			case 0:
				firstName = lastName = "";
				break;
			case 1:
				firstName = "";
				lastName = name[0];
				break;
			case 2:
				firstName = name[0];
				lastName = name[1];
				break;
			default:
				firstName = name[0];
				middleInitial = name[1];
				lastName = name[2];
		}
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstName != null && !"".equals(firstName)) {
			sb.append(firstName);
			sb.append(' ');
		}
		if (middleInitial != null && !"".equals(middleInitial)) {
			sb.append(middleInitial);
			sb.append(' ');
		}
		if (lastName != null && !"".equals(lastName)) {
			sb.append(lastName);
		}
		return sb.toString();
	}

}
