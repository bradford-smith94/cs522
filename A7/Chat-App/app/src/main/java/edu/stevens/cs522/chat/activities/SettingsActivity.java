package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import edu.stevens.cs522.chat.R;

/**
 * Created by dduggan.
 */

public class SettingsActivity extends Activity {

    /*
     * See Settings for saving and restoring settings
     */

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.settings);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the messages content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}