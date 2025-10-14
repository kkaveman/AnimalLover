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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userProfileImageView: ShapeableImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()
        userProfileImageView = view.findViewById(R.id.user_profile)

        loadUserProfileImage()

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)

        // Get the gradient colors
        val startColor = ContextCompat.getColor(requireContext(), R.color.header_gradient_start)
        val endColor = ContextCompat.getColor(requireContext(), R.color.header_gradient_end)

        // 🔥 Apply gradient shader ONLY after layout is complete
        tvTitle.doOnLayout { view ->
            val width = view.width
            val height = view.height

            if (width > 0 && height > 0) {
                // Create a horizontal gradient (left to right)
                val shader = LinearGradient(
                    0f, 0f,                          // Start: top-left corner
                    width.toFloat(), 0f,             // End: top-right corner
                    intArrayOf(startColor, endColor), // Colors to blend
                    null,                            // Positions (null = evenly spaced)
                    Shader.TileMode.CLAMP            // How to tile beyond edges
                )

                // Apply the shader to the text paint
                tvTitle.paint.shader = shader

                // Optional: Debug log to confirm it worked
                println("✅ Gradient applied to tvTitle: width=$width, height=$height")
            } else {
                println("⚠️ tvTitle width is 0 — layout not ready yet.")
            }
        }

        userProfileImageView.setOnClickListener {
            if (firebaseAuth.currentUser != null) {
                // User is signed in, navigate to profile
                findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
            } else {
                // User is not signed in, navigate to register
                findNavController().navigate(R.id.action_homeFragment_to_registerFragment)
            }
        }

        // Setup Events RecyclerView
        val eventsRecyclerView = view.findViewById<RecyclerView>(R.id.eventsRecyclerView)
        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        eventsRecyclerView.isNestedScrollingEnabled = false // Fix for scrolling issue

        val events = listOf(
            Event(R.drawable.ai_cat, "Cat Show 2024"),
            Event(R.drawable.cat_event_placeholder, "Adoption Day"),
            Event(R.drawable.cat_event_placeholder, "Feline Health Webinar"),
            Event(R.drawable.cat_event_placeholder, "Cute Cat Contest"),
            Event(R.drawable.cat_event_placeholder, "Community Meetup")
        )

        // Pass the list of events (max 3) and the navigation action to the adapter.
        val eventAdapter = EventAdapter(events.take(3)) {
            // This is the lambda that gets called when the "See All" button is clicked.
            findNavController().navigate(R.id.action_homeFragment_to_eventFragment)
        }
        eventsRecyclerView.adapter = eventAdapter
    }

    private fun loadUserProfileImage() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users").child(currentUser.uid)
            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.child("image").getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(this@HomeFragment)
                            .load(imageUrl)
                            .placeholder(R.drawable.user_2) // Optional: a placeholder while loading
                            .error(R.drawable.user_2) // Optional: an error image
                            .into(userProfileImageView)
                    } else {
                        // No image URL in database, load local drawable
                        userProfileImageView.setImageResource(R.drawable.user_2)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Failed to read value, so load default
                    userProfileImageView.setImageResource(R.drawable.user_2)
                }
            })
        } else {
            // User is not logged in, load local drawable
            userProfileImageView.setImageResource(R.drawable.user_2)
        }
    }
}
