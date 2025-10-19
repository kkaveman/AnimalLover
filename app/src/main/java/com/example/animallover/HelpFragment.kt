package com.example.animallover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.animallover.adapter.FaqAdapter
import com.example.animallover.databinding.FragmentHelpBinding
import com.example.animallover.model.Faq

class HelpFragment : Fragment() {

    private var _binding: FragmentHelpBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHelpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val faqList = listOf(
            Faq("How do I reset my password?", "You can reset your password by going to the settings page and clicking on 'Reset Password'."),
            Faq("How do I change my email address?", "You can change your email address in the account settings."),
            Faq("Is my data secure?", "We take data security very seriously. Please read our privacy policy for more information."),
            Faq("How do I delete my account?", "You can delete your account from the settings page. This action is irreversible."),
            Faq("How to contact support?", "You can contact us via email at support@animallover.com or call us at +123456789")
        )

        val adapter = FaqAdapter(faqList)
        binding.faqRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.faqRecyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}