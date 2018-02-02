package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable {

	// NOTE: middleInitial may be NULL!
	
	public String firstName;
	
	public String middleInitial;
	
	public String lastName;

	public Author() {
    }

	public Author(String name) {
		String[] names = name.split("\\s+");
		this.firstName = names[0];
		if (names.length > 2)
		    this.middleInitial = String.valueOf(names[1].charAt(0));
		this.lastName = names[names.length - 1];
    }

	protected Author(Parcel in) {
		firstName = in.readString();
		middleInitial = in.readString();
		lastName = in.readString();
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(firstName);
		dest.writeString(middleInitial);
		dest.writeString(lastName);
	}
}
