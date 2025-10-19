package com.example.animallover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animallover.data.model.CommunityPost
import com.example.animallover.databinding.FragmentCommunityPostBinding
import com.example.animallover.ui.adapters.CommunityPostAdapter

class CommunityPostFragment : Fragment() {

    private var _binding: FragmentCommunityPostBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommunityPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.addPostButton.setOnClickListener {
            findNavController().navigate(R.id.action_communityFragment_to_addPostFragment)
        }
        setupCommunityPostsRecyclerView()
    }

    private fun setupCommunityPostsRecyclerView() {
        val samplePosts = createSamplePosts()
        val adapter = CommunityPostAdapter(samplePosts.toMutableList())
        binding.communityPostsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.communityPostsRecyclerView.adapter = adapter
    }

    private fun createSamplePosts(): List<CommunityPost> {
        return mutableListOf(
            CommunityPost(
                id = "1",
                userName = "Alex",
                userProfileImageUrl = "https://i.pravatar.cc/150?u=alex",
                postText = "Just adopted this little guy! Everyone, meet Sparky!",
                postImageUrl = "https://images.dog.ceo/breeds/retriever-golden/n02099601_1125.jpg",
                likes = 150,
                comments = 12
            ),
            CommunityPost(
                id = "2",
                userName = "Maria",
                userProfileImageUrl = "https://i.pravatar.cc/150?u=maria",
                postText = "My cat, Luna, enjoying the afternoon sun. Isn't she lovely?",
                postImageUrl = "https://cdn2.thecatapi.com/images/5v1.jpg",
                likes = 230,
                comments = 25
            ),
            CommunityPost(
                id = "3",
                userName = "Sam",
                userProfileImageUrl = "https://i.pravatar.cc/150?u=sam",
                postText = "Found a new trail for our weekend hike. The dogs loved it!",
                postImageUrl = "https://images.dog.ceo/breeds/collie-border/n02106166_3639.jpg",
                likes = 180,
                comments = 18
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
