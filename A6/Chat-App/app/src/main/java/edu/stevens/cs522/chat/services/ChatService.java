package edu.stevens.cs522.chat.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.chat.activities.SettingsActivity;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.util.InetAddressUtils;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService, SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String TAG = ChatService.class.getCanonicalName();

    protected static final String SEND_TAG = "ChatSendThread";

    protected static final String RECEIVE_TAG = "ChatReceiveThread";

    protected IBinder binder = new ChatBinder();

    protected SendHandler sendHandler;

    protected Thread receiveThread;

    protected DatagramSocket chatSocket;

    protected boolean socketOK = true;

    protected boolean finished = false;

    PeerManager peerManager;

    MessageManager messageManager;

    protected int chatPort;

    @Override
    public void onCreate() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        chatPort = prefs.getInt(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT);
        prefs.registerOnSharedPreferenceChangeListener(this);

        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);

        try {
            chatSocket = new DatagramSocket(chatPort);
        } catch (Exception e) {
            IllegalStateException ex = new IllegalStateException("Unable to init client socket.");
            ex.initCause(e);
            throw ex;
        }

        // initialize the thread that sends messages
        HandlerThread senderThread = new HandlerThread(SEND_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        senderThread.start();
        sendHandler = new SendHandler(senderThread.getLooper());

        /*
         * This is the thread that receives messages.
         */
        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            return ChatService.this;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(SettingsActivity.APP_PORT_KEY)) {
            try {
                chatSocket.close();
                chatPort = prefs.getInt(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT);
                chatSocket = new DatagramSocket(chatPort);
            } catch (IOException e) {
                IllegalStateException ex = new IllegalStateException("Unable to change client socket.");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    @Override
    public void send(InetAddress destAddress, int destPort, String sender, String messageText, ResultReceiver receiver) {
        Message message = sendHandler.obtainMessage();

        Bundle data = new Bundle();
        data.putSerializable(SendHandler.DEST_ADDRESS, destAddress);
        data.putSerializable(SendHandler.DEST_PORT, destPort);
        data.putSerializable(SendHandler.CHAT_NAME, sender);
        data.putSerializable(SendHandler.CHAT_MESSAGE, messageText);
        data.putParcelable(SendHandler.RECEIVER, receiver);
        message.setData(data);

        // send the message to the sending thread
        sendHandler.sendMessage(message);

    }


    private final class SendHandler extends Handler {

        public static final String CHAT_NAME = "edu.stevens.cs522.chat.services.extra.CHAT_NAME";
        public static final String CHAT_MESSAGE = "edu.stevens.cs522.chat.services.extra.CHAT_MESSAGE";
        public static final String DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message message) {

            try {
                InetAddress destAddr = null;

                int destPort = -1;

                byte[] sendData = null;  // Combine sender and message text; default encoding is UTF-8

                ResultReceiver receiver = null;

                // get data from message (including result receiver)
                Bundle data = message.getData();
                destAddr = (InetAddress) data.getSerializable(DEST_ADDRESS);
                destPort = (int) data.getSerializable(DEST_PORT);
                String username = (String) data.getSerializable(CHAT_NAME);
                String messageText = (String) data.getSerializable(CHAT_MESSAGE);
                receiver = data.getParcelable(RECEIVER);

                // combine data in sendData
                long now = new Date().getTime();
                String sep = new String(":");
                sendData = new byte[username.length() + messageText.length() + String.valueOf(now).length() + 2];
                int i = 0;
                for (int j = 0; j < username.length(); j++) {
                    sendData[i++] = username.getBytes("UTF-8")[j];
                }
                sendData[i++] = sep.getBytes("UTF-8")[0];
                for (int j = 0; j < String.valueOf(now).length(); j++) {
                    sendData[i++] = String.valueOf(now).getBytes("UTF-8")[j];
                }
                sendData[i++] = sep.getBytes("UTF-8")[0];
                for (int j = 0; j < messageText.length(); j++) {
                    sendData[i++] = messageText.toString().getBytes("UTF-8")[j];
                }

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddr, destPort);

                chatSocket.send(sendPacket);

                Log.i(TAG, "Sent packet: " + new String(sendData));

                receiver.send(RESULT_OK, null);

            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO exception: " + e.getMessage());
            }

        }
    }

    private final class ReceiverThread implements Runnable {

        public void run() {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (!finished && socketOK) {

                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

                    String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

                    final ChatMessage message = new ChatMessage();
                    message.sender = msgContents[0];
                    message.timestamp = new Date(Long.parseLong(msgContents[1]));
                    message.messageText = msgContents[2];

                    Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

                    Peer sender = new Peer();
                    sender.name = message.sender;
                    sender.timestamp = message.timestamp;
                    sender.address = receivePacket.getAddress();
                    sender.port = receivePacket.getPort();

                    peerManager.persistAsync(sender, new IContinue<Long>() {
                        @Override
                        public void kontinue(Long id) {
                            message.senderId = id;
                            messageManager.persistAsync(message);
                        }
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }

            }

        }

    }

}
