package net.vrgsoft.vrgsofttest

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.vrgsoft.redditclient.RedditApi
import net.vrgsoft.redditclient.WebClientBuilder
import net.vrgsoft.redditclient.model.RedditPost
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

    private var recyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        postAdapter = PostAdapter(mutableListOf())
        recyclerView.adapter = postAdapter

        redditApi = WebClientBuilder.getClient()
        setupRecyclerViewScrollListener()

        if (savedInstanceState == null) {
            fetchTopPosts("all", 2)
        }


    }

    private fun fetchTopPosts(timePeriod: String, limit: Int) {
        redditApi.getTopPosts(timePeriod, limit, after).enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val redditResponse = response.body()
                    if (redditResponse != null) {
                        after = redditResponse.data.after

                        val newPosts = redditResponse.data.children
                        postAdapter.addPosts(newPosts)
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

    private fun setupRecyclerViewScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!loading && dy > 0 && (firstVisibleItemPosition + visibleItemCount) >= totalItemCount) {
                    loading = true
                    fetchTopPosts("all", 2)
                }
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("posts", ArrayList(postAdapter.getPosts()))
        outState.putString("after", after)
        recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        outState.putParcelable("recycler_state", recyclerViewState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val savedPosts = savedInstanceState.getParcelableArrayList<RedditPost>("posts")
        if (savedPosts != null) {
            postAdapter.setPosts(savedPosts)
        }
        after = savedInstanceState.getString("after")
        recyclerViewState = savedInstanceState.getParcelable<Parcelable>("recycler_state")
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }
}