package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public Peer() {

    }

    protected Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeLong(timestamp.getTime());
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
    }

    public Peer(Cursor cursor) {
        id = PeerContract.getId(cursor);
        name = PeerContract.getName(cursor);
        timestamp = new Date(PeerContract.getTimestamp(cursor));
        longitude = PeerContract.getLongitude(cursor);
        latitude = PeerContract.getLatitude(cursor);
    }

    public void writeToProvider(ContentValues out) {
        PeerContract.putName(out, name);
        PeerContract.putTimestamp(out, timestamp.getTime());
        PeerContract.putLongitude(out, longitude);
        PeerContract.putLatitude(out, latitude);
    }

}
