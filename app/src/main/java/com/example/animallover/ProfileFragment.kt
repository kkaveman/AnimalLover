package com.example.animallover

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import androidx.navigation.fragment.findNavController
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class ProfileFragment : Fragment() {
    private lateinit var firebaseAuth: FirebaseAuth
    private var _binding : com.example.animallover.databinding.FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = com.example.animallover.databinding.FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize ProgressDialog
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Loading profile...")
        progressDialog.setCancelable(false)

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        // Load user profile data
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
            return
        }

        progressDialog.show()

        val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(currentUser.uid)

        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                progressDialog.dismiss()

                if (snapshot.exists()) {
                    // Get user data - Note: posts, followers, following are stored as Int in Firebase
                    val username = snapshot.child("username").getValue(String::class.java) ?: "unknown"
                    val bio = snapshot.child("bio").getValue(String::class.java) ?: "No bio"
                    val posts_count = snapshot.child("posts").getValue(Int::class.java) ?: 0
                    val followers = snapshot.child("followers").getValue(Int::class.java) ?: 0
                    val following = snapshot.child("following").getValue(Int::class.java) ?: 0

                    // Display data
                    binding.username.text = username
                    binding.bio.text = bio
                    binding.postsCount.text = posts_count.toString()
                    binding.followersCount.text = followers.toString()
                    binding.followingCount.text = following.toString()

                    // Load profile image
//                    if (!imageUrl.isNullOrEmpty()) {
//                        Glide.with(requireContext())
//                            .load(imageUrl)
//                            .placeholder(R.drawable.cat_icon)
//                            .error(R.drawable.cat_icon)
//                            .into(binding.profileImage)
//                    } else {
//                        binding.profileImage.setImageResource(R.drawable.cat_icon)
//                    }

                } else {
                    Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to load profile: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}