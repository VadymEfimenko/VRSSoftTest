package net.vrgsoft.vrgsofttest

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import net.vrgsoft.redditclient.model.RedditPost
import java.io.IOException

class PostAdapter(private val postList: MutableList<RedditPost>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.postTitle)
        val comments: TextView = view.findViewById(R.id.postComments)
        val thumbnail: ImageView = view.findViewById(R.id.postThumbnail)
        val author: TextView = view.findViewById(R.id.postAuthor)
        val time: TextView = view.findViewById(R.id.postTime)
        val saveButton: Button = view.findViewById(R.id.myButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    fun addPosts(newPosts: List<RedditPost>) {
        postList.addAll(newPosts)
        notifyDataSetChanged()
    }

    fun clearPosts() {
        postList.clear()
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.title.text = post.data.title
        holder.author.text = "Author: ${post.data.subreddit}"
        holder.comments.text = "${post.data.num_comments} comments"

        val currentTime = System.currentTimeMillis() / 1000
        val timeDiff = currentTime - post.data.created_utc
        val hoursAgo = timeDiff / 3600
        val minutesAgo = (timeDiff % 3600) / 60

        holder.time.text = when {
            hoursAgo > 0 -> "$hoursAgo hours ago"
            else -> "$minutesAgo minutes ago"
        }

        val imageUrl = post.data.url
        if (imageUrl.isNotEmpty()) {
            Picasso.get().load(imageUrl).into(holder.thumbnail)

            holder.thumbnail.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(imageUrl))
                it.context.startActivity(intent)
            }
        }

        holder.saveButton.setOnClickListener {
            if (imageUrl.isNotEmpty()) {
                saveImageToGallery(holder.itemView.context, imageUrl)
            } else {
                Toast.makeText(holder.itemView.context, "Изображение недоступно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    private fun saveImageToGallery(context: Context, imageUrl: String) {
        Picasso.get().load(imageUrl).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                saveBitmapToGallery(context, bitmap)
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
                Toast.makeText(context, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show()
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }
        })
    }

    private fun saveBitmapToGallery(context: Context, bitmap: Bitmap) {
        val filename = "IMG_${System.currentTimeMillis()}.jpg"
        val mimeType = "image/jpeg"
        val relativeLocation = Environment.DIRECTORY_DCIM

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            put(MediaStore.Images.Media.IS_PENDING, 0)
        }

        val contentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            try {
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                Toast.makeText(context, "Image have been saved", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
        }
    }
}
