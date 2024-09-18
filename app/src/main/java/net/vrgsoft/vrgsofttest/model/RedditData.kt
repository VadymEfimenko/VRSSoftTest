package net.vrgsoft.redditclient.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RedditData(
    val after: String?,
    val children: List<RedditPost>,
) : Parcelable
