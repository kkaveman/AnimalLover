package com.example.animallover

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.*


class EditProfileFragment : Fragment() {

    private var _binding: com.example.animallover.databinding.FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.example.animallover.databinding.FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide UI elements immediately
        hideUIElements()

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Loading Profile")
            setMessage("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        // Load user profile data
        loadUserProfile()

        // Edit Bio button
        binding.tilBio.setEndIconOnClickListener {
            enableBioEditing()
        }

        // Edit Birthdate button
        binding.tilBirthdate.setEndIconOnClickListener {
            showDatePicker()
        }

    }

    override fun onResume() {
        super.onResume()
        // Hide again in onResume to be extra sure
        view?.post {
            hideUIElements()
        }
    }

    private fun hideUIElements() {

        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    private fun showUIElements() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
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
                    // Get user data
                    val username = snapshot.child("username").getValue(String::class.java) ?: "No username"
                    val bio = snapshot.child("bio").getValue(String::class.java) ?: "No bio"
                    val imageUrl = snapshot.child("image").getValue(String::class.java)
                    val birthdate = snapshot.child("birthdate").getValue(String::class.java) ?: "Not set"

                    // Display data
                    binding.etUsername.setText(username)
                    binding.etBio.setText(bio)
                    binding.etBirthdate.setText(birthdate)

                    // Load profile image
                    if (!imageUrl.isNullOrEmpty()) {
                        Glide.with(requireContext())
                            .load(imageUrl)
                            .placeholder(R.drawable.cat_icon)
                            .error(R.drawable.cat_icon)
                            .into(binding.profileImage)
                    } else {
                        binding.profileImage.setImageResource(R.drawable.cat_icon)
                    }
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

    private fun enableBioEditing() {
        val bioEditText = binding.etBio

        if (bioEditText.isEnabled) {
            // Currently in edit mode, save the changes
            saveBio()
        } else {
            // Enable editing
            bioEditText.isEnabled = true
            bioEditText.requestFocus()
            bioEditText.setSelection(bioEditText.text?.length ?: 0)

            // Change icon to a check/save icon
            binding.tilBio.setEndIconDrawable(R.drawable.ic_check)
        }
    }

    private fun saveBio() {
        val currentUser = firebaseAuth.currentUser ?: return
        val newBio = binding.etBio.text.toString().trim()

        if (newBio.isEmpty()) {
            Toast.makeText(requireContext(), "Bio cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        progressDialog.setTitle("Updating Bio")
        progressDialog.show()

        val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(currentUser.uid)

        val updates = HashMap<String, Any>()
        updates["bio"] = newBio

        usersRef.updateChildren(updates)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Bio updated successfully", Toast.LENGTH_SHORT).show()

                // Disable editing and change icon back
                binding.etBio.isEnabled = false
                binding.tilBio.setEndIconDrawable(R.drawable.ic_edit)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to update bio: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Try to parse current birthdate if it exists
        val currentBirthdate = binding.etBirthdate.text.toString()
        if (currentBirthdate != "Not set") {
            try {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val date = sdf.parse(currentBirthdate)
                if (date != null) {
                    calendar.time = date
                }
            } catch (e: Exception) {
                // Use current date if parsing fails
            }
        }

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                // Format the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = sdf.format(selectedDate.time)

                // Update the birthdate
                updateBirthdate(formattedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set max date to today (can't be born in the future)
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updateBirthdate(newBirthdate: String) {
        val currentUser = firebaseAuth.currentUser ?: return

        progressDialog.setTitle("Updating Birthdate")
        progressDialog.show()

        val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app")
            .reference
            .child("Users")
            .child(currentUser.uid)

        val updates = HashMap<String, Any>()
        updates["birthdate"] = newBirthdate

        usersRef.updateChildren(updates)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), "Birthdate updated successfully", Toast.LENGTH_SHORT).show()

                // Update the UI
                binding.etBirthdate.setText(newBirthdate)
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Failed to update birthdate: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        showUIElements()
    }
}