package net.vrgsoft.vrgsofttest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import net.vrgsoft.redditclient.model.RedditPost

class PostAdapter(private val postList: List<RedditPost>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val comments: TextView = view.findViewById(R.id.postComments)
        val thumbnail: ImageView = view.findViewById(R.id.postThumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.title.text = post.data.title
        holder.comments.text = "${post.data.num_comments} comments"

        if (post.data.thumbnail != null && post.data.thumbnail.isNotEmpty()) {
            Picasso.get().load(post.data.thumbnail).into(holder.thumbnail)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}