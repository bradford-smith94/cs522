package edu.stevens.cs522.chat.services;

import android.os.ResultReceiver;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by dduggan.
 */

public interface IChatService {

    /**
     * Operation for bound service provides for sending a message to another device.  The operation is asynchronous,
     * the sending will be done on a background thread.
     * @param destAddress
     * @param destPort
     * @param sender
     * @param message
     * @param receiver
     */
    public void send(InetAddress destAddress, int destPort, String sender, String message, ResultReceiver receiver);
}
