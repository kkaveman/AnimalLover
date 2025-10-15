package com.example.animallover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.data.CommunityPost
import com.example.animallover.ui.adapters.CommunityPostAdapter

class CommunityFragment : Fragment() {

    private lateinit var communityPostsRecyclerView: RecyclerView
    private lateinit var communityPostAdapter: CommunityPostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_community, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        communityPostsRecyclerView = view.findViewById(R.id.communityPostsRecyclerView)
        setupCommunityPostsRecyclerView()
    }

    private fun setupCommunityPostsRecyclerView() {
        // Same dummy data as in HomeFragment's popular section
        val communityPosts = listOf(
            CommunityPost("1", "CatLover123", "", "Just adopted this little fella!", "https://i.imgur.com/4qZzW8T.jpeg", 150, 23),
            CommunityPost("2", "CrazyCatLady", "", "My cat does the funniest things.", "https://i.imgur.com/SO39L6s.jpeg", 230, 45),
            CommunityPost("3", "MeowMix", "", "Does anyone else's cat do this?", "https://i.imgur.com/gA3tF5C.jpeg", 180, 30)
        )

        communityPostAdapter = CommunityPostAdapter(communityPosts)
        communityPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        communityPostsRecyclerView.adapter = communityPostAdapter
    }
}