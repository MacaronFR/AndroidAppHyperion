package fr.macaron_dev.hyperion.activity

import android.app.Dialog
import android.os.Bundle
import android.provider.BaseColumns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import fr.macaron_dev.hyperion.*
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import fr.macaron_dev.hyperion.ui.dialog.ContributeDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException

class ProjectDetailActivity: AppCompatActivity(), ContributeDialog.ContributeDialogListener {

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.project_detail_activity)
        project = intent.getSerializableExtra("project") as Project
        val db = HyperionDbHelper(applicationContext).readableDatabase
        val logo = findViewById<ImageView>(R.id.ProjectDetailLogo)
        val textName = findViewById<TextView>(R.id.ProjectDetailName)
        val textDesc = findViewById<TextView>(R.id.ProjectDetailDesc)
        val textLeft = findViewById<TextView>(R.id.ProjectDetailLeft)
        val textContrib = findViewById<TextView>(R.id.ProjectDetailContribution)
        val contributeButton = findViewById<Button>(R.id.button_contribute)
        contributeButton.setOnClickListener(contributeListener)

        logo.setImageBitmap(retrieveLogo(project.logo, db)?.let {
            try {
                b64ToBitmap(it)
            }catch (e: InvalidParameterException){
                null
            }
        })

        with(project) {
            textName.text = name
            textDesc.text = description
            textLeft.text = getString(R.string.days_left, left)
            textContrib.text = getString(R.string.contrib, contribution)
        }
    }

    private val contributeListener = View.OnClickListener {
        val dial = ContributeDialog()
        dial.show(supportFragmentManager, null)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        val amount = dialog.dialog?.findViewById<EditText>(R.id.amount)?.text?.toString()?.toInt() ?: return
        CoroutineScope(Dispatchers.Default).launch{
            if(api.postContribute(project.id, amount)){
                withContext(Dispatchers.Main){
                    val textContrib = findViewById<TextView>(R.id.ProjectDetailContribution)
                    textContrib.text = getString(R.string.contrib, amount + project.contribution)
                }
            }else{
                withContext(Dispatchers.Main){
                    Toast.makeText(applicationContext, "Erreur", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }
}