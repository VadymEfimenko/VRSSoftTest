package net.vrgsoft.redditclient


import net.vrgsoft.redditclient.model.RedditResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RedditApi {

    @GET("/top.json")
    fun getTopPosts(
        @Query("t") timePeriod: String,
        @Query("limit") limit: Int,
        @Query("after") after: String? = null
    ): Call<RedditResponse>


}
