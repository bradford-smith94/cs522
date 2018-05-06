package edu.stevens.cs522.chat.settings;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by dduggan.
 */

public class Settings {

    private static String SETTINGS = "settings";

    public static String CLIENT_ID_KEY = "client-senderId";

    private static String SENDER_ID_KEY = "sender-senderId";

    public static String CHAT_NAME_KEY = "user-name";

    public static UUID getClientId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String clientID = prefs.getString(CLIENT_ID_KEY, null);
        if (clientID == null) {
            clientID = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(CLIENT_ID_KEY, clientID);
            editor.commit();
        }
        return UUID.fromString(clientID);
    }

    public static long getSenderId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getLong(SENDER_ID_KEY, -1);
    }

    public static void saveSenderId(Context context, long senderId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putLong(SENDER_ID_KEY, senderId);
        editor.commit();
    }

    public static String getChatName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        return prefs.getString(CHAT_NAME_KEY, null);
    }

    public static void saveChatName(Context context, String chatName) {
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE).edit();
        editor.putString(CHAT_NAME_KEY, chatName);
        editor.commit();
    }

    public static boolean isRegistered(Context context) {
        return getSenderId(context) >= 0;
    }

}
