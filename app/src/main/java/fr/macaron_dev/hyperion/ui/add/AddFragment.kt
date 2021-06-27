package fr.macaron_dev.hyperion.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddFragment: Fragment(){

    private lateinit var root: View
    private lateinit var dbHelper: HyperionDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_add, container, false)
        dbHelper = HyperionDbHelper(root.context)
        val name = root.findViewById<EditText>(R.id.add_name)
        val desc = root.findViewById<EditText>(R.id.add_desc)
        val start = root.findViewById<EditText>(R.id.add_start)
        val duration = root.findViewById<EditText>(R.id.add_duration)
        val rna = root.findViewById<EditText>(R.id.add_rna)
        val valid = root.findViewById<Button>(R.id.add_valid)

        valid.setOnClickListener {
            val date = start.text.toString().split("/").reversed().joinToString("-")
            CoroutineScope(Dispatchers.Default).launch {
                if (api.postProject(
                        name.text.toString(),
                        desc.text.toString(),
                        date,
                        Integer.parseInt(duration.text.toString()),
                        rna.text.toString()
                    )
                ) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(root.context, "Projet Créer", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(root.context, "Erreur", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        return root
    }
}