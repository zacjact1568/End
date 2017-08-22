package me.imzack.app.end.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import butterknife.ButterKnife

import me.imzack.app.end.R
import me.imzack.app.end.model.DataManager
import me.imzack.app.end.view.dialog.BaseDialogFragment
import me.imzack.app.end.view.dialog.MessageDialogFragment
import me.imzack.app.end.view.fragment.SettingsFragment

class SettingsActivity : BaseActivity() {

    companion object {

        fun start(context: Context) {
            context.startActivity(Intent(context, SettingsActivity::class.java))
        }
    }

    @BindView(R.id.toolbar)
    lateinit var mToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        ButterKnife.bind(this)

        setSupportActionBar(mToolbar)
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
            R.id.action_reset -> MessageDialogFragment.Builder()
                    .setMessage(R.string.msg_dialog_reset_settings)
                    .setTitle(R.string.title_dialog_reset_settings)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.btn_dialog_reset_settings, object : BaseDialogFragment.OnButtonClickListener {
                        override fun onClick(): Boolean {
                            DataManager.preferenceHelper.resetAllValues()
                            return true
                        }
                    })
                    .show(supportFragmentManager)
            else -> { }
        }
        return super.onOptionsItemSelected(item)
    }
}
