package fr.macaron_dev.hyperion.ui.project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.activity.ProjectDetailActivity
import fr.macaron_dev.hyperion.adapter.ProjectAdapter
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class ProjectFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var root: View
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var dbHelper: HyperionDbHelper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_project, container, false)
        dbHelper = HyperionDbHelper(root.context);
        return root;
    }

    override fun onResume() {
        super.onResume()
        swipe = root.findViewById(R.id.swipeProject)
        swipe.setOnRefreshListener(this)
        CoroutineScope(Dispatchers.Default).launch{
            val project = api.getLatestProject()
            prepareAndDisplayRecycler(project);
        }
    }

    private suspend fun prepareAndDisplayRecycler(project: JSONArray){
        if ((project[0] is JSONObject)) {
            val projects = mutableListOf<Project>()
            for (i in 0 until project.length()) {
                with(project[i] as JSONObject) {
                    println(get("contribution"))
                    val contrib = if (this.getString("contribution").equals("null")) {
                        0
                    } else {
                        this.getInt("contribution")
                    }
                    var date = LocalDate.parse(this.getString("start"))
                    date = date.plusDays(this.getString("duration").toLong())
                    val diff = ChronoUnit.DAYS.between(LocalDate.now(), date)
                    projects.add(
                        Project(
                            this.getInt("id"),
                            this.getInt("logo"),
                            this.getString("name"),
                            this.getString("description"),
                            diff.toInt(),
                            contrib
                        )
                    )
                }
            }
            withContext(Dispatchers.Main) {
                root.findViewById<ProgressBar>(R.id.project_spinner).visibility = View.GONE
                root.findViewById<RecyclerView>(R.id.project_recycler).apply {
                    adapter = ProjectAdapter(projects, dbHelper){project -> adapterOnClick(project)}
                    addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
                    swipe.isRefreshing = false
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(root.context, "Error", Toast.LENGTH_SHORT).show()
                swipe.isRefreshing = false
            }
        }
    }

    private fun adapterOnClick(project: Project){
        val intent = Intent(root.context, ProjectDetailActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    override fun onRefresh() {
        CoroutineScope(Dispatchers.Default).launch{
            val project = api.getLatestProject()
            prepareAndDisplayRecycler(project);
        }
    }
}