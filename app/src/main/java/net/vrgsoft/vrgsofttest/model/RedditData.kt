package net.vrgsoft.redditclient.model

data class RedditData(
    val after: String?,
    val dist: Int,
    val children: List<RedditPost>,
    val before: String?
)
