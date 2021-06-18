package fr.macaron_dev.hyperion.ui.about

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.activity.KaamelottActivity

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_about, container, false)
        val logo = root.findViewById<ImageView>(R.id.logo)
        val copyright = root.findViewById<TextView>(R.id.copyright)
        copyright.setOnClickListener {
            val builder = AlertDialog.Builder(root.context)
            builder.setTitle("Étalon du Cul")
            builder.setMessage("Étalon du Cul veut te dire bonjour")
            builder.create().show()
        }
        logo.setOnClickListener {
            val intent = Intent(context, KaamelottActivity::class.java)
            startActivity(intent)
        }
        return root
    }
}