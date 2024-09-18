package net.vrgsoft.redditclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedditPost(
    val data: RedditPostData
) : Parcelable
