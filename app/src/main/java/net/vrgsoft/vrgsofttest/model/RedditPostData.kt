package net.vrgsoft.redditclient.model

data class RedditPostData(
    val subreddit: String,
    val title: String,
    val thumbnail: String?,
    val num_comments: Int,
    val created_utc: Long,
    val url: String
)
