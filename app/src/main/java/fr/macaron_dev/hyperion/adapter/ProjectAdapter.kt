package fr.macaron_dev.hyperion.adapter

import android.content.ContentValues
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.b64ToBitmap
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.InvalidParameterException

class ProjectAdapter(private val projects: MutableList<Project>, private val dbHelper: HyperionDbHelper, private val onClick: (Project) -> Unit): RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    inner class ViewHolder(private val view: View, val onClick: (Project) -> Unit): RecyclerView.ViewHolder(view){
        private val projectLogo = view.findViewById<ImageView>(R.id.projectLogo)
        private val projectName = view.findViewById<TextView>(R.id.projectName)
        private val projectDesc = view.findViewById<TextView>(R.id.projectDesc)
        private val projectLeft = view.findViewById<TextView>(R.id.projectLeft)
        private val projectContrib = view.findViewById<TextView>(R.id.projectContrib)
        private var currentProject: Project? = null

        init {
            itemView.setOnClickListener {
                currentProject?.let{
                    onClick(it)
                }
            }
        }

        fun bind(project: Project){
            val dbW = dbHelper.writableDatabase
            val dbR = dbHelper.readableDatabase
            currentProject = project
            val projection = arrayOf(Hyperion.Logo.COLUMN_NAME_CONTENT, BaseColumns._ID)
            val selection = "${Hyperion.Logo.COLUMN_NAME_ID} = ?"
            val selectionArgs = arrayOf(project.logo.toString())

            val cursor = dbR.query(
                Hyperion.Logo.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
            )
            if(cursor.count > 0){
                cursor.moveToNext()
                try {
                    val b64 = cursor.getString(cursor.getColumnIndexOrThrow(Hyperion.Logo.COLUMN_NAME_CONTENT))
                    projectLogo.setImageBitmap(b64ToBitmap(b64))
                }catch (e: InvalidParameterException){}
            }else {
                CoroutineScope(Dispatchers.Default).launch {
                    val logo = api.getLogo(project.id)
                    if (logo.has("status")) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(view.context, "Error", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val value = ContentValues().apply {
                            put(Hyperion.Logo.COLUMN_NAME_ID, project.logo)
                            put(Hyperion.Logo.COLUMN_NAME_CONTENT, logo.getString("content"))
                        }
                        dbW.insert(Hyperion.Logo.TABLE_NAME, null, value)
                        val b64 = logo.getString("content")
                        withContext(Dispatchers.Main) {
                            try {
                                projectLogo.setImageBitmap(b64ToBitmap(b64))
                            }catch (e: InvalidParameterException){}
                        }
                    }
                }
            }
            cursor.close()
            projectName.text = project.name
            projectDesc.text = project.description
            projectLeft.text =  view.resources.getString(R.string.days_left, project.left)
            projectContrib.text = view.resources.getString(R.string.contrib, project.contribution)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_project, parent, false)
        return ViewHolder(view, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size
}