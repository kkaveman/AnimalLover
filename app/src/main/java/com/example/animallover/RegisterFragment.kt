package com.example.animallover

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.animallover.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Registering Account")
            setMessage("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        // Go to Login button
        binding.buttonGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        // Register button
        binding.buttonRegister.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val username = binding.editTextUsername.text.toString()
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        when {
            TextUtils.isEmpty(username) -> Toast.makeText(
                requireContext(),
                "Username is required",
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(email) -> Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_LONG)
                .show()

            TextUtils.isEmpty(password) -> Toast.makeText(
                requireContext(),
                "Password is required",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                progressDialog.show()

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        // Authentication was successful, now save user information to the database.
                        val currentUserId = authResult.user!!.uid
                        val usersRef = FirebaseDatabase.getInstance("https://cat-app-4922a-default-rtdb.asia-southeast1.firebasedatabase.app").reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["uid"] = currentUserId
                        userMap["username"] = username
                        userMap["email"] = email
                        userMap["bio"] = "Hi! i am yet another a cat lover! meow."
                        // The 'image' field is intentionally left out.
                        // The app will load a local drawable if this field is missing from the database.

                        usersRef.child(currentUserId).setValue(userMap)
                            .addOnSuccessListener {
                                // Database write was successful.
                                progressDialog.dismiss()
                                Toast.makeText(requireContext(), "Account created.", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                            }
                            .addOnFailureListener { e ->
                                // Database write failed.
                                progressDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Failed to save user info: ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                                // Optional: Clean up by deleting the newly created user if database write fails.
                                authResult.user?.delete()
                            }
                    }
                    .addOnFailureListener { e ->
                        // Authentication failed.
                        progressDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Account creation failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
