package me.imzack.app.ender.view.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;

import me.imzack.app.ender.R;
import me.imzack.app.ender.common.Constant;
import me.imzack.app.ender.model.DataManager;
import me.imzack.app.ender.model.preference.PreferenceHelper;
import me.imzack.app.ender.view.activity.HomeActivity;
import me.imzack.app.ender.view.dialog.BaseDialogFragment;
import me.imzack.app.ender.view.dialog.MessageDialogFragment;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DataManager mDataManager;
    private SwitchPreference mNightModePreference;
    private ListPreference mDrawerHeaderDisplayPreference;
    private ListPreference mTypeListItemEndDisplayPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        mDataManager = DataManager.getInstance();

        mNightModePreference = (SwitchPreference) findPreference(Constant.PREF_KEY_NIGHT_MODE);
        mDrawerHeaderDisplayPreference = (ListPreference) findPreference(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY);
        mTypeListItemEndDisplayPreference = (ListPreference) findPreference(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY);

        mNightModePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                new MessageDialogFragment.Builder()
                        .setMessage(R.string.msg_dialog_switch_night_mode)
                        .setTitle(R.string.title_dialog_switch_night_mode)
                        .setNegativeButton(R.string.button_cancel, null)
                        .setPositiveButton(R.string.button_restart, new BaseDialogFragment.OnButtonClickListener() {
                            @Override
                            public boolean onClick() {
                                mNightModePreference.setChecked(!mNightModePreference.isChecked());
                                return true;
                            }
                        })
                        //这里需要使用宿主activity的support包中的FragmentManager
                        .show(((FragmentActivity) getActivity()).getSupportFragmentManager());
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
