package net.vrgsoft.vrgsofttest

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.vrgsoft.redditclient.RedditApi
import net.vrgsoft.redditclient.WebClientBuilder
import net.vrgsoft.redditclient.model.RedditResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var redditApi: RedditApi
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView

    private var after: String? = null

    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupSpinner()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        postAdapter = PostAdapter(mutableListOf())
        recyclerView.adapter = postAdapter

        redditApi = WebClientBuilder.getClient()

        fetchTopPosts("day", 2)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!loading && dy > 0 && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) {
                    loading = true
                    fetchTopPosts("day", 2)

                }
            }
        })
    }

    private fun setupSpinner() {
        val timePeriodSpinner: Spinner = findViewById(R.id.timePeriodSpinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.time_periods,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            timePeriodSpinner.adapter = adapter
        }

        timePeriodSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedTimePeriod = parent.getItemAtPosition(position).toString()

                postAdapter.clearPosts()
                Log.d("MainActivity", selectedTimePeriod)
                fetchTopPosts(selectedTimePeriod, 2)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }


    private fun fetchTopPosts(timePeriod: String, limit: Int) {
        redditApi.getTopPosts(timePeriod, limit, after).enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val redditResponse = response.body()
                    if (redditResponse != null) {
                        for (post in redditResponse.data.children) {
                            Log.d("MainActivity", "Title: ${post.data.title}, Comments: ${post.data.num_comments}")
                        }
                        after = redditResponse.data.after

                        val newPosts = redditResponse.data.children
                        postAdapter.addPosts(newPosts) // Добавляем посты, а не заменяем
                        loading = false
                    }
                } else {
                    Log.e("MainActivity", "Request failed")
                }
            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.e("MainActivity", "Error: ${t.message}")
            }
        })
    }
}