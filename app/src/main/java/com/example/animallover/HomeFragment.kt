package com.example.animallover

import android.graphics.Shader
import android.graphics.LinearGradient
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.doOnLayout
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.animallover.data.model.CommunityPost
import com.example.animallover.data.model.Event
import com.example.animallover.databinding.FragmentHomeBinding
import com.example.animallover.ui.adapters.CommunityPostAdapter
import com.example.animallover.ui.adapters.EventAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        loadUserProfile()
        applyGradientToTitle()
        setupClickListeners()
        setupEventsRecyclerView()
        setupPopularPostsRecyclerView()
    }

    private fun setupPopularPostsRecyclerView() {
        val popularPosts = listOf(
            CommunityPost("1", "CatLover123", "", "Just adopted this little fella!", "https://i.imgur.com/4qZzW8T.jpeg", 150, 23),
            CommunityPost("2", "CrazyCatLady", "", "My cat does the funniest things.", "https://i.imgur.com/SO39L6s.jpeg", 230, 45),
            CommunityPost("3", "MeowMix", "", "Does anyone else's cat do this?", "https://i.imgur.com/gA3tF5C.jpeg", 180, 30)
        )

        val adapter = CommunityPostAdapter(popularPosts.toMutableList())
        binding.popularPostsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.popularPostsRecyclerView.adapter = adapter
        binding.popularPostsRecyclerView.isNestedScrollingEnabled = false // Important for smooth scrolling inside NestedScrollView
    }

    private fun setupEventsRecyclerView() {
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val events = listOf(
            Event(R.drawable.ai_cat, "Cat Show 2024"),
            Event(R.drawable.cat_event_placeholder, "Adoption Day"),
            Event(R.drawable.cat_event_placeholder, "Feline Health Webinar"),
            Event(R.drawable.cat_event_placeholder, "Cute Cat Contest"),
            Event(R.drawable.cat_event_placeholder, "Community Meetup")
        )

        val eventAdapter = EventAdapter(events.take(3)) {
            findNavController().navigate(R.id.action_homeFragment_to_eventFragment)
        }
        binding.eventsRecyclerView.adapter = eventAdapter
    }

    private fun setupClickListeners() {
        binding.userProfile.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            } else {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
    }

    private fun applyGradientToTitle() {
        val tvTitle = binding.tvTitle
        val startColor = ContextCompat.getColor(requireContext(), R.color.header_gradient_start)
        val endColor = ContextCompat.getColor(requireContext(), R.color.header_gradient_end)

        tvTitle.doOnLayout { view ->
            val width = view.width.toFloat()
            if (width > 0) {
                val shader = LinearGradient(0f, 0f, width, 0f, intArrayOf(startColor, endColor), null, Shader.TileMode.CLAMP)
                tvTitle.paint.shader = shader
                tvTitle.invalidate() // Redraw the view with the shader
            }
        }
    }

    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users").child(currentUser.uid)
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    if (snapshot.exists()){
                        val imageUrl = snapshot.child("image").getValue(String::class.java)
                        val username = snapshot.child("username").getValue(String::class.java)

                        binding.tvSubtitle.setText("Hello $username!")

                        if (!imageUrl.isNullOrEmpty()) {
                            Glide.with(this@HomeFragment)
                                .load(imageUrl)
                                .placeholder(R.drawable.cat_icon)
                                .error(R.drawable.cat_icon)
                                .into(binding.userProfile)
                        } else {
                            binding.userProfile.setImageResource(R.drawable.cat_icon)
                        }

                    }

                }

                override fun onCancelled(error: DatabaseError) {
                    binding.userProfile.setImageResource(R.drawable.cat_icon)
                }
            })
        } else {
            binding.userProfile.setImageResource(R.drawable.cat_icon)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to avoid memory leaks
    }
}
