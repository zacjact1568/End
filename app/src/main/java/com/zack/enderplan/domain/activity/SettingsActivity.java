package com.zack.enderplan.domain.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zack.enderplan.R;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.common.Constant;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

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

            languagePref = (ListPreference) findPreference(PreferenceHelper.KEY_PREF_LANGUAGE);
            nightModePref = (ListPreference) findPreference(PreferenceHelper.KEY_PREF_NIGHT_MODE);

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
                case PreferenceHelper.KEY_PREF_LANGUAGE:
                    String languageValue = sharedPreferences.getString(PreferenceHelper.KEY_PREF_LANGUAGE, Constant.DEF);
                    languagePref.setSummary(languagePref.getEntries()[languagePref.findIndexOfValue(languageValue)]);

                    Configuration config = getResources().getConfiguration();
                    switch (languageValue) {
                        case Constant.EN:
                            config.setLocale(Locale.ENGLISH);
                            break;
                        case Constant.ZH_CN:
                            config.setLocale(Locale.SIMPLIFIED_CHINESE);
                            break;
                        case Constant.ZH_TW:
                            config.setLocale(Locale.TRADITIONAL_CHINESE);
                            break;
                        default:
                            config.setLocale(Locale.getDefault());
                            break;
                    }
                    getResources().updateConfiguration(config, null);
                    break;
                case PreferenceHelper.KEY_PREF_NIGHT_MODE:
                    String nightModeValue = sharedPreferences.getString(PreferenceHelper.KEY_PREF_NIGHT_MODE, "");
                    nightModePref.setSummary(nightModePref.getEntries()[nightModePref.findIndexOfValue(nightModeValue)]);

                    int mode = AppCompatDelegate.MODE_NIGHT_NO;
                    switch (nightModeValue) {
                        case Constant.OFF:
                            mode = AppCompatDelegate.MODE_NIGHT_NO;
                            break;
                        case Constant.ON:
                            mode = AppCompatDelegate.MODE_NIGHT_YES;
                            break;
                        case Constant.AUTO:
                            mode = AppCompatDelegate.MODE_NIGHT_AUTO;
                            break;
                        case Constant.DEF:
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
            PreferenceHelper helper = PreferenceHelper.getInstance();
            //Language
            languagePref.setSummary(languagePref.getEntries()[languagePref.findIndexOfValue(helper.getStringPref(PreferenceHelper.KEY_PREF_LANGUAGE))]);
            //Night mode
            nightModePref.setSummary(nightModePref.getEntries()[nightModePref.findIndexOfValue(helper.getStringPref(PreferenceHelper.KEY_PREF_NIGHT_MODE))]);
        }
    }
}
