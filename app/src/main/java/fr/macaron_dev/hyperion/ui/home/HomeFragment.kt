package fr.macaron_dev.hyperion.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import fr.macaron_dev.hyperion.Project
import fr.macaron_dev.hyperion.ProjectAdapter
import fr.macaron_dev.hyperion.R
import fr.macaron_dev.hyperion.api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class HomeFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var root: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
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
        return root
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (id) {
            0L -> {
                CoroutineScope(Dispatchers.Default).launch {
                    val project = api.getLatestProject()
                    if ((project[0] is JSONObject)) {
                        val projects = mutableListOf<Project>()
                        for (i in 0 until project.length()) {
                            println((project[i] as JSONObject).get("contribution"))
                            val contrib: Int = if((project[i] as JSONObject).getString("contribution").equals("null")){
                                0
                            }else{
                                (project[i] as JSONObject).getInt("contribution")
                            }
                            projects.add(
                                Project(
                                    (project[i] as JSONObject).getJSONObject("logo").getString("content"),
                                    (project[i] as JSONObject).getString("name"),
                                    (project[i] as JSONObject).getString("description"),
                                    (project[i] as JSONObject).getString("start"),
                                    (project[i] as JSONObject).getString("duration"),
                                    contrib
                                )
                            )
                        }
                        withContext(Dispatchers.Main) {
                            val recycler = root.findViewById<RecyclerView>(R.id.recyclerProject)
                            recycler.apply {
                                adapter = ProjectAdapter(projects)
                                addItemDecoration(DividerItemDecoration(root.context, DividerItemDecoration.VERTICAL))
                            }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(root.context, "NIKKK", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            1L -> {

            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }
}