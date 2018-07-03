package net.zackzhang.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*
import net.zackzhang.app.end.R
import net.zackzhang.app.end.model.DataManager
import net.zackzhang.app.end.view.dialog.MessageDialogFragment
import net.zackzhang.app.end.view.fragment.SettingsFragment

class SettingsActivity : BaseActivity() {

    companion object {

        private const val TAG_RESET_SETTINGS = "reset_settings"

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        setupActionBar()

        if (savedInstanceState == null) {
            // 说明 Activity 是新启动的，添加 SettingsFragment；否则 SettingsFragment 会自动恢复，不需要重复添加
            supportFragmentManager.beginTransaction().add(R.id.frame_layout, SettingsFragment()).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)

        if (fragment.tag == TAG_RESET_SETTINGS) {
            (fragment as MessageDialogFragment).okButtonClickListener = { DataManager.preferenceHelper.resetAllValues() }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exit()
            R.id.action_reset -> MessageDialogFragment.Builder()
                    .setTitle(R.string.title_dialog_reset_settings)
                    .setMessage(R.string.msg_dialog_reset_settings)
                    .setOkButtonText(R.string.btn_dialog_reset_settings)
                    .showCancelButton()
                    .show(supportFragmentManager, TAG_RESET_SETTINGS)
        }
        return super.onOptionsItemSelected(item)
    }
}
