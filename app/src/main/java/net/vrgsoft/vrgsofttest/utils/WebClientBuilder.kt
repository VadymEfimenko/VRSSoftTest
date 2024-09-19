package net.vrgsoft.vrgsofttest.utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WebClientBuilder {

    private const val BASE_URL = "https://www.reddit.com"
    private var redditApi: RedditApi? = null

    fun getClient(): RedditApi {
        if (redditApi == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            redditApi = retrofit.create(RedditApi::class.java)
        }
        return redditApi!!
    }
}