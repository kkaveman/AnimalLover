package com.example.animallover

import android.app.ProgressDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException


class LoginFragment : Fragment() {

    private lateinit var progressDialog: ProgressDialog

    private var _binding: com.example.animallover.databinding.FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = com.example.animallover.databinding.FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Hide UI elements immediately
        hideUIElements()

        // Initialize progress dialog
        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Signing In")
            setMessage("Please wait...")
            setCanceledOnTouchOutside(false)
        }

        // Go to Register button
        binding.buttonGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        // Login button
        binding.buttonLogin.setOnClickListener {
            loginUser()
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
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.GONE
    }

    private fun showUIElements() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility = View.VISIBLE
    }

    private fun loginUser() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        when {
            TextUtils.isEmpty(email) -> {
                Toast.makeText(requireContext(), "Email is required", Toast.LENGTH_SHORT).show()
            }
            TextUtils.isEmpty(password) -> {
                Toast.makeText(requireContext(), "Password is required", Toast.LENGTH_SHORT).show()
            }
            else -> {
                progressDialog.show()

                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        // Login successful!
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Welcome back!", Toast.LENGTH_SHORT).show()

                        // Navigate to home screen (e.g., MainActivity or HomeFragment)
                        // Adjust destination based on your nav graph
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                    .addOnFailureListener { exception ->
                        progressDialog.dismiss()

                        when (exception) {
                            is FirebaseAuthInvalidCredentialsException -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Invalid email or password",
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            is FirebaseAuthUserCollisionException -> {
                                Toast.makeText(
                                    requireContext(),
                                    "This email is already registered. Try logging in.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            else -> {
                                Toast.makeText(
                                    requireContext(),
                                    "Login failed: ${exception.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        showUIElements()
    }
}