package me.imzack.app.end.view.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDelegate

import me.imzack.app.end.R
import me.imzack.app.end.common.Constant
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.view.activity.HomeActivity
import me.imzack.app.end.view.dialog.BaseDialogFragment
import me.imzack.app.end.view.dialog.MessageDialogFragment

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mNightModePreference: SwitchPreference
    private lateinit var mDrawerHeaderDisplayPreference: ListPreference
    private lateinit var mTypeListItemEndDisplayPreference: ListPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)

        mNightModePreference = findPreference(Constant.PREF_KEY_NIGHT_MODE) as SwitchPreference
        mDrawerHeaderDisplayPreference = findPreference(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY) as ListPreference
        mTypeListItemEndDisplayPreference = findPreference(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY) as ListPreference

        mNightModePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, _ ->
            MessageDialogFragment.Builder()
                    .setMessage(R.string.msg_dialog_switch_night_mode)
                    .setTitle(R.string.title_dialog_switch_night_mode)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.button_restart, object : BaseDialogFragment.OnButtonClickListener {
                        override fun onClick(): Boolean {
                            mNightModePreference.isChecked = !mNightModePreference.isChecked
                            return true
                        }
                    })
                    //这里需要使用宿主activity的support包中的FragmentManager
                    .show((activity as FragmentActivity).supportFragmentManager)
            //返回false表示不改变preference的值
            false
        }

        setSummary(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY)
        setSummary(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            Constant.PREF_KEY_NIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(if (DataManager.preferenceHelper.nightModeValue) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                //返回并重新创建HomeActivity
                HomeActivity.start(activity)
            }
            Constant.PREF_KEY_DRAWER_HEADER_DISPLAY -> setSummary(Constant.PREF_KEY_DRAWER_HEADER_DISPLAY)
            Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY -> setSummary(Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY)
        }
    }

    private fun setSummary(key: String) {
        val preferenceHelper = DataManager.preferenceHelper
        when (key) {
            Constant.PREF_KEY_DRAWER_HEADER_DISPLAY -> mDrawerHeaderDisplayPreference.summary = mDrawerHeaderDisplayPreference.entries[mDrawerHeaderDisplayPreference.findIndexOfValue(preferenceHelper.drawerHeaderDisplayValue)]
            Constant.PREF_KEY_TYPE_LIST_ITEM_END_DISPLAY -> mTypeListItemEndDisplayPreference.summary = mTypeListItemEndDisplayPreference.entries[mTypeListItemEndDisplayPreference.findIndexOfValue(preferenceHelper.typeListItemEndDisplayValue)]
        }
    }
}
