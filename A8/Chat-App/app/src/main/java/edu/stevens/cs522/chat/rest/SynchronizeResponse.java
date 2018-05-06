package edu.stevens.cs522.chat.rest;

import android.os.Parcel;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by dduggan.
 */

public class SynchronizeResponse extends Response {

    public static final String ID_LABEL = "id";

    public SynchronizeResponse(HttpURLConnection connection) throws IOException {
        super(connection);
    }

    @Override
    public boolean isValid() { return true; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    public SynchronizeResponse(Parcel in) {
        super(in);
        // TODO
    }

    public static Creator<SynchronizeResponse> CREATOR = new Creator<SynchronizeResponse>() {
        @Override
        public SynchronizeResponse createFromParcel(Parcel source) {
            return new SynchronizeResponse(source);
        }

        @Override
        public SynchronizeResponse[] newArray(int size) {
            return new SynchronizeResponse[size];
        }
    };
}
