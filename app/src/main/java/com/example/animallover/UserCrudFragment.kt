package com.example.animallover

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.data.model.User
import com.example.animallover.ui.adapters.UserAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*

class UserCrudFragment : Fragment() {

    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewEmpty: TextView

    private lateinit var userAdapter: UserAdapter
    private val usersList = mutableListOf<User>()

    private val database = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app")
    private val usersRef = database.reference.child("Users")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_crud, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers)
            progressBar = view.findViewById(R.id.progressBar)
            textViewEmpty = view.findViewById(R.id.textViewEmpty)

            android.util.Log.d("UserCrudFragment", "Views initialized successfully")

            setupRecyclerView()
            loadUsers()
        } catch (e: Exception) {
            android.util.Log.e("UserCrudFragment", "Error in onViewCreated", e)
            Toast.makeText(requireContext(), "Error initializing view: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRecyclerView() {
        android.util.Log.d("UserCrudFragment", "setupRecyclerView called")

        userAdapter = UserAdapter(
            users = usersList,
            onEditClick = { user -> showEditDialog(user) },
            onDeleteClick = { user -> showDeleteConfirmation(user) }
        )

        recyclerViewUsers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            setHasFixedSize(true)
        }

        android.util.Log.d("UserCrudFragment", "RecyclerView setup complete")
    }

    private fun loadUsers() {
        try {
            progressBar.visibility = View.VISIBLE
            textViewEmpty.visibility = View.GONE
            recyclerViewUsers.visibility = View.VISIBLE // Keep visible

            android.util.Log.d("UserCrudFragment", "Loading users from Firebase...")

            usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    android.util.Log.d("UserCrudFragment", "onDataChange called")
                    android.util.Log.d("UserCrudFragment", "Snapshot exists: ${snapshot.exists()}")
                    android.util.Log.d("UserCrudFragment", "Children count: ${snapshot.childrenCount}")

                    usersList.clear()
                    progressBar.visibility = View.GONE

                    if (!snapshot.exists()) {
                        android.util.Log.d("UserCrudFragment", "No data found in Firebase")
                        textViewEmpty.visibility = View.VISIBLE
                        recyclerViewUsers.visibility = View.GONE
                        return
                    }

                    for (userSnapshot in snapshot.children) {
                        try {
                            val username = userSnapshot.child("username").getValue(String::class.java) ?: ""
                            val bio = userSnapshot.child("bio").getValue(String::class.java) ?: ""
                            val uid = userSnapshot.child("uid").getValue(String::class.java) ?: ""

                            if (uid.isNotEmpty() && username.isNotEmpty()) {
                                val user = User(username, "", bio, uid)
                                usersList.add(user)
                                android.util.Log.d("UserCrudFragment", "User added: $username")
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("UserCrudFragment", "Error reading user", e)
                        }
                    }

                    android.util.Log.d("UserCrudFragment", "Total users loaded: ${usersList.size}")

                    if (usersList.isEmpty()) {
                        textViewEmpty.visibility = View.VISIBLE
                        recyclerViewUsers.visibility = View.GONE
                    } else {
                        textViewEmpty.visibility = View.GONE
                        recyclerViewUsers.visibility = View.VISIBLE

                        // Critical: Post to message queue to ensure view is laid out
                        recyclerViewUsers.post {
                            userAdapter.notifyDataSetChanged()
                            android.util.Log.d("UserCrudFragment", "Adapter notified - Item count: ${userAdapter.itemCount}")
                            android.util.Log.d("UserCrudFragment", "RecyclerView dimensions: ${recyclerViewUsers.width}x${recyclerViewUsers.height}")
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    progressBar.visibility = View.GONE
                    textViewEmpty.visibility = View.VISIBLE
                    recyclerViewUsers.visibility = View.GONE
                    android.util.Log.e("UserCrudFragment", "Firebase error: ${error.message}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to load users: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        } catch (e: Exception) {
            android.util.Log.e("UserCrudFragment", "Exception in loadUsers", e)
            progressBar.visibility = View.GONE
            textViewEmpty.visibility = View.VISIBLE
            Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when fragment becomes visible in ViewPager
        if (::userAdapter.isInitialized && usersList.isNotEmpty()) {
            userAdapter.notifyDataSetChanged()
            android.util.Log.d("UserCrudFragment", "onResume: notified adapter")
        }
    }

    private fun showEditDialog(user: User) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_user, null)

        val editTextUsername = dialogView.findViewById<TextInputEditText>(R.id.editTextUsername)
        val editTextBio = dialogView.findViewById<TextInputEditText>(R.id.editTextBio)
        val buttonCancel = dialogView.findViewById<Button>(R.id.buttonCancel)
        val buttonSave = dialogView.findViewById<Button>(R.id.buttonSave)

        editTextUsername.setText(user.getUsername())
        editTextBio.setText(user.getBio())

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        buttonSave.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val bio = editTextBio.text.toString().trim()

            if (username.isEmpty()) {
                Toast.makeText(requireContext(), "Username is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            updateUser(user.getUID(), username, bio, dialog)
        }

        dialog.show()
    }

    private fun updateUser(uid: String, username: String, bio: String, dialog: AlertDialog) {
        val userMap = HashMap<String, Any>()
        userMap["username"] = username
        userMap["bio"] = bio

        usersRef.child(uid).updateChildren(userMap)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User updated successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                loadUsers() // Reload to reflect changes
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to update user: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun showDeleteConfirmation(user: User) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete User")
            .setMessage("Are you sure you want to delete ${user.getUsername()}? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                deleteUser(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteUser(user: User) {
        usersRef.child(user.getUID()).removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "User deleted successfully", Toast.LENGTH_SHORT).show()
                userAdapter.removeUser(user)

                if (usersList.isEmpty()) {
                    textViewEmpty.visibility = View.VISIBLE
                    recyclerViewUsers.visibility = View.GONE
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    requireContext(),
                    "Failed to delete user: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}