package dansul.namethiscountrycapital;


import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_PREF_HIDE_COUNTRY = "pref_hide_country";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // set theme needs to be invoked before super.onCreate
        setTheme(android.R.style.Theme_Holo);

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}













