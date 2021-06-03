package fr.macaron_dev.hyperion.ui.home

import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import fr.macaron_dev.hyperion.data.Project
import fr.macaron_dev.hyperion.adapter.ProjectAdapter
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.activity.ProjectDetailActivity
import fr.macaron_dev.hyperion.api
import fr.macaron_dev.hyperion.database.Hyperion
import fr.macaron_dev.hyperion.database.HyperionDbHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var root: View
    private lateinit var dbHelper: HyperionDbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        root = inflater.inflate(R.layout.fragment_home, container, false)
        val spinner = root.findViewById<Spinner>(R.id.home_spinner)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            root.context,
            R.array.home_spinner,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        dbHelper = HyperionDbHelper(root.context)
        return root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (id) {
            0L -> {
                CoroutineScope(Dispatchers.Default).launch {
                    onSelectLatestProject()
                }
            }
            1L -> {
                CoroutineScope(Dispatchers.Default).launch {
                    onSelectPopularProject()
                }
            }
        }
    }

    private suspend fun onSelectLatestProject(){
        val project = api.getLatestProject()
        prepareAndDisplayRecycler(project)
    }

    private suspend fun onSelectPopularProject() {
        val project = api.getPopularProject()
        prepareAndDisplayRecycler(project)
    }

    private suspend fun prepareAndDisplayRecycler(project: JSONArray){
        if ((project[0] is JSONObject)) {
            val projects = mutableListOf<Project>()
            for (i in 0 until project.length()) {
                with(project[i] as JSONObject) {
                    println(get("contribution"))
                    val contrib: Int = if (this.getString("contribution").equals("null")) {
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
                val recycler = root.findViewById<RecyclerView>(R.id.recyclerProject)
                recycler.apply {
                    adapter = ProjectAdapter(projects, dbHelper){project -> adapterOnClick(project)}
                    addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(root.context, "NIKKK", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun adapterOnClick(project: Project){
        val intent = Intent(root.context, ProjectDetailActivity::class.java)
        intent.putExtra("project", project)
        startActivity(intent)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}