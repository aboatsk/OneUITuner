package tk.zwander.oneuituner.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.samsungthemelib.util.mainHandler
import com.samsungthemelib.util.needsThemeCenter
import tk.zwander.deviceowner.sdk.actionManager
import tk.zwander.oneuituner.R
import tk.zwander.oneuituner.util.*

class Main : Base() {
    override val title = R.string.app_name

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main, rootKey)

        with(findPreference(PrefManager.USE_SYNERGY)) {
            onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { pref, newValue ->
                    val enabled = newValue.toString().toBoolean()

                    if (enabled && !context!!.isSynergyInstalled) {
                        mainHandler.post {
                            (pref as SwitchPreference).isChecked = false
                        }

                        AlertDialog.Builder(context!!)
                            .setTitle(R.string.use_synergy)
                            .setMessage(R.string.synergy_not_installed_desc)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                val uri = Uri.parse("https://play.google.com/store/apps/details?id=projekt.samsung.theme.compiler")
                                val listingIntent = Intent(Intent.ACTION_VIEW)

                                listingIntent.`package` = "com.android.vending"
                                listingIntent.data = uri
                                listingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                                startActivity(listingIntent)
                            }
                            .setNegativeButton(R.string.no, null)
                            .show()
                    }
                    true
                }
        }

        with(findPreference(PrefManager.FORCE_NORMAL_INSTALL)) {
            isVisible = needsThemeCenter
        }

        with(findPreference("push_to_fwk")) {
            setOnPreferenceClickListener {
//                context!!.themeLibApp.ipcReceiver.postIPCAction {
//                    it.copyAndApplyThemes()
//                }

                true
            }
        }

        with(findPreference("enable_theme_center") as SwitchPreference) {
            val manager = context!!.actionManager
            val hidden = manager.isApplicationHidden("com.samsung.android.themecenter")

            isChecked = hidden != true

            setOnPreferenceChangeListener { _, newValue ->
                manager.setApplicationHidden("com.samsung.android.themecenter", !newValue.toString().toBoolean())
                true
            }
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return run {
            val (ret, action) = when(preference?.key) {
                Keys.clock -> true to R.id.action_main_to_clock
                Keys.qs -> true to R.id.action_main_to_qs
                Keys.misc -> true to R.id.action_main_to_misc
                Keys.statusBar -> true to R.id.action_main_to_statusBar
                Keys.lockScreen -> true to R.id.action_main_to_lockScreen
                else -> super.onPreferenceTreeClick(preference) to 0
            }

            if (action != 0) {
                navController.navigate(
                    action,
                    null,
                    navOptions
                )
            }

            ret
        }
    }
}