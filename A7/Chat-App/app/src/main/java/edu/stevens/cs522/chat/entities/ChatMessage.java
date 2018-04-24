package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public String sender;

    public long senderId;

    public ChatMessage() {

    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
        sender = in.readString();
        senderId = in.readLong();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(messageText);
        dest.writeLong(timestamp.getTime());
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public ChatMessage(Cursor cursor) {
        id = MessageContract.getId(cursor);
        messageText = MessageContract.getMessageText(cursor);
        timestamp = new Date(MessageContract.getTimestamp(cursor));
        longitude = MessageContract.getLongitude(cursor);
        latitude = MessageContract.getLatitude(cursor);
        sender = MessageContract.getSender(cursor);
        senderId = MessageContract.getSenderId(cursor);
    }

    public void writeToProvider(ContentValues out) {
        MessageContract.putMessageText(out, messageText);
        MessageContract.putTimestamp(out, timestamp.getTime());
        MessageContract.putLongitude(out, longitude);
        MessageContract.putLatitude(out, latitude);
        MessageContract.putSender(out, sender);
        MessageContract.putSenderId(out, senderId);
    }

}
