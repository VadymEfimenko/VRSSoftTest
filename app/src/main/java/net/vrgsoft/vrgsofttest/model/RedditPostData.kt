package net.vrgsoft.redditclient.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedditPostData(
    val subreddit: String,
    val title: String,
    val thumbnail: String?,
    @SerializedName("num_comments")
    val numComments: Int,
    @SerializedName("created_utc")
    val createdUtc: Long,
    val url: String,
    @SerializedName("is_video")
    val isVideo: Boolean
) : Parcelable
