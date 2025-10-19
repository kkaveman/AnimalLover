package com.example.animallover.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.animallover.UserCrudFragment
// Import your EventCrudFragment here

class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Fragment() // Replace with your EventCrudFragment when ready
            1 -> UserCrudFragment()
            else -> Fragment()
        }
    }
}