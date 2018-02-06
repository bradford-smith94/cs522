package edu.stevens.cs522.chatserver.entities;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Peer {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

}
