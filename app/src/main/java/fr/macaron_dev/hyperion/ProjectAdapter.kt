package fr.macaron_dev.hyperion

import android.content.ContentValues
import android.graphics.BitmapFactory
import android.provider.BaseColumns
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProjectAdapter(private val projects: MutableList<Project>, private val dbHelper: HyperionDbHelper): RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    inner class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val projectLogo = view.findViewById<ImageView>(R.id.projectLogo)
        private val projectName = view.findViewById<TextView>(R.id.projectName)
        private val projectDesc = view.findViewById<TextView>(R.id.projectDesc)
        private val projectLeft = view.findViewById<TextView>(R.id.projectLeft)
        private val projectContrib = view.findViewById<TextView>(R.id.projectContrib)
        private var currentProject: Project? = null

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
                val bytes = Base64.decode(cursor.getString(cursor.getColumnIndexOrThrow(Hyperion.Logo.COLUMN_NAME_CONTENT)), Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                projectLogo.setImageBitmap(bitmap)
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
                        val bytes = Base64.decode(logo.getString("content"), Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        withContext(Dispatchers.Main) {
                            projectLogo.setImageBitmap(bitmap)
                            Toast.makeText(view.context, "From API", Toast.LENGTH_SHORT).show()
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
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(projects[position])
    }

    override fun getItemCount() = projects.size
}