package edu.stevens.cs522.bookstoredatabase.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstoredatabase.contracts.AuthorContract;

public class Author implements Parcelable {

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
                middleInitial = String.valueOf(name[1].charAt(0));
                lastName = name[2];
        }
    }

    protected Author(Parcel in) {
        id = in.readLong();
        firstName = in.readString();
        middleInitial = in.readString();
        lastName = in.readString();
    }

    public Author(Cursor cursor) {
        firstName = AuthorContract.getFirstName(cursor);
        middleInitial = AuthorContract.getMiddleInitial(cursor);
        lastName = AuthorContract.getLastName(cursor);
    }

    public void writeToProvider(ContentValues out) {
        AuthorContract.putFirstName(out, firstName);
        AuthorContract.putMiddleInitial(out, middleInitial);
        AuthorContract.putLastName(out, lastName);
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
        dest.writeLong(id);
        dest.writeString(firstName);
        dest.writeString(middleInitial);
        dest.writeString(lastName);
    }
}
