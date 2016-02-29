package com.zack.enderplan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;
import android.support.v7.widget.Toolbar;

import com.zack.enderplan.R;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private static final String KEY_PREF_LANGUAGE = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setupActionBar();

        getFragmentManager().beginTransaction().add(R.id.frame_layout, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference languagePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            languagePref = (ListPreference) findPreference(KEY_PREF_LANGUAGE);

            initPreferenceSummary();
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_LANGUAGE)) {
                String languageValue = sharedPreferences.getString(KEY_PREF_LANGUAGE, "");
                int index = languagePref.findIndexOfValue(languageValue);
                languagePref.setSummary(languagePref.getEntries()[index]);

                Configuration config = getResources().getConfiguration();
                switch (languageValue) {
                    case "en":
                        config.locale = Locale.ENGLISH;
                        break;
                    case "zh":
                        config.locale = Locale.CHINESE;
                        break;
                    default:
                        config.locale = Locale.getDefault();
                        break;
                }
                getResources().updateConfiguration(config, null);

                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }

        private void initPreferenceSummary() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            int index = languagePref.findIndexOfValue(sharedPreferences.getString(KEY_PREF_LANGUAGE, ""));
            languagePref.setSummary(languagePref.getEntries()[index]);
        }
    }
}
