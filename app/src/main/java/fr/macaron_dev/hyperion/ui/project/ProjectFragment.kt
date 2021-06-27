package fr.macaron_dev.hyperion.ui.project

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
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

class ProjectFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener, View.OnClickListener {

    private lateinit var root: View
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var dbHelper: HyperionDbHelper
    private lateinit var projects: MutableList<Project>
    private var isLoading = false
    private var page = 0

    inner class ScrollListener: RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!isLoading) {
                if (recyclerView.layoutManager !== null && (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == projects.size -1) {
                    isLoading = true
                    CoroutineScope(Dispatchers.Default).launch {
                        page++
                        val search = root.findViewById<SearchView>(R.id.projectSearch).query.let {
                            if(it == ""){
                                null
                            }else{
                                it.toString()
                            }
                        }
                        val res = api.getProject(page, search)
                        if(res[0] is JSONObject) {
                            loadMoreProject(res)
                            withContext(Dispatchers.Main) {
                                recyclerView.adapter?.notifyDataSetChanged()
                                isLoading = false
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_project, container, false)
        dbHelper = HyperionDbHelper(root.context)
        root.findViewById<RecyclerView>(R.id.project_recycler).addOnScrollListener(this.ScrollListener())
        return root
    }

    override fun onResume() {
        super.onResume()
        swipe = root.findViewById(R.id.swipeProject)
        swipe.setOnRefreshListener(this)
        root.findViewById<SearchView>(R.id.projectSearch).setOnQueryTextListener(this)
        //root.findViewById<SearchView>(R.id.projectSearch).setOnSearchClickListener(this)
        CoroutineScope(Dispatchers.Default).launch {
            val project = api.getProject(0, null)
            prepareAndDisplayRecycler(project)
        }
    }

    private suspend fun prepareAndDisplayRecycler(project: JSONArray) {
        if ((project[0] is JSONObject)) {
            projects = mutableListOf()
            loadMoreProject(project)
            withContext(Dispatchers.Main) {
                root.findViewById<ProgressBar>(R.id.project_spinner).visibility = View.GONE
                root.findViewById<RecyclerView>(R.id.project_recycler).apply {
                    adapter = ProjectAdapter(projects, dbHelper) { project -> adapterOnClick(project) }
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

    private fun loadMoreProject(project: JSONArray) {
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
    }

    private fun adapterOnClick(project: Project) {
        val intent = Intent(root.context, ProjectDetailActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    override fun onRefresh() {
        CoroutineScope(Dispatchers.Default).launch {
            val project = api.getProject(0, null)
            prepareAndDisplayRecycler(project)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        isLoading = false
        CoroutineScope(Dispatchers.Default).launch {
            val res = api.getProject(0, query)
            prepareAndDisplayRecycler(res)
        }
        return false
    }

    override fun onQueryTextChange(newText: String?) = onQueryTextSubmit(newText)

    override fun onClick(v: View?) {
        CoroutineScope(Dispatchers.Default).launch {
            val res = api.getProject(0, (v as SearchView).query.toString())
            if(res[0] is JSONObject){
                prepareAndDisplayRecycler(res)
            }
        }
    }

}