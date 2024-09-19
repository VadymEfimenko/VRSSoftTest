package net.vrgsoft.vrgsofttest.repository

import android.util.Log
import net.vrgsoft.vrgsofttest.utils.RedditApi
import net.vrgsoft.redditclient.model.RedditPost
import net.vrgsoft.redditclient.model.RedditResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PostRepository(private val redditApi: RedditApi) {

    fun fetchTopPosts(timePeriod: String, limit: Int, after: String?, callback: (List<RedditPost>, String?) -> Unit) {
        redditApi.getTopPosts(timePeriod, limit, after).enqueue(object : Callback<RedditResponse> {
            override fun onResponse(call: Call<RedditResponse>, response: Response<RedditResponse>) {
                if (response.isSuccessful) {
                    val redditResponse = response.body()
                    val newPosts = redditResponse?.data?.children ?: emptyList()
                    val nextAfter = redditResponse?.data?.after
                    callback(newPosts, nextAfter)
                } else {
                    Log.e("Repository", "Request failed")
                    callback(emptyList(), null)
                }
            }

            override fun onFailure(call: Call<RedditResponse>, t: Throwable) {
                Log.e("Repository", "Error: ${t.message}")
                callback(emptyList(), null)
            }
        })
    }
}
