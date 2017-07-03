package com.zack.enderplan.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;

import com.zack.enderplan.R;
import com.zack.enderplan.common.Constant;
import com.zack.enderplan.model.DataManager;
import com.zack.enderplan.model.preference.PreferenceHelper;
import com.zack.enderplan.view.activity.HomeActivity;
import com.zack.enderplan.view.dialog.MessageDialogFragment;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DataManager mDataManager = DataManager.getInstance();

    private SwitchPreferenceCompat mNightModePreference;
    private ListPreference mDrawerHeaderDisplayPreference;
    private ListPreference mTypeListItemEndDisplayPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        mNightModePreference = (SwitchPreferenceCompat) findPreference(Constant.PREF_KEY_NIGHT_MODE);
        mDrawerHeaderDisplayPreference = (ListPreference) findPreference(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY);
        mTypeListItemEndDisplayPreference = (ListPreference) findPreference(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY);

        mNightModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                MessageDialogFragment fragment = MessageDialogFragment.newInstance(getString(R.string.title_dialog_switch_night_mode), getString(R.string.msg_dialog_switch_night_mode), null, getString(R.string.button_cancel), getString(R.string.button_restart));
                fragment.setOnPositiveButtonClickListener(new MessageDialogFragment.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        mNightModePreference.setChecked(!mNightModePreference.isChecked());
                    }
                });
                fragment.show(getFragmentManager());
                //返回false表示不改变preference的值
                return false;
            }
        });

        setSummary(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY);
        setSummary(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY);
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
            case Constant.PREF_KEY_NIGHT_MODE:
                AppCompatDelegate.setDefaultNightMode(mDataManager.getPreferenceHelper().getNightModeValue() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                //返回并重新创建HomeActivity
                HomeActivity.start(getActivity());
                break;
            case Constant.PREF_KEY_DRAWER_HEADER_DISPLAY:
                setSummary(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY);
                break;
            case Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY:
                setSummary(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY);
                break;
        }
    }

    private void setSummary(String key) {
        PreferenceHelper preferenceHelper = mDataManager.getPreferenceHelper();
        switch (key) {
            case Constant.PREF_KEY_DRAWER_HEADER_DISPLAY:
                mDrawerHeaderDisplayPreference.setSummary(mDrawerHeaderDisplayPreference.getEntries()[mDrawerHeaderDisplayPreference.findIndexOfValue(preferenceHelper.getDrawerHeaderDisplayValue())]);
                break;
            case Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY:
                mTypeListItemEndDisplayPreference.setSummary(mTypeListItemEndDisplayPreference.getEntries()[mTypeListItemEndDisplayPreference.findIndexOfValue(preferenceHelper.getTypeListItemEndDisplayValue())]);
                break;
        }
    }
}
