package net.vrgsoft.vrgsofttest

import android.os.Bundle
import android.util.Log
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
        recyclerView.layoutManager = LinearLayoutManager(this)

        redditApi = WebClientBuilder.getClient()

        fetchTopPosts("day", 10)
    }

    private fun fetchTopPosts(timePeriod: String, limit: Int) {
        redditApi.getTopPosts(timePeriod, limit).enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val redditResponse = response.body()
                    if (redditResponse != null) {
                        for (post in redditResponse.data.children) {
                            Log.d("MainActivity", "Title: ${post.data.title}, Comments: ${post.data.num_comments}")
                        }

                        postAdapter = PostAdapter(redditResponse.data.children)
                        recyclerView.adapter = postAdapter
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