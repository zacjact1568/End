package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_settings.*
import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.view.dialog.MessageDialogFragment
import me.imzack.app.end.view.fragment.SettingsFragment

class SettingsActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setSupportActionBar(toolbar)
        setupActionBar()

        fragmentManager.beginTransaction().add(R.id.frame_layout, SettingsFragment()).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> exit()
            R.id.action_reset -> MessageDialogFragment.newInstance(
                    getString(R.string.msg_dialog_reset_settings),
                    getString(R.string.title_dialog_reset_settings),
                    getString(R.string.btn_dialog_reset_settings),
                    { DataManager.preferenceHelper.resetAllValues() }
            ).show(supportFragmentManager)
        }
        return super.onOptionsItemSelected(item)
    }
}
