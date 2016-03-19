package com.zack.enderplan.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zack.enderplan.R;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private static final String KEY_PREF_LANGUAGE = "language";
    private static final String KEY_PREF_NIGHT_MODE = "night_mode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        setupActionBar();

        getFragmentManager().beginTransaction().add(R.id.frame_layout, new SettingsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private ListPreference languagePref, nightModePref;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            languagePref = (ListPreference) findPreference(KEY_PREF_LANGUAGE);
            nightModePref = (ListPreference) findPreference(KEY_PREF_NIGHT_MODE);

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
            switch (key) {
                case KEY_PREF_LANGUAGE:
                    String languageValue = sharedPreferences.getString(KEY_PREF_LANGUAGE, "");
                    languagePref.setSummary(languagePref.getEntries()[languagePref.findIndexOfValue(languageValue)]);

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
                    break;
                case KEY_PREF_NIGHT_MODE:
                    String nightModeValue = sharedPreferences.getString(KEY_PREF_NIGHT_MODE, "");
                    nightModePref.setSummary(nightModePref.getEntries()[nightModePref.findIndexOfValue(nightModeValue)]);

                    int mode = AppCompatDelegate.MODE_NIGHT_NO;
                    switch (nightModeValue) {
                        case "off":
                            mode = AppCompatDelegate.MODE_NIGHT_NO;
                            break;
                        case "on":
                            mode = AppCompatDelegate.MODE_NIGHT_YES;
                            break;
                        case "auto":
                            mode = AppCompatDelegate.MODE_NIGHT_AUTO;
                            break;
                        case "def":
                            mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                            break;
                        default:
                            break;
                    }
                    AppCompatDelegate.setDefaultNightMode(mode);
                    break;
                default:
                    break;
            }
            //返回并重新创建HomeActivity
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        private void initPreferenceSummary() {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //Language
            languagePref.setSummary(languagePref.getEntries()[languagePref.findIndexOfValue(sharedPreferences.getString(KEY_PREF_LANGUAGE, ""))]);
            //Night mode
            nightModePref.setSummary(nightModePref.getEntries()[nightModePref.findIndexOfValue(sharedPreferences.getString(KEY_PREF_NIGHT_MODE, ""))]);
        }
    }
}
