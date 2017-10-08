package moe.haruue.test.fontprovider

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main_content.*

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        downloadButton.setOnClickListener {
            val intent = with(Intent(Intent.ACTION_VIEW)) {
                data = Uri.parse("https://github.com/RikkaApps/FontProvider/releases/latest");
                this
            }
            startActivity(intent)
        }
        dialogButton.setOnClickListener {
            val dialog = with(AlertDialog.Builder(this@MainActivity)) {
                setTitle(getString(R.string.it_s_rikka))
                setMessage(R.string.rikka)
                setPositiveButton(R.string.ok) { _, _ -> }
                setNegativeButton(R.string.cancel) { _, _ -> }
                create()
            }
            dialog.show()
        }
        recreateButton.setOnClickListener { recreate() }
    }
}
