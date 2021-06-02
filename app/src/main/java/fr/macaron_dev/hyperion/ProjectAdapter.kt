package fr.macaron_dev.hyperion

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*

class ProjectAdapter(private val projects: MutableList<Project>): RecyclerView.Adapter<ProjectAdapter.ViewHolder>() {
    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view){
        private val projectLogo = view.findViewById<ImageView>(R.id.projectLogo)
        private val projectName = view.findViewById<TextView>(R.id.projectName)
        private val projectDesc = view.findViewById<TextView>(R.id.projectDesc)
        private val projectLeft = view.findViewById<TextView>(R.id.projectLeft)
        private val projectContrib = view.findViewById<TextView>(R.id.projectContrib)
        private var currentProject: Project? = null

        fun bind(project: Project){
            var date = LocalDate.parse(project.start)
            date = date.plusDays(project.duration.toLong())
            currentProject = project
            val bytes = Base64.decode(project.logo, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            println(bitmap)
            projectLogo.setImageBitmap(bitmap)
            projectName.text = project.name
            projectDesc.text = project.description
            projectLeft.text =  view.resources.getString(R.string.days_left, ChronoUnit.DAYS.between(LocalDate.now(), date))
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