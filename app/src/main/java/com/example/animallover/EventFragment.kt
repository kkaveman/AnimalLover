package com.example.animallover

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.animallover.data.model.Event
import com.example.animallover.databinding.FragmentEventBinding
import com.example.animallover.ui.adapters.EventAdapter

class EventFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var _binding: FragmentEventBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentEventBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupEventRecyclerView()
    }

    private fun setupEventRecyclerView() {
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val events = listOf(
            Event(R.drawable.ai_cat, "Cat Show 2024", "Who has the best Cat?","23 February 2026"),
            Event(R.drawable.cat_event_placeholder, "Adoption Day", "Lets Adopt Cats","11 January 2026"),
            Event(R.drawable.cat_event_placeholder, "Feline Health Webinar","lets Care for our cats","18 July 2026"),
            Event(R.drawable.cat_event_placeholder, "Cute Cat Contest","Which is the cutest cat?","16 August 2026"),
            Event(R.drawable.cat_event_placeholder, "Community Meetup","lets meetup!","12 December 2026")
        )

        val eventAdapter = EventAdapter(events)

        binding.eventRecyclerView.adapter = eventAdapter
    }

//    private fun createSampleEvents(): List<Event> {
//        return listOf(
//            Event(R.drawable.ai_cat, "Cat Show 2024"),
//            Event(R.drawable.cat_event_placeholder, "Adoption Day"),
//            Event(R.drawable.cat_event_placeholder, "Feline Health Webinar"),
//            Event(R.drawable.cat_event_placeholder, "Cute Cat Contest"),
//            Event(R.drawable.cat_event_placeholder, "Community Meetup")
//        )
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding
    }

//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment EventFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            EventFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
}