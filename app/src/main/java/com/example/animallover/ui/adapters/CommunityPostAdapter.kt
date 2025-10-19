package com.example.animallover.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animallover.R
import com.example.animallover.data.model.CommunityPost
import com.example.animallover.databinding.ItemCommunityPostBinding

class CommunityPostAdapter(
    private val posts: MutableList<CommunityPost>)
    : RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemCommunityPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount(): Int = posts.size

    fun addPost(post: CommunityPost) {
        posts.add(0, post)
        notifyItemInserted(0)
    }

    inner class PostViewHolder(private val binding: ItemCommunityPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: CommunityPost) {
            binding.tvUserName.text = post.userName
            binding.tvPostContent.text = post.postText
            binding.tvLikeCount.text = post.likes.toString()
            binding.tvCommentCount.text = post.comments.toString()

            // Load user profile image
            Glide.with(itemView.context)
                .load(post.userProfileImageUrl)
                .placeholder(R.drawable.cat_icon)
                .circleCrop()
                .into(binding.ivUserProfile)

            // Load post media if it exists
            if (post.postImageUrl != null) {
                binding.ivPostMedia.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load(post.postImageUrl)
                    .into(binding.ivPostMedia)
            } else {
                binding.ivPostMedia.visibility = View.GONE
            }
        }
    }
}
