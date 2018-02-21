package edu.stevens.cs522.chatserver.entities;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    public Peer() {
    }

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public Peer(Cursor cursor) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }
}
