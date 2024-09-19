package net.vrgsoft.vrgsofttest

import net.vrgsoft.vrgsofttest.repository.PostRepository
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.vrgsoft.vrgsofttest.utils.RedditApi
import net.vrgsoft.vrgsofttest.utils.WebClientBuilder
import net.vrgsoft.redditclient.model.RedditPost
import net.vrgsoft.vrgsofttest.utils.Constants

class MainActivity : AppCompatActivity() {

    private lateinit var redditApi: RedditApi
    private lateinit var postAdapter: PostAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var postRepository: PostRepository

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
        postRepository = PostRepository(redditApi)
        setupRecyclerViewScrollListener()

        if (savedInstanceState == null) {
            fetchTopPosts(Constants.DEFAULT_TIME_PERIOD, Constants.DEFAULT_POST_LIMIT)
        }
    }

    private fun fetchTopPosts(timePeriod: String, limit: Int) {
        postRepository.fetchTopPosts(timePeriod, limit, after) { newPosts, nextAfter ->
            runOnUiThread {
                postAdapter.addPosts(newPosts)
                after = nextAfter
                loading = false
            }
        }
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
                    fetchTopPosts(Constants.DEFAULT_TIME_PERIOD, Constants.DEFAULT_POST_LIMIT)
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