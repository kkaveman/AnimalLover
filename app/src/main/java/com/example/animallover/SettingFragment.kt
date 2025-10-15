package com.example.animallover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.example.animallover.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        // You would typically load and apply the user's saved settings here
    }

    private fun setupListeners() {
        // Notifications Switch
        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Notifications Enabled", Toast.LENGTH_SHORT).show()
                // TODO: Add logic to enable notifications
            } else {
                Toast.makeText(requireContext(), "Notifications Disabled", Toast.LENGTH_SHORT).show()
                // TODO: Add logic to disable notifications
            }
        }

        // Data Saver Switch
        binding.switchDataSaver.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(requireContext(), "Data Saver Enabled", Toast.LENGTH_SHORT).show()
                // TODO: Add logic to reduce data consumption
            } else {
                Toast.makeText(requireContext(), "Data Saver Disabled", Toast.LENGTH_SHORT).show()
                // TODO: Add logic for normal data consumption
            }
        }

        // Dark Mode Toggle Group
        binding.toggleDarkMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val mode = when (checkedId) {
                    R.id.button_light_mode -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.button_dark_mode -> AppCompatDelegate.MODE_NIGHT_YES
                    R.id.button_system_mode -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> return@addOnButtonCheckedListener
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }

        // Language Setting
        binding.labelLanguage.setOnClickListener {
            Toast.makeText(requireContext(), "Language selection clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to a language selection screen
        }

        // Help & Support Setting
        binding.labelHelp.setOnClickListener {
            Toast.makeText(requireContext(), "Help & Support clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to a help screen or open a support web page
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
